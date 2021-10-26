package com.yzm.security06.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

import java.io.IOException;

/**
 * 并发登录导致session失效
 */
@Slf4j
public class SecSessionExpiredStrategy implements SessionInformationExpiredStrategy {


    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException {
        log.info("session 并发失效");
        // 跳转html页面
        event.getResponse().sendRedirect(event.getRequest().getContextPath() + "/auth/login?expired");
    }

}
