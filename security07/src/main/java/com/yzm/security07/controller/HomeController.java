package com.yzm.security07.controller;


import com.yzm.common.entity.HttpResult;
import com.yzm.common.utils.HttpUtils;
import com.yzm.security07.entity.User;
import com.yzm.security07.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
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

}
