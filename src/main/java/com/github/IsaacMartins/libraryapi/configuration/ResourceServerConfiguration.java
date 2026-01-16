package com.github.IsaacMartins.libraryapi.configuration;

import com.github.IsaacMartins.libraryapi.security.JwtCustomAuthenticationFilter;
import com.github.IsaacMartins.libraryapi.security.SocialLoginSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * <b>securedEnabled = true:</b> Ativa o suporte para a anotação @Secured do Spring. Isso permite que você especifique uma lista de funções que um usuário deve ter para acessar um metodo, como por exemplo @Secured({"ROLE_ADMIN", "ROLE_USER"}) [1].
 * <br>
 * <br>
 * <b>jsr250Enabled = true:</b> Ativa o suporte para as anotações de segurança baseadas em padrões JSR-250 (Java Specification Requests). As principais anotações que isso habilita são:
 * @RolesAllowed: Semelhante a @Secured, define quais funções podem acessar o metodo [1].
 * @DenyAll: Impede o acesso ao metodo, independentemente das funções do usuário.
 * @PermitAll: Permite acesso irrestrito ao metodo.
 */

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true) // @EnableMethodSecurity Habilita @PreAuthorize
public class ResourceServerConfiguration {

    @Bean
    @Order(2)
    public SecurityFilterChain resourceServerSecurityFilterChain(HttpSecurity http,
                                                                 SocialLoginSuccessHandler successHandler,
                                                                 JwtCustomAuthenticationFilter jwtCustomAuthenticationFilter) throws Exception {
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
                .oauth2ResourceServer(oauth2RS -> oauth2RS.jwt(Customizer.withDefaults()))
                // adiciona filtro customizado no JwtAuthenticationToken (Authentication) gerado pelo BearerTokenAuthenticationFilter
                // a partir do token JWT (Bearer token) recebido.
                // importante: Esses dois filtros são chamados mesmo não tendo um token na requisição e uma CustomAuthentication já tendo sido
                // instanciada, logo eles verificam se a authentication é uma instancia de custom para não realizar nada caso seja.
                .addFilterAfter(jwtCustomAuthenticationFilter, BearerTokenAuthenticationFilter.class)
                .build();
    }

    //remove o prefixo 'ROLE_' das verificações de autorização
    @Bean
    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("");
    }

    //remove o prefixo 'SCOPE_' das verificações de autorização do Token JWT
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        var authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthorityPrefix("");

        var converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);

        return converter;
    }
}
