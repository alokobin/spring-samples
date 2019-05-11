package com.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SampleController {

    @GetMapping("/test")
    public String saySomething() {
        return "test";
    }

    @GetMapping("/hello")
    public String sayHello() {
        return "hello";
    }

    @GetMapping(value = "/")
    public String homepage() {
        return "index";
    }


}
