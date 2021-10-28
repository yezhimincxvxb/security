package com.yzm.security08.config;

import com.yzm.common.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JwtFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.info("登录失败：" + exception.getMessage());
        String msg = "登录失败";
        if (exception instanceof BadCredentialsException || exception instanceof UsernameNotFoundException) {
            msg = "用户名或密码错误";
        } else if (exception instanceof LockedException) {
            msg = "账号被锁定";
        } else if (exception instanceof AccountExpiredException) {
            msg = "账号过期";
        } else if (exception instanceof CredentialsExpiredException) {
            msg = "凭证过期";
        } else if (exception instanceof DisabledException) {
            msg = "账号被禁用";
        }
        HttpUtils.errorWrite(response, msg);
    }
}
