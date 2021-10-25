package com.yzm.security05.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 退出登录成功后进行业务处理
 */
@Slf4j
public class SecLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof User) {
                String username = ((User) principal).getUsername();
                log.info("退出成功，用户名：{}", username);
            }
        }
        // 重定向到登录页
        response.sendRedirect(request.getContextPath() + "/auth/login?logout");
    }

}
