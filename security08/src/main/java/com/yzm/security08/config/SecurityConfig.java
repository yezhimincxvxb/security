package com.yzm.security08.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@Slf4j
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;

    public SecurityConfig(@Qualifier("jwtUserDetailsServiceImpl") UserDetailsService userDetailsService) {
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


//    /**
//     * 配置用户
//     * 指定默认从哪里获取认证用户的信息，即指定一个UserDetailsService接口的实现类
//     */
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        // 从数据库读取用户、并使用密码编码器解密
//        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
//    }

    //配置资源权限规则
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // 关闭CSRF
                .sessionManagement().disable() //禁用session
                .formLogin().disable() //禁用form登录

                // 添加token认证
                .apply(new JwtAuthenticationConfigurer(userDetailsService, passwordEncoder()))
                .and()
                // 添加token授权
                .apply(new JwtAuthorizationConfigurer(userDetailsService))
                .and()

                .exceptionHandling()
                .accessDeniedHandler(new JwtAccessDeniedHandler())
                .and()

                // 退出登录
                .logout()
                .permitAll()
                .and()

                // 访问路径URL的授权策略，如注册、登录免登录认证等
                .authorizeRequests()
                .antMatchers("/", "/home", "/register", "/login").permitAll() //指定url放行
                .anyRequest().authenticated() //其他任何请求都需要身份认证
        ;
    }

}
