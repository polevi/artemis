package com.mycompany.app;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class SimpleController {

    @GetMapping("/hello/{name}")
    public String sayHello(@PathVariable String name) {
        return String.format("Hello, %s !", name);
    }
}
