package com.example.reminder.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class TestController {

    @GetMapping("/admin/hello")
    public String adminHello() {
        return "Hello ADMIN";
    }

    @GetMapping("/user/hello")
    public String userHello() {
        return "Hello USER";
    }
}