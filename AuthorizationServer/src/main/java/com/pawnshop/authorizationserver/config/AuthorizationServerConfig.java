package com.pawnshop.authorizationserver.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.pawnshop.authorizationserver.entity.OAuth2Client;
import com.pawnshop.authorizationserver.repository.OAuth2ClientRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;
import reactor.core.publisher.Mono;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.Arrays;
import java.util.UUID;

@Configuration
@EnableWebFluxSecurity
public class AuthorizationServerConfig {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE) // Устанавливаем приоритет для фильтров безопасности OAuth2
    public SecurityWebFilterChain authorizationServerSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/oauth/**").permitAll()
                        .anyExchange().authenticated()
                )
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // Отключение CSRF
                .build();
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository(OAuth2ClientRepository clientRepository) {
        return new RegisteredClientRepository() {
            @Override
            public void save(RegisteredClient registeredClient) {
                OAuth2Client client = new OAuth2Client();
                client.setClientId(registeredClient.getClientId());
                client.setClientSecret(registeredClient.getClientSecret());
                client.setRedirectUris(String.join(",", registeredClient.getRedirectUris()));
                client.setScopes(String.join(",", registeredClient.getScopes()));
                client.setGrantTypes(String.join(",", registeredClient.getAuthorizationGrantTypes()
                        .stream().map(AuthorizationGrantType::getValue).toList()));
                clientRepository.save(client).block();
            }

            @Override
            public RegisteredClient findById(String id) {
                return clientRepository.findByClientId(id)
                        .map(client -> {
                            RegisteredClient.Builder builder = RegisteredClient.withId(client.getClientId())
                                    .clientId(client.getClientId())
                                    .clientSecret(client.getClientSecret())
                                    .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);

                            Arrays.stream(client.getGrantTypes().split(","))
                                    .map(AuthorizationGrantType::new)
                                    .forEach(builder::authorizationGrantType);

                            builder.redirectUris(uris -> uris.addAll(Arrays.asList(client.getRedirectUris().split(","))));
                            builder.scopes(scopes -> scopes.addAll(Arrays.asList(client.getScopes().split(","))));

                            builder.clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                                    .tokenSettings(TokenSettings.builder()
                                            .accessTokenTimeToLive(Duration.ofHours(1))
                                            .refreshTokenTimeToLive(Duration.ofDays(30))
                                            .build());

                            return builder.build();
                        })
                        .switchIfEmpty(Mono.empty())
                        .block();
            }

            @Override
            public RegisteredClient findByClientId(String clientId) {
                return findById(clientId);
            }
        };
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();

        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }
}
