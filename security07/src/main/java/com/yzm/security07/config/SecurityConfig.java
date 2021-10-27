package com.yzm.security07.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
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
        // 从数据库获取用户
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    //配置资源权限规则
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // 关闭CSRF跨域
                .csrf().disable()
                // addFilterAfter 在过滤链中的指定Filter(第二个参数)之后，添加Filter
                .addFilterAfter(new SmsAuthenticationFilter(authenticationManagerBean()), UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(new SmsAuthenticationProvider(userDetailsService))

                // 登录
                .formLogin()
                .loginPage("/auth/login") //指定登录页的路径，默认/login
                .loginProcessingUrl("/login") //指定自定义form表单请求的路径(必须跟login.html中的form action=“url”一致)
                .defaultSuccessUrl("/home", true) // 登录成功后的跳转url地址
                .failureUrl("/auth/login?error") // 登录失败后的跳转url地址
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
                .antMatchers("/sms/code").permitAll() //获取短信验证码
                .anyRequest().authenticated() //其他任何请求都需要身份认证
        ;
    }

}
