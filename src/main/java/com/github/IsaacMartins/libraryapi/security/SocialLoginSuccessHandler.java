package com.github.IsaacMartins.libraryapi.security;

import com.github.IsaacMartins.libraryapi.model.entities.UserEntity;
import com.github.IsaacMartins.libraryapi.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SocialLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private static final String DEFAULT_PASSWORD = "123";
    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {

        OAuth2AuthenticationToken oAuth2AuthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = oAuth2AuthToken.getPrincipal();

        String email = oAuth2User.getAttribute("email");

        UserEntity user = userService.findByEmail(email);

        if(user == null) {
            user = saveTransientUser(email);
        }

        authentication = new CustomAuthentication(user);

        // Set da custom authentication gerada a partir de login social no contexto de segurança da aplicação. Necessário por não ser uma classe que
        // implementa AuthenticationProvider
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // finaliza o processo de login, realizando o comportamento padrão após login
        super.onAuthenticationSuccess(request, response, authentication);
    }

    private UserEntity saveTransientUser(String email) {

        UserEntity user = new UserEntity();

        user.setEmail(email);
        user.setLogin(getLoginFromEmail(email));
        user.setPassword(DEFAULT_PASSWORD);
        user.setRoles(List.of("OPERADOR"));

        userService.save(user);

        return user;
    }

    private static String getLoginFromEmail(String email) {
        //tambem poderia ser: return email.substring(0, email.indexOf("@"));
        return email.split("@")[0];
    }
}
