package com.example.service;

import com.example.dto.EditProfileRequest;
import com.example.dto.LoginRequest;
import com.example.dto.RegisterRequest;
import com.example.model.User;
import com.example.repository.UserRepository;
import java.util.UUID;


import java.util.List;

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
        u.setUserId(UUID.randomUUID().toString());
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

    public User viewProfile(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Not found"));
    }

    public User editProfile(String userId, EditProfileRequest req) {
        User user = viewProfile(userId);
        user.setName(req.name);
        user.setBio(req.bio);
        return userRepository.save(user);
    }

    // UC-5
    @Transactional
    public List<User> followUser(String userEmail, String followedUserEmail) {
        if (userEmail.equals(followedUserEmail)) {
            throw new RuntimeException("You cannot follow yourself");
        }

        User follower = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User followed = userRepository.findByEmail(followedUserEmail)
                .orElseThrow(() -> new RuntimeException("Followed user not found"));

        userRepository.createFollowsRelationshipByEmail(userEmail, followedUserEmail);
        return List.of(follower, followed);
    }

    // UC-6
    @Transactional
    public List<User> unfollowUser(String userEmail, String followedUserEmail) {
        if (userEmail.equals(followedUserEmail)) {
            throw new RuntimeException("You cannot unfollow yourself");
        }

        User follower = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User followed = userRepository.findByEmail(followedUserEmail)
                .orElseThrow(() -> new RuntimeException("Followed user not found"));

        if (!userRepository.followsByEmailExists(userEmail, followedUserEmail)) {
            throw new RuntimeException("User does not follow this user");
        }

        userRepository.deleteFollowsRelationshipByEmail(userEmail, followedUserEmail);
        return List.of(follower, followed);
    }

    // UC-7
    @Transactional
    public List<User> getFollowersByEmail(String userEmail) {
        if (!userRepository.existsByEmail(userEmail)) {
            throw new RuntimeException("User not found");
        }
        return userRepository.findFollowersByEmail(userEmail);
    }

    @Transactional
    public List<User> getFollowingByEmail(String userEmail) {
        if (!userRepository.existsByEmail(userEmail)) {
            throw new RuntimeException("User not found");
        }
        return userRepository.findFollowingByEmail(userEmail);
    }

    // UC-7: Get Followers by UserId
    @Transactional
    public List<User> getFollowersByUserId(String userId) {
        if (!userRepository.existsByUserId(userId)) {
            throw new RuntimeException("User not found");
        }
        return userRepository.findFollowersByUserId(userId);
    }

    // UC-7: Get Following by UserId
    @Transactional
    public List<User> getFollowingByUserId(String userId) {
        if (!userRepository.existsByUserId(userId)) {
            throw new RuntimeException("User not found");
        }
        return userRepository.findFollowingByUserId(userId);
    }

    // UC-8
    @Transactional
    public List<User> getMutualConnectionsByEmail(String userEmail, String otherUserEmail) {
        if (!userRepository.existsByEmail(userEmail)) {
            throw new RuntimeException("User not found");
        }
        if (!userRepository.existsByEmail(otherUserEmail)) {
            throw new RuntimeException("User not found");
        }

        return userRepository.findMutualConnectionsByEmail(userEmail, otherUserEmail);
    }

    // UC-8: Get Mutual Connections by UserId
    @Transactional
    public List<User> getMutualConnectionsByUserId(String userId, String otherUserId) {
        if (!userRepository.existsByUserId(userId)) {
            throw new RuntimeException("User not found");
        }
        if (!userRepository.existsByUserId(otherUserId)) {
            throw new RuntimeException("User not found");
        }

        return userRepository.findMutualConnectionsByUserId(userId, otherUserId);
    }

    // UC-5: Follow by UserId
    @Transactional
    public List<User> followUserByUserId(String userId, String followedUserId) {
        if (userId.equals(followedUserId)) {
            throw new RuntimeException("You cannot follow yourself");
        }

        User follower = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User followed = userRepository.findByUserId(followedUserId)
                .orElseThrow(() -> new RuntimeException("Followed user not found"));

        userRepository.createFollowsRelationshipByUserId(userId, followedUserId);
        return List.of(follower, followed);
    }

    // UC-6: Unfollow by UserId
    @Transactional
    public List<User> unfollowUserByUserId(String userId, String followedUserId) {
        if (userId.equals(followedUserId)) {
            throw new RuntimeException("You cannot unfollow yourself");
        }

        User follower = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User followed = userRepository.findByUserId(followedUserId)
                .orElseThrow(() -> new RuntimeException("Followed user not found"));

        if (!userRepository.followsByUserIdExists(userId, followedUserId)) {
            throw new RuntimeException("User does not follow this user");
        }

        userRepository.deleteFollowsRelationshipByUserId(userId, followedUserId);
        return List.of(follower, followed);
    }
}
