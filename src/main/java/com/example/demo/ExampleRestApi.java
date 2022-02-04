package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class ExampleRestApi {

    @GetMapping("/hello")
    String hello() {
        return "hello world";
    }
    
}