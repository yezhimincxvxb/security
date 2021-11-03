package com.yzm.security09.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;

    public SecurityConfig(@Qualifier("secUserDetailsServiceImpl") UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * 密码编码器
     * passwordEncoder.encode是用来加密的,passwordEncoder.matches是用来解密的
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置用户
     * 指定默认从哪里获取认证用户的信息，即指定一个UserDetailsService接口的实现类
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 从数据库读取用户、并使用密码编码器解密
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    /**
     * 注入验证码servlet
     * servlet可拦截指定url路径，添加自定义操作
     */
    @Bean
    public ServletRegistrationBean<VerifyServlet> initServletRegistrationBean() {
        return new ServletRegistrationBean<>(new VerifyServlet(),"/auth/getVerifyCode");
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    //配置资源权限规则
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        UsernamePasswordCodeAuthenticationFilter codeAuthenticationFilter = new UsernamePasswordCodeAuthenticationFilter(authenticationManagerBean());
        codeAuthenticationFilter.setAuthenticationSuccessHandler(new LoginSuccessHandler());
        codeAuthenticationFilter.setAuthenticationFailureHandler(new LoginFailureHandler());
        http
                // 关闭CSRF跨域
                .csrf().disable()
                .addFilterAt(codeAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // 登录
                .formLogin()
                .loginPage("/auth/login") //指定登录页的路径，默认/login
                .loginProcessingUrl("/login") //指定自定义form表单请求的路径(必须跟login.html中的form action=“url”一致)
                .permitAll()
                .and()

                .exceptionHandling()
                .accessDeniedPage("/401") // 拒接访问跳转页面
                .and()

                // 退出登录
                .logout()
                .permitAll()
                .and()

                // 访问路径URL的授权策略，如注册、登录免登录认证等
                .authorizeRequests()
                .antMatchers("/", "/home", "/register", "/auth/login").permitAll() //指定url放行
                .antMatchers("/auth/getVerifyCode").permitAll() //放行验证码请求
                .anyRequest().authenticated() //其他任何请求都需要身份认证
        ;
    }

}
