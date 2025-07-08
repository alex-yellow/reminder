package com.example.reminder.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    // Публичный эндпоинт - доступен всем
    @GetMapping("/public/hello")
    public String publicHello() {
        return "Hello, this is a public endpoint!";
    }

    // Защищенный эндпоинт - требует аутентификации
    @GetMapping("/hello")
    public String hello() {
        return "Hello, this is a secured endpoint!";
    }
}