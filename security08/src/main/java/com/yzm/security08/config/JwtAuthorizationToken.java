package com.yzm.security08.config;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthorizationToken extends UsernamePasswordAuthenticationToken {

    public JwtAuthorizationToken(Object principal, Object credentials) {
        super(principal, credentials);
    }

    public JwtAuthorizationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }
}
