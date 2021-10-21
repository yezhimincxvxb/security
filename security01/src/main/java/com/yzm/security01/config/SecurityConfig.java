package com.yzm.security01.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 密码编码器
     * passwordEncoder.encode是用来加密的,passwordEncoder.matches是用来解密的
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置用户，这里是创建内存用户
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 从内存创建用户
        auth.inMemoryAuthentication()
                .withUser("admin")
                // 密码需要加密，不加就提示错误
                .password(passwordEncoder().encode("123456"))
                // 基于内存创建的用户不能同时使用roles和authorities，如果同时使用只有后面的生效，这个坑
                //.roles("ADMIN", "USER")
                .authorities("admin:select", "admin:delete")
                .and()
                .withUser("yzm")
                .password(passwordEncoder().encode("123456"))
                //.roles("USER")
                .authorities("user:select")
        ;

    }

    /**
     * http安全配置
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // 关闭CSRF跨域
                .csrf().disable()

                // 默认登录
                .formLogin().permitAll()
                .and()

                // 退出登录
                .logout().permitAll()
                .and()

                // 访问路径URL的授权策略，如注册、登录免登录认证等
                .authorizeRequests()
                .antMatchers("/home", "/").permitAll() //指定url放行

                .antMatchers("/user/select").hasAuthority("user:select")
                .antMatchers("/user/delete").hasAuthority("user:delete")
                .antMatchers("/admin/select").hasAuthority("admin:select")
                .antMatchers("/admin/delete").hasAnyAuthority("admin:delete", "admin:remove") // 需要权限(二选一)

//                .antMatchers("/user/**").hasAnyRole("ADMIN", "USER") // 需要角色(二选一)
//                .antMatchers("/admin/**").hasRole("ADMIN") // 需要角色
                .anyRequest().authenticated() //其他任何请求都需要身份认证
                .and()

        ;
    }

}
