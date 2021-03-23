package com.nexiles.example.gatewayrsocketwebsocket.config;

public interface SecurityConstants {

    String ADMIN = "ADMIN";
    String LOTR = "MIDDLEEARTH";
    String GOT = "WESTEROS";

    String ROLE_PREFIX = "ROLE_";

    String ADMIN_ROLE = ROLE_PREFIX + ADMIN;
    String LOTR_ROLE = ROLE_PREFIX + LOTR;
    String GOT_ROLE = ROLE_PREFIX + GOT;

    String JWT_NAME_CLAIM = "preferred_username";
    String JWT_REALM_ACCESS_CLAIM = "realm_access";
    String JWT_ROLES_CLAIM = "roles";

}
