package com.github.IsaacMartins.libraryapi.security;

import com.github.IsaacMartins.libraryapi.model.entities.UserEntity;
import com.github.IsaacMartins.libraryapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAthenticationProvider implements AuthenticationProvider {

    private final UserService userService;
    private final PasswordEncoder encoder;

    @Override
    public @Nullable Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String login = authentication.getName();
        String typedPassword = authentication.getCredentials().toString();

        UserEntity findedUser = userService.findByLogin(login);

        if(findedUser == null) {
            getUsernameNotFoundException();
        }

        String encryptedPassword = findedUser.getPassword();

        //Ordem obrigatoria dos parametros: (CharSequence rawPassword, String encodedPassword) -> senha do input e senha criptografada vinda do DB
        boolean passwordMatches = encoder.matches(typedPassword, encryptedPassword);

        if(!passwordMatches) {
            getUsernameNotFoundException();
        }

        return new CustomAuthentication(findedUser);
    }

    private void getUsernameNotFoundException() {
        throw new UsernameNotFoundException("Incorrect username and/or password.");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(UsernamePasswordAuthenticationToken.class);
    }
}
