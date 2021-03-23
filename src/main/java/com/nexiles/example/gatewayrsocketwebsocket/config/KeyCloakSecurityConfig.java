package com.nexiles.example.gatewayrsocketwebsocket.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.rsocket.EnableRSocketSecurity;
import org.springframework.security.config.annotation.rsocket.RSocketSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.rsocket.core.PayloadSocketAcceptorInterceptor;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Slf4j
@Profile("keycloak")
@Configuration
@EnableWebFluxSecurity
@EnableRSocketSecurity
public class KeyCloakSecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         ReactiveClientRegistrationRepository clientRegistrationRepository) {

        log.info("Using KeyCloak as Authentication Provider");

        // Authenticate through configured OpenID Provider
        http.oauth2Login();

        http.oauth2ResourceServer().jwt();

        final OidcClientInitiatedServerLogoutSuccessHandler logoutSuccessHandler =
                new OidcClientInitiatedServerLogoutSuccessHandler(clientRegistrationRepository);
        logoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}/");
        http.logout(logout -> logout.logoutSuccessHandler(logoutSuccessHandler));

        http.csrf().disable();

        return http.authorizeExchange().anyExchange().authenticated().and().build();
    }

    @Bean
    public PayloadSocketAcceptorInterceptor authorization(RSocketSecurity security) {
        security.authorizePayload(authorize -> authorize.anyExchange().authenticated())
                .addPayloadInterceptor(RSocketConfig.payLoadInterceptor())
                .jwt(Customizer.withDefaults())
        ;
        return security.build();
    }

}
