package com.nexiles.example.gatewayrsocketwebsocket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.rsocket.EnableRSocketSecurity;
import org.springframework.security.config.annotation.rsocket.RSocketSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.messaging.handler.invocation.reactive.AuthenticationPrincipalArgumentResolver;
import org.springframework.security.rsocket.core.PayloadSocketAcceptorInterceptor;

@Configuration
@EnableRSocketSecurity
@EnableReactiveMethodSecurity
public class RSocketSecurityConfig {


    private static final String ADMIN = "ADMIN";
    private static final String LOTR = "MIDDLEEARTH";
    private static final String GOT = "WESTEROS";

    private static final String ROLE_PREFIX = "ROLE_";
    public static final String ADMIN_ROLE = ROLE_PREFIX + ADMIN;
    public static final String LOTR_ROLE = ROLE_PREFIX + LOTR;
    public static final String GOT_ROLE = ROLE_PREFIX + GOT;

    @Bean
    public RSocketMessageHandler messageHandler(RSocketStrategies strategies) {
        RSocketMessageHandler handler = new RSocketMessageHandler();
        handler.getArgumentResolverConfigurer().addCustomResolver(new AuthenticationPrincipalArgumentResolver());
        handler.setRSocketStrategies(strategies);
        return handler;
    }

    @Bean
    public MapReactiveUserDetailsService authentication() {
        UserDetails admin = User.builder()
                .username("admin")
                .password("admin")
                .roles(ADMIN)
                .passwordEncoder(encoder()::encode)
                .build();

        UserDetails frodo = User.builder()
                .username("frodo")
                .password("frodo")
                .roles(LOTR)
                .passwordEncoder(encoder()::encode)
                .build();

        UserDetails john = User.builder()
                .username("john")
                .password("john")
                .roles(GOT)
                .passwordEncoder(encoder()::encode)
                .build();

        return new MapReactiveUserDetailsService(admin, frodo, john);
    }

    @Bean
    public PayloadSocketAcceptorInterceptor authorization(RSocketSecurity security) {
        security.authorizePayload(authorize ->
                authorize.anyExchange().authenticated())
                .simpleAuthentication(Customizer.withDefaults());
        return security.build();
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

}
