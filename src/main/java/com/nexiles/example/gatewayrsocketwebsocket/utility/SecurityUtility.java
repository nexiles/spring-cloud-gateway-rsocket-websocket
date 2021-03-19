package com.nexiles.example.gatewayrsocketwebsocket.utility;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
public class SecurityUtility {

    public enum Provider {
        Unknown, SpringSecurity, KeyCloak
    }

    public Provider resolveSecurityProvider(Principal principal) {

        if (principal instanceof UsernamePasswordAuthenticationToken)
            return Provider.SpringSecurity;
        else if (principal instanceof OAuth2AuthenticationToken)
            return Provider.KeyCloak;

        return Provider.Unknown;
    }

    public String resolveUserName(Principal principal) {

        if (principal instanceof UsernamePasswordAuthenticationToken) {
            final UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
            final User user = (User) token.getPrincipal();
            return user.getUsername();
        } else if (principal instanceof OAuth2AuthenticationToken) {
            final OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) principal;
            final OAuth2User user = token.getPrincipal();
            return user.getName();
        }

        return null;
    }


}
