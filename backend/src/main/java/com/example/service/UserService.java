package com.example.service;

import com.example.dto.EditProfileRequest;
import com.example.dto.LoginRequest;
import com.example.dto.RegisterRequest;
import com.example.model.User;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User register(RegisterRequest req) {
        if (userRepository.existsByUsername(req.username)) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(req.email)) {
            throw new RuntimeException("Email already exists");
        }

        User u = new User();
        u.setName(req.name);
        u.setEmail(req.email);
        u.setUsername(req.username);
        u.setPassword(passwordEncoder.encode(req.password));

        return userRepository.save(u);
    }

    public User login(LoginRequest req) {
        User user = userRepository.findByUsername(req.username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(req.password, user.getPassword())) {
            throw new RuntimeException("Wrong password");
        }

        return user;
    }

    public User viewProfile(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Not found"));
    }

    public User editProfile(Integer userId, EditProfileRequest req) {
        User user = viewProfile(userId);
        user.setName(req.name);
        user.setBio(req.bio);
        return userRepository.save(user);
    }

    // UC-5
    @Transactional
    public void followUser(String userEmail, String followedUserEmail) {
        if (userEmail.equals(followedUserEmail)) {
            throw new RuntimeException("You cannot follow yourself");
        }

        if (!userRepository.existsByEmail(userEmail)) {
            throw new RuntimeException("User not found");
        }

        if (!userRepository.existsByEmail(followedUserEmail)) {
            throw new RuntimeException("Followed user not found");
        }

        userRepository.createFollowsRelationship(userEmail, followedUserEmail);

    }

    // UC-6
    @Transactional
    public void unfollowUser(String userEmail, String followedUserEmail) {
        if (userEmail.equals(followedUserEmail)) {
            throw new RuntimeException("You cannot unfollow yourself");
        }

        if (!userRepository.existsByEmail(userEmail)) {
            throw new RuntimeException("User not found");
        }

        if (!userRepository.existsByEmail(followedUserEmail)) {
            throw new RuntimeException("Followed user not found");
        }

        if (!userRepository.followsByEmailExists(userEmail, followedUserEmail)) {
            throw new RuntimeException("User does not follow this user");
        }

        userRepository.deleteFollowsRelationship(userEmail, followedUserEmail);

    }
}

