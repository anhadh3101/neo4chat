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
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private Neo4jDriverService neo4jDriverService;

    @GetMapping("/search")
    public List<UserSearchResult> searchUsers(
            @RequestParam String q,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        return neo4jDriverService.searchUsers(q, limit, offset);
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
    public User getProfile(@PathVariable Integer id) {
        return userService.viewProfile(id);
    }

    @PutMapping("/{id}")
    public User editProfile(@PathVariable Integer id, @RequestBody EditProfileRequest req) {
        return userService.editProfile(id, req);
    }

    @GetMapping("/popular")
    public List<UserSearchResult> getPopularUsers(
            @RequestParam String userId,
            @RequestParam(defaultValue = "10") int limit) {
        return neo4jDriverService.getPopularUsers(userId, limit);
    }

    @GetMapping("/{userId}/recommendations")
    public List<UserSearchResult> getFriendRecommendations(@PathVariable String userId) {
        return neo4jDriverService.getFriendRecommendations(userId);
    }

    @GetMapping("/{userId}/diagnostics")
    public java.util.Map<String, Object> diagnoseUserRelationships(@PathVariable String userId) {
        return neo4jDriverService.diagnoseUserRelationships(userId);
    }

    // UC-5: Follow Another User
    @PostMapping("/follow")
    public ResponseEntity<String> followUser(
            @RequestParam String userEmail,
            @RequestParam String followedUserEmail) {
        try {
            userService.followUser(userEmail, followedUserEmail);
            return ResponseEntity.ok("User " + userEmail + " is now following user " + followedUserEmail);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // UC-5: Follow Another User
    @PostMapping("/unfollow")
    public ResponseEntity<String> unfollowUser(
            @RequestParam String userEmail,
            @RequestParam String followedUserEmail) {
        try {
            userService.unfollowUser(userEmail, followedUserEmail);
            return ResponseEntity.ok("User " + userEmail + " has now unfollowed user " + followedUserEmail);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // UC-5: Follow Another User by UserId
    @PostMapping("/follow/by-id")
    public ResponseEntity<String> followUserById(
            @RequestParam String userId,
            @RequestParam String followedUserId) {
        try {
            userService.followUserByUserId(userId, followedUserId);
            return ResponseEntity.ok("User " + userId + " is now following user " + followedUserId);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // UC-6: Unfollow Another User by UserId
    @PostMapping("/unfollow/by-id")
    public ResponseEntity<String> unfollowUserById(
            @RequestParam String userId,
            @RequestParam String followedUserId) {
        try {
            userService.unfollowUserByUserId(userId, followedUserId);
            return ResponseEntity.ok("User " + userId + " has now unfollowed user " + followedUserId);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // UC-7: Get Followers
    @GetMapping("/followers")
    public List<User> getFollowers(@RequestParam String userEmail) {
        return userService.getFollowersByEmail(userEmail);
    }

    // UC-7: Get Followers
    @GetMapping("/following")
    public List<User> getFollowing(@RequestParam String userEmail) {
        return userService.getFollowingByEmail(userEmail);
    }

    // UC-8: Get Mutual Connections
    @GetMapping("/mutual")
    public List<User> getMutualConnections(
            @RequestParam String userEmail,
            @RequestParam String otherUserEmail) {
        return userService.getMutualConnectionsByEmail(userEmail, otherUserEmail);
    }
}
