package com.nexiles.example.gatewayrsocketwebsocket.controller;

import com.nexiles.example.gatewayrsocketwebsocket.utility.SecurityUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SecurityController {

    @GetMapping("/provider")
    public SecurityUtility.Provider getSecurityProvider(@Value("${spring.profiles.active:}") List<String> profiles) {
        if (profiles != null && profiles.contains("keycloak"))
            return SecurityUtility.Provider.KeyCloak;
        return SecurityUtility.Provider.SpringSecurity;
    }

    @GetMapping("/security/authenticated")
    public Mono<String> authenticated() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getName);
    }

    @GetMapping("/security/jwt")
    public Mono<String> jwt() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .cast(OidcUser.class)
                .map(OidcUser::getIdToken)
                .map(OidcIdToken::getTokenValue);
    }

}
