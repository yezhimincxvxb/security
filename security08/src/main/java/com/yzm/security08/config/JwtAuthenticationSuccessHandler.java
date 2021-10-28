package com.yzm.security08.config;

import com.yzm.common.utils.HttpUtils;
import com.yzm.security08.utils.JwtUtils;
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
public class JwtAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        if (authentication instanceof JwtAuthenticationToken) {
            log.info("登录认证");
            JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) authentication;
            String username = (String) authenticationToken.getPrincipal();

            Map<String, Object> map = new HashMap<>();
            map.put(JwtUtils.USERNAME, username);
            String token = JwtUtils.generateToken(map);
            HttpUtils.successWrite(response, token);
        }
    }

}
