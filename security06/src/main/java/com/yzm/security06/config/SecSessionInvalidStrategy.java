package com.yzm.security06.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.session.InvalidSessionStrategy;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Session 无效
 */
@Slf4j
public class SecSessionInvalidStrategy implements InvalidSessionStrategy {

    private final boolean createNewSession;

    public SecSessionInvalidStrategy() {
        this.createNewSession = true;
    }

    public SecSessionInvalidStrategy(boolean createNewSession) {
        this.createNewSession = createNewSession;
    }

    @Override
    public void onInvalidSessionDetected(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        log.info("Session 过期了");

        if (this.createNewSession) {
            request.getSession();
        }

        response.sendRedirect(request.getContextPath() + "/auth/login?invalid");
    }

}
