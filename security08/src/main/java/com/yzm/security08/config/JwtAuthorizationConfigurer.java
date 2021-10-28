package com.yzm.security08.config;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.DefaultSecurityFilterChain;

public class JwtAuthorizationConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final UserDetailsService userDetailsService;

    public JwtAuthorizationConfigurer(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        JwtAuthorizationFilter jwtAuthorizationFilter = new JwtAuthorizationFilter(http.getSharedObject(AuthenticationManager.class));
        jwtAuthorizationFilter.setAuthenticationSuccessHandler(new JwtAuthorizationSuccessHandler());
        jwtAuthorizationFilter.setAuthenticationFailureHandler(new JwtFailureHandler());

        JwtAuthorizationProvider jwtAuthorizationProvider = new JwtAuthorizationProvider(userDetailsService);

        http
                .addFilterAfter(jwtAuthorizationFilter, JwtAuthenticationFilter.class)
                .authenticationProvider(jwtAuthorizationProvider);
    }

}
