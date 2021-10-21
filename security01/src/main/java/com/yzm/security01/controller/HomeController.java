package com.yzm.security01.controller;


import com.alibaba.fastjson.JSONObject;
import com.yzm.common.entity.HttpResult;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    @GetMapping(value = {"/", "/home"})
    public Object home() {
        return "home";
    }

    @GetMapping("/hello")
    @ResponseBody
    public Object hello() {
        return "hello";
    }

    // 通过authentication或userDetails获取当前登录用户信息+
    @GetMapping(value = {"/user", "/admin"})
    @ResponseBody
    public String info(Authentication authentication, @AuthenticationPrincipal UserDetails userDetails) {
        HttpResult result = HttpResult.ok(authentication);
        System.out.println("authentication ：");
        System.out.println(JSONObject.toJSONString(result, true));
        result.setData(userDetails);
        System.out.println("userDetails ：");
        System.out.println(JSONObject.toJSONString(result, true));
        return "请求成功";
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
