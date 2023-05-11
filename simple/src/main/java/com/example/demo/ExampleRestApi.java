package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class ExampleRestApi {

    @GetMapping("/hello")
    String hello() {
        log.info("hello");
        return "hello world";
    }

    @PostMapping("/check")
    String check(@RequestBody String content) {
        log.info("check: "+content);
        return content;
    }
    
}