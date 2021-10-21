package com.yzm.security02.controller;


import com.yzm.common.utils.HttpUtils;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping
    public void user(Authentication authentication, HttpServletResponse response) throws IOException {
        HttpUtils.successWrite(response, authentication);
    }

    @GetMapping("/select")
    public Object select() {
        return "Select";
    }

    @GetMapping("/create")
    public Object create() {
        return "Create";
    }

    @GetMapping("/update")
    public Object update() {
        return "Update";
    }

    @GetMapping("/delete")
    public Object delete() {
        return "Delete";
    }

}
