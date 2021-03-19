package com.nexiles.example.gatewayrsocketwebsocket.config;

import org.springframework.security.config.web.server.ServerHttpSecurity;

public interface SecurityConstants {

    String ADMIN = "ADMIN";
    String LOTR = "MIDDLEEARTH";
    String GOT = "WESTEROS";

    String ROLE_PREFIX = "ROLE_";

    String ADMIN_ROLE = ROLE_PREFIX + ADMIN;
    String LOTR_ROLE = ROLE_PREFIX + LOTR;
    String GOT_ROLE = ROLE_PREFIX + GOT;

    static ServerHttpSecurity configureExchange(ServerHttpSecurity.AuthorizeExchangeSpec authorizeExchangeSpec) {
        return authorizeExchangeSpec.anyExchange().authenticated().and();
    }

}
