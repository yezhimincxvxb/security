package com.yzm.security04.controller;


import com.yzm.common.entity.HttpResult;
import com.yzm.common.utils.HttpUtils;
import com.yzm.security04.entity.User;
import com.yzm.security04.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class HomeController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public HomeController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
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

    @GetMapping("/anonymous")
    @PreAuthorize("isAnonymous()")
    public String anonymous() {
        return "anonymous";
    }

    @GetMapping("/authenticated")
    @PreAuthorize("isAuthenticated()")
    public String authenticated() {
        return "authenticated";
    }

    @GetMapping("/rememberMe")
    @PreAuthorize("isRememberMe()")
    public String rememberMe() {
        return "rememberMe";
    }

    @GetMapping("/fullyAuthenticated")
    @PreAuthorize("isFullyAuthenticated()")
    public String fullyAuthenticated() {
        return "fullyAuthenticated";
    }

}
