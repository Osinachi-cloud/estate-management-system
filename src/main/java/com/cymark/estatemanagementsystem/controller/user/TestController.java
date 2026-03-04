package com.cymark.estatemanagementsystem.controller.user;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.cymark.estatemanagementsystem.util.Constants.BASE_URL;

@RestController
@RequestMapping(BASE_URL)
public class TestController {
    @GetMapping("/test")
    public String test(){
        return "test";
    }
}
