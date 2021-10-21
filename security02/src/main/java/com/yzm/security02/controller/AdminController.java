package com.yzm.security02.controller;


import com.yzm.common.utils.HttpUtils;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @GetMapping
    public void admin(@AuthenticationPrincipal UserDetails userDetails, HttpServletResponse response) throws IOException {
        HttpUtils.successWrite(response, userDetails);
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
