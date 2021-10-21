package com.yzm.security02.controller;


import com.yzm.common.entity.HttpResult;
import com.yzm.common.utils.HttpUtils;
import com.yzm.security02.entity.User;
import com.yzm.security02.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class HomeController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public HomeController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("auth/login")
    public String login() {
        return "login";
    }

    @GetMapping(value = {"/", "home"})
    public Object home() {
        return "home";
    }

    @GetMapping("401")
    public Object notRole() {
        return "401";
    }

    @PostMapping("register")
    @ResponseBody
    public Object register(@RequestParam String username, @RequestParam String password) {
        User user = new User();
        user.setUsername(username);
        // 密码加密
        user.setPassword(passwordEncoder.encode(password));
        userService.save(user);
        return HttpResult.ok("注册成功");
    }

    @GetMapping("/info")
    @ResponseBody
    public void info(HttpServletResponse response, @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        HttpUtils.successWrite(response, userDetails);
    }

    @GetMapping(value = {"/user/select", "/admin/select"})
    @ResponseBody
    public Object select() {
        return "Select";
    }

    @GetMapping(value = {"/user/create", "/admin/create"})
    @ResponseBody
    public Object create() {
        return "Create";
    }

    @GetMapping(value = {"/user/update", "/admin/update"})
    @ResponseBody
    public Object update() {
        return "Update";
    }

    @GetMapping(value = {"/user/delete", "/admin/delete"})
    @ResponseBody
    public Object delete() {
        return "Delete";
    }

}
