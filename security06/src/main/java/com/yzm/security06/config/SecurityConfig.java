package com.yzm.security06.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.FlushMode;
import org.springframework.session.Session;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableRedisHttpSession(redisNamespace = "spring:session:yzm", maxInactiveIntervalInSeconds = 200, flushMode = FlushMode.ON_SAVE)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final SecPermissionEvaluator permissionEvaluator;

    public SecurityConfig(@Qualifier("secUserDetailsServiceImpl") UserDetailsService userDetailsService, SecPermissionEvaluator permissionEvaluator) {
        this.userDetailsService = userDetailsService;
        this.permissionEvaluator = permissionEvaluator;
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
     * 注入自定义PermissionEvaluator
     */
    @Bean
    public DefaultWebSecurityExpressionHandler webSecurityExpressionHandler() {
        DefaultWebSecurityExpressionHandler handler = new DefaultWebSecurityExpressionHandler();
        handler.setPermissionEvaluator(permissionEvaluator);
        return handler;
    }

    /**
     * 会话注册表
     */
//    @Bean
//    public SessionRegistry sessionRegistry() {
//        return new SessionRegistryImpl();
//    }

    /**
     * 是spring session为Spring Security提供的,
     * 用于在集群环境下控制会话并发的会话注册表实现
     */
    @Autowired
    @Lazy
    private FindByIndexNameSessionRepository<? extends Session> sessionRepository;

    @Bean
    public SpringSessionBackedSessionRegistry sessionRegistry(){
        return new SpringSessionBackedSessionRegistry<>(sessionRepository);
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

    //配置资源权限规则
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // 关闭CSRF跨域
                .csrf().disable()

                // 登录
                .formLogin()
                .loginPage("/auth/login") //指定登录页的路径，默认/login
                .loginProcessingUrl("/login") //指定自定义form表单请求的路径(必须跟login.html中的form action=“url”一致)
                .defaultSuccessUrl("/home", true) // 登录成功后的跳转url地址
                .failureUrl("/auth/login?error") // 登录失败后的跳转url地址
                .permitAll()
                .and()

                // 记住我
                .rememberMe()
                .tokenValiditySeconds(120) // 有效时间，单位秒，默认30分钟
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
                .anyRequest().authenticated() //其他任何请求都需要身份认证
                .and()

                // session 管理
                .sessionManagement()
                // 1.session 超时，默认60秒，即60秒内无操作就会过期
                // 通过在yml里面设置 server.servlet.session.timeout=60
                //.invalidSessionUrl("/invalid")
                .invalidSessionStrategy(new SecSessionInvalidStrategy())
                // 2.session 并发控制：控制一个账号同一时刻最多能登录多少个
                .maximumSessions(1) // 限制最大登陆数
                // 当达到最大值时，是否阻止用户登录，false表示不阻止，那么新的会覆盖旧的，旧的被迫下载
                .maxSessionsPreventsLogin(false)
                //.expiredUrl("/invalid")
                .expiredSessionStrategy(new SecSessionExpiredStrategy()) // 当达到最大值时，旧用户被踢出后的操作
                // 3.手动使Session立即失效
                .sessionRegistry(sessionRegistry())
        ;
    }

}
