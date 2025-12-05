package com.example.service;

import com.example.dto.EditProfileRequest;
import com.example.dto.LoginRequest;
import com.example.dto.RegisterRequest;
import com.example.model.User;
import com.example.repository.UserRepository;

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

        userRepository.createFollowsRelationshipByEmail(userEmail, followedUserEmail);

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

        userRepository.deleteFollowsRelationshipByEmail(userEmail, followedUserEmail);

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

    // UC-5: Follow by UserId
    @Transactional
    public void followUserByUserId(String userId, String followedUserId) {
        if (userId.equals(followedUserId)) {
            throw new RuntimeException("You cannot follow yourself");
        }

        if (!userRepository.existsByUserId(userId)) {
            throw new RuntimeException("User not found");
        }

        if (!userRepository.existsByUserId(followedUserId)) {
            throw new RuntimeException("Followed user not found");
        }

        userRepository.createFollowsRelationshipByUserId(userId, followedUserId);
    }

    // UC-6: Unfollow by UserId
    @Transactional
    public void unfollowUserByUserId(String userId, String followedUserId) {
        if (userId.equals(followedUserId)) {
            throw new RuntimeException("You cannot unfollow yourself");
        }

        if (!userRepository.existsByUserId(userId)) {
            throw new RuntimeException("User not found");
        }

        if (!userRepository.existsByUserId(followedUserId)) {
            throw new RuntimeException("Followed user not found");
        }

        if (!userRepository.followsByUserIdExists(userId, followedUserId)) {
            throw new RuntimeException("User does not follow this user");
        }

        userRepository.deleteFollowsRelationshipByUserId(userId, followedUserId);
    }
}
