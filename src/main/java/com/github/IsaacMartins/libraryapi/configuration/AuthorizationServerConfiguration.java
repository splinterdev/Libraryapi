package com.github.IsaacMartins.libraryapi.configuration;

import com.github.IsaacMartins.libraryapi.security.CustomAuthentication;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Configuration
@EnableWebSecurity
public class AuthorizationServerConfiguration {

    @Bean
    @Order(1) // define a ordem de precedencia das SecurityFilterChains da aplicação.
              // Vai ser a primeira cadeia de filtros aplicada a requisição. Ficará na frente da primeira configurada no SecurityConfiguration.
    public SecurityFilterChain authServerSecurityFilterChain(HttpSecurity http) throws Exception {

/*
        Código a seguir no comentário é um metodo com Authorization Server (1.3.2) antes de ser movido para dentro do spring security.
        Spring Authorization Server foi movido para dentro do Spring Security a partir da versão 7.0 do Security. Authorization Server independente foi
        movido na versão 1.5.X, sendo a última geração. Novas features agora só são adicionadas dentro de Spring Security.

        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http); -> deprecated desde a versão de projeto separada do Authorization server 1.4, indisponivel

        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .oidc(Customizer.withDefaults());

        http.oauth2ResourceServer(oauth2Rs -> oauth2Rs.jwt(Customizer.withDefaults()));

        http.formLogin(configurer -> configurer.loginPage("/login"));

        return http.build();
*/

        // Maneira moderna - Spring Security 7.X

        return http
                .oauth2AuthorizationServer(authorizationServerConfigurer -> {
                    http.securityMatcher(authorizationServerConfigurer.getEndpointsMatcher()); // Define quais endPoints a request precisa ter para dar acesso ao authorization server
                    authorizationServerConfigurer.oidc(Customizer.withDefaults()); // ativa Open ID Connect (OIDC) -> Permite acesso as informações de Token (quem gerou, usuário, etc)
                })
                .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
                .oauth2ResourceServer(oauth2Rs -> oauth2Rs.jwt(Customizer.withDefaults()))
                .formLogin(configurer -> configurer.loginPage("/login"))
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public TokenSettings tokenSettings() {
        return TokenSettings.builder()
                .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED) // pesquisar sobre o que define a enum SELF_CONTAINED
                // access_token: utilizado nas requisições. Duração setada para 60 minutos
                .accessTokenTimeToLive(Duration.ofMinutes(60))
                // refresh_token: renova o access_token. Durante 90 minutos, o client pode requisitar um novo access_token com duração de 60
                .refreshTokenTimeToLive(Duration.ofMinutes(90))
                .build();
    }

    @Bean
    public ClientSettings  clientSettings() {
        return ClientSettings.builder()
                .requireProofKey(false)  // -> desativa PKCE (chave de prova que garante que, mesmo que o authorization code seja interceptado, ele não irá gerar tokens jwt pois apenas o client terá a proofKey)
                .requireAuthorizationConsent(false) // desabilita pedido de consentimento do acesso as informações da conta realizado pela API (igual pedido feito no login social google)
                .build();
    }

    // Gera Token JWK - JSON WEB KEY
    @Bean
    public JWKSource<SecurityContext> jwkSource() throws Exception {
        JWKSet jwkSet = new JWKSet(generateRSAKey());
        return new ImmutableJWKSet<>(jwkSet);
    }

    // Gera chave para o JWK, com algoritmo RSA key. São criadas duas chaves, uma pública e uma privada.
    // A chave privada gera a assinatura e a chave pública assina o token, assim a pública podendo (e devendo)
    // ser exposta para que outros serviços verifiquem a assinatura.
    private static RSAKey generateRSAKey() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048); // 2048 bits
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        return new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                .tokenEndpoint("/oauth2/token") // Obter token
                .tokenIntrospectionEndpoint("/oauth2/introspect") // para consultar status do token
                .tokenRevocationEndpoint("/oauth2/revoke") // revogar token
                .authorizationEndpoint("/oauth2/authorize") // endpoint de authorization
                .oidcUserInfoEndpoint("/oauth2/userinfo") // informações do usuário OPEN ID CONNECT
                .jwkSetEndpoint("/oauth2/jwks") // obter chave publica para verificar assinatura do token
                .oidcLogoutEndpoint("/oauth2/logout") // logout
                .build();
    }

    // Authorization Server ao gerar um token vai chamar este customizer e, caso seja um access token, ele irá customizar o token
    // de acordo com a definição de claims no context (.claim("roles", authoritiesList).claim(...) definido dentro desse bean
    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
        // JwtEncodingContext context é fornecido pelo authorization server
        return context -> {

            var principal = context.getPrincipal(); // Authentication

            if(principal instanceof CustomAuthentication customAuth) {

                OAuth2TokenType tokenType = context.getTokenType();

                if(OAuth2TokenType.ACCESS_TOKEN.equals(tokenType)) {

                    Collection<GrantedAuthority> authorities = customAuth.getAuthorities();

                    List<String> authoritiesList = authorities
                            .stream()
                            .map(GrantedAuthority::getAuthority) // Method reference. Mapeia cada GrantedAuthority para o seu respectivo metodo .getAuthority (retorna String com nome da role)
                            .toList();

                    context.getClaims()
                            .claim("authorities", authoritiesList)
                            .claim("email", customAuth.getUser().getEmail());
                }
            }
        };
    }
}
