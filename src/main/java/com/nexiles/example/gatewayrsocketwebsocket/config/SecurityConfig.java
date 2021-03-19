package com.nexiles.example.gatewayrsocketwebsocket.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Slf4j
@Profile("!keycloak")
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        log.info("Using SpringSecurity as Authentication Provider");
        return SecurityConstants.configureExchange(http.authorizeExchange()).httpBasic().and().build();
    }

    @Bean
    public MapReactiveUserDetailsService authentication() {
        UserDetails admin = User.builder()
                .username("admin")
                .password("admin")
                .roles(SecurityConstants.ADMIN)
                .passwordEncoder(encoder()::encode)
                .build();

        UserDetails frodo = User.builder()
                .username("frodo")
                .password("frodo")
                .roles(SecurityConstants.LOTR)
                .passwordEncoder(encoder()::encode)
                .build();

        UserDetails john = User.builder()
                .username("john")
                .password("john")
                .roles(SecurityConstants.GOT)
                .passwordEncoder(encoder()::encode)
                .build();

        return new MapReactiveUserDetailsService(admin, frodo, john);
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

}
