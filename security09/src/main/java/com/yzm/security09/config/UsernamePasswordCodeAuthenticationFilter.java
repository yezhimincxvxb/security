package com.yzm.security09.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 校验验证码、用户名密码
 */
@Slf4j
public class UsernamePasswordCodeAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    public UsernamePasswordCodeAuthenticationFilter() {
        super();
    }

    public UsernamePasswordCodeAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String inputVerify = request.getParameter("verifyCode");
        log.info("用户输入：" + inputVerify);
        //这个validateCode是在servlet中存入session的名字
        String validateCode = (String) request.getSession().getAttribute("validateCode");

        if (!validateCode.equalsIgnoreCase(inputVerify)) {
            throw new AuthenticationServiceException("验证码不一致");
        }

        String username = obtainUsername(request);
        String password = obtainPassword(request);
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            throw new AuthenticationServiceException("用户名密码错误");
        }

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
        this.setDetails(request, authToken);
        return this.getAuthenticationManager().authenticate(authToken);
    }

}
