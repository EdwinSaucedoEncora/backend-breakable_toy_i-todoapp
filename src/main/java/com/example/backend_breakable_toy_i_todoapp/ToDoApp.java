package com.example.backend_breakable_toy_i_todoapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class ToDoApp {

    @RequestMapping("/")
    String home() {
        return "Hello Encora!";
    }

    public static void main(String[] args) {
        SpringApplication.run(ToDoApp.class, args);
    }

}