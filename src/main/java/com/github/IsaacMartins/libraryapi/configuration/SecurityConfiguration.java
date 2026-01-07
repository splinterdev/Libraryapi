package com.github.IsaacMartins.libraryapi.configuration;

import com.github.IsaacMartins.libraryapi.security.SocialLoginSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true) // Pesquisar JSR250 para colocar um comentario explicando aqui
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, SocialLoginSuccessHandler successHandler) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults())
                .formLogin(configurer -> {
                    configurer.loginPage("/login");
                })
                .authorizeHttpRequests((authorize -> {
                    authorize.requestMatchers("/login/**").permitAll();
                    authorize.requestMatchers(HttpMethod.POST, "/usuarios/**").permitAll();

                    // .anyRequest(); precisa ser o último metodo a ser chamado, pois anula todos os outros a seguir.
                    // Esta chamada define que qualquer outra requisição que não bata com requestMatcher pode ser acessada
                    // se pelo menos o usuário estiver autenticado
                    authorize.anyRequest().authenticated();
                }))
                .oauth2Login(oAuth2 -> {
                    // tratativa a ser realizada após login social (Ex: google) ser realizado com sucesso e retornar os dados do user
                    oAuth2.loginPage("/login")
                          .successHandler(successHandler);
                })
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

//    @Bean
//    public UserDetailsService userDetailsService(UserService userService) {
//        UserDetails user1 = User.builder()
//                .username("user")
//                .password(encoder.encode("123"))
//                .roles("USER")
//                .build();
//
//        UserDetails user2 = User.builder()
//                .username("admin")
//                .password(encoder.encode("321"))
//                .roles("ADMIN")
//                .build();
//
//        return new CustomUserDetailsService(userService);
//    }

    //remove o prefixo 'ROLE_' das verificações de autorização
    @Bean
    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("");
    }
}
