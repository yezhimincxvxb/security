package com.yzm.security06.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;

import java.io.IOException;

/**
 * Session 过期策略
 */
@Slf4j
public class SecSessionExpiredStrategy implements SessionInformationExpiredStrategy {


    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException {
        log.info("被迫下线");
        // 如果是跳转html页面，url代表跳转的地址
        event.getResponse().sendRedirect(event.getRequest().getContextPath() + "/auth/login?expired");
    }

}
