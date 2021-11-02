package com.yzm.security07.controller;


import com.yzm.security07.entity.User;
import com.yzm.security07.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
public class HomeController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public HomeController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/sms/code")
    public String sms(String mobile, HttpSession session) {
        int code = (int) Math.ceil(Math.random() * 9000 + 1000);

        Map<String, Object> map = new HashMap<>(16);
        map.put("mobile", mobile);
        map.put("code", code);

        session.setAttribute("smsCode", map);
        log.info("{}：为 {} 设置短信验证码：{}", session.getId(), mobile, code);
        return "redirect:/auth/login";
    }

    @GetMapping(value = {"/", "/home"})
    public String home(ModelMap map, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails == null ? null : userDetails.getUsername();
        map.addAttribute("username", username);
        return "home";
    }

    @GetMapping("/auth/login")
    public String login() {
        return "login";
    }

    @GetMapping("401")
    public Object notRole() {
        return "401";
    }

    @PostMapping("register")
    public Object register(@RequestParam String username, @RequestParam String password, ModelMap map) {
        User user = new User();
        user.setUsername(username);
        // 密码加密
        user.setPassword(passwordEncoder.encode(password));
        userService.save(user);
        map.addAttribute("user", user);
        return "home";
    }

}
