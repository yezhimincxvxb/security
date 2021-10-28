package com.yzm.security08.config;

import com.yzm.security08.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.www.NonceExpiredException;

public class JwtAuthorizationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;

    public JwtAuthorizationProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthorizationToken.class.isAssignableFrom(authentication);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtAuthorizationToken authorizationToken = (JwtAuthorizationToken) authentication;
        String token = (String) authorizationToken.getPrincipal();
        if (JwtUtils.isExpired(token)) throw new NonceExpiredException("Token is Expired");

        Claims claims = JwtUtils.verifyToken(token);
        String username = claims.getSubject();
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (userDetails == null) throw new UsernameNotFoundException("账号异常");

        return new JwtAuthorizationToken(token, null, userDetails.getAuthorities());
    }

}
