package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExampleEndpoint {

    @GetMapping(path = "/hello")
    public String helloworld() {
        return "hello";
    }  
    
}
