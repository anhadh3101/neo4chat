package com.example.controllers;

import com.example.dto.EditProfileRequest;
import com.example.dto.LoginRequest;
import com.example.dto.RegisterRequest;
import com.example.dto.UserSearchResult;
import com.example.model.User;
import com.example.service.UserService;
import com.example.service.Neo4jDriverService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private Neo4jDriverService neo4jDriverService;

    @GetMapping("/search")
    public List<UserSearchResult> searchUsers(@RequestParam String q) {
        return neo4jDriverService.searchUsers(q);
    }

    @PostMapping("/register")
    public User register(@RequestBody RegisterRequest req) {
        return userService.register(req);
    }

    @PostMapping("/login")
    public User login(@RequestBody LoginRequest req) {
        return userService.login(req);
    }

    @GetMapping("/{id}")
    public User getProfile(@PathVariable UUID id) {
        return userService.viewProfile(id);
    }

    @PutMapping("/{id}")
    public User editProfile(@PathVariable UUID id, @RequestBody EditProfileRequest req) {
        return userService.editProfile(id, req);
    }
}
