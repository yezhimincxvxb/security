    package com.yzm.security06.controller;


    import com.yzm.common.entity.HttpResult;
    import com.yzm.common.utils.HttpUtils;
    import com.yzm.security06.entity.User;
    import com.yzm.security06.service.UserService;
    import org.springframework.security.core.annotation.AuthenticationPrincipal;
    import org.springframework.security.core.session.SessionInformation;
    import org.springframework.security.core.session.SessionRegistry;
    import org.springframework.security.core.userdetails.UserDetails;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.RequestParam;
    import org.springframework.web.bind.annotation.RestController;

    import javax.servlet.http.HttpServletResponse;
    import java.io.IOException;
    import java.util.List;

    @RestController
    public class HomeController {

        private final UserService userService;
        private final PasswordEncoder passwordEncoder;
        private final SessionRegistry sessionRegistry;

        public HomeController(UserService userService, PasswordEncoder passwordEncoder,SessionRegistry sessionRegistry) {
            this.userService = userService;
            this.passwordEncoder = passwordEncoder;
            this.sessionRegistry = sessionRegistry;
        }

        @PostMapping("register")
        public Object register(@RequestParam String username, @RequestParam String password) {
            User user = new User();
            user.setUsername(username);
            // 密码加密
            user.setPassword(passwordEncoder.encode(password));
            userService.save(user);
            return HttpResult.ok("注册成功");
        }

        @GetMapping("/info")
        public void info(HttpServletResponse response, @AuthenticationPrincipal UserDetails userDetails) throws IOException {
            HttpUtils.successWrite(response, userDetails);
        }

        @GetMapping("/kick")
        public String removeUserSessionByUsername(@RequestParam String username) {
            int count = 0;

            // 获取session中所有的用户信息
            List<Object> users = sessionRegistry.getAllPrincipals(); // 获取所有 principal 信息
            for (Object principal : users) {
                if (principal instanceof UserDetails) {
                    String principalName = ((UserDetails) principal).getUsername();
                    if (principalName.equals(username)) {
                        // 参数二：是否包含过期的Session
                        List<SessionInformation> sessionsInfo = sessionRegistry.getAllSessions(principal, false);
                        if (null != sessionsInfo && sessionsInfo.size() > 0) {
                            for (SessionInformation sessionInformation : sessionsInfo) {
                                // 使 session 过期
                                sessionInformation.expireNow();
                                count++;
                            }
                        }
                    }
                }
            }
            return "操作成功，清理session共" + count + "个";
        }

    }
