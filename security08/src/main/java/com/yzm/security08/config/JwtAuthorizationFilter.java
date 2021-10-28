package com.yzm.security08.config;

import com.yzm.security08.utils.JwtUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.*;
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthorizationFilter extends OncePerRequestFilter {

    // 拦截header中带Authorization的请求
    private final RequestMatcher authorizationRequestMatcher = new RequestHeaderRequestMatcher("Authorization");
    private final AuthenticationManager authenticationManager;

    private AuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
    private AuthenticationFailureHandler failureHandler = new SimpleUrlAuthenticationFailureHandler();

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    protected boolean requiresAuthentication(HttpServletRequest request) {
        return authorizationRequestMatcher.matches(request);
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(authenticationManager, "authenticationManager must be specified");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        // 请求头是否携带 Authorization
        if (!requiresAuthentication(request)) {
            // 请求头没携带Authorization,可能是登录或匿名访问,直接放行,继续下一个过滤链
            chain.doFilter(request, response);
            return;
        }

        Authentication authResult = null;
        AuthenticationException failed = null;
        try {
            String token = JwtUtils.getTokenFromRequest(request);
            if (StringUtils.isNotBlank(token)) {
                JwtAuthorizationToken authorizationToken = new JwtAuthorizationToken(token, null);
                authorizationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                authResult = this.authenticationManager.authenticate(authorizationToken);
            } else {
                failed = new InsufficientAuthenticationException("JWT is Empty");
            }

        } catch (AuthenticationException e) {
            SecurityContextHolder.clearContext();
            failed = e;
        }

        if (authResult != null) {
            this.successfulAuthentication(request, response, authResult);
        } else {
            // 认证失败
            this.unsuccessfulAuthentication(request, response, failed);
            return;
        }

        // 放行,继续下一个过滤链
        chain.doFilter(request, response);
    }

    protected void unsuccessfulAuthentication(
            HttpServletRequest request, HttpServletResponse response, AuthenticationException failed)
            throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        failureHandler.onAuthenticationFailure(request, response, failed);
    }

    protected void successfulAuthentication(
            HttpServletRequest request, HttpServletResponse response, Authentication authResult)
            throws IOException, ServletException {
        SecurityContextHolder.getContext().setAuthentication(authResult);
        successHandler.onAuthenticationSuccess(request, response, authResult);
    }

    public void setAuthenticationSuccessHandler(AuthenticationSuccessHandler successHandler) {
        Assert.notNull(successHandler, "successHandler cannot be null");
        this.successHandler = successHandler;
    }

    public void setAuthenticationFailureHandler(AuthenticationFailureHandler failureHandler) {
        Assert.notNull(failureHandler, "failureHandler cannot be null");
        this.failureHandler = failureHandler;
    }

    protected AuthenticationSuccessHandler getSuccessHandler() {
        return this.successHandler;
    }

    protected AuthenticationFailureHandler getFailureHandler() {
        return this.failureHandler;
    }

}
