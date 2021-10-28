package com.yzm.security08.config;

import com.yzm.security08.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class JwtAuthorizationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        if (authentication instanceof JwtAuthorizationToken) {
            log.info("授权认证");
            JwtAuthorizationToken authorizationToken = (JwtAuthorizationToken) authentication;
            String token = (String) authorizationToken.getPrincipal();

            Claims claims = JwtUtils.verifyToken(token);
            long expiration = claims.getExpiration().getTime();
            // 当前时间加上刷新时间大于过期时间,则刷新token
            if (System.currentTimeMillis() + JwtUtils.TOKEN_REFRESH_TIME >= expiration) {
                String username = claims.getSubject();
                Map<String, Object> map = new HashMap<>();
                map.put(JwtUtils.USERNAME, username);
                token = JwtUtils.generateToken(map);
            }

            response.setHeader("Authorization", token);
        }
    }

}
