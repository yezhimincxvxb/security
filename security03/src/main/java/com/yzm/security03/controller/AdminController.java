package com.yzm.security03.controller;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {

    @GetMapping
    public Object admin(@AuthenticationPrincipal UserDetails userDetails) {
        return userDetails;
    }

    @GetMapping("/select")
    @PreAuthorize("hasPermission('admin','select')")
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
    @PreAuthorize("hasPermission('admin','delete')")
    public Object delete() {
        return "Delete";
    }

}
