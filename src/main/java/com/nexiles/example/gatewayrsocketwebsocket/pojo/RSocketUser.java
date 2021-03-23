package com.nexiles.example.gatewayrsocketwebsocket.pojo;

import com.nexiles.example.gatewayrsocketwebsocket.utility.SecurityUtility;
import lombok.Value;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

@Value
public class RSocketUser {

    String userName;

    SecurityUtility.Provider provider;

    Set<GrantedAuthority> authorities;

}
