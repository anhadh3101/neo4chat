package com.example.controllers;

import com.example.service.Neo4jDriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class DemoController {

    @Autowired
    private Neo4jDriverService neo4jDriverService;

    @GetMapping("/")
    public String home() {
        return "Hello, Spring Boot is running!";
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello World!";
    }

    @GetMapping("/api/status")
    public String status() {
        return "Application is running successfully!";
    }

    @GetMapping("/api/users/first10")
    public List<Map<String, Object>> getFirst10Users() {
        return neo4jDriverService.getFirst10Users();
    }
}

