package com.yzm.security05.controller;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@PreAuthorize("hasAnyRole('ADMIN','USER')")
public class UserController {

    @GetMapping("/select")
    @PreAuthorize("hasPermission('user','select')")
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
    @PreAuthorize("hasPermission('user','delete')")
    public Object delete() {
        return "Delete";
    }

}