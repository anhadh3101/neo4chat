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
    public User getProfile(@PathVariable String id) {
        return userService.viewProfile(id);
    }

    @PutMapping("/{id}")
    public User editProfile(@PathVariable String id, @RequestBody EditProfileRequest req) {
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
            List<User> users = userService.followUser(userEmail, followedUserEmail);
            User follower = users.get(0);
            User followed = users.get(1);
            return ResponseEntity.ok("User " + follower.getName() + " (" + follower.getUserId()
                    + ") is now following user " + followed.getName() + " (" + followed.getUserId() + ")");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // UC-6: Unfollow Another User
    @PostMapping("/unfollow")
    public ResponseEntity<String> unfollowUser(
            @RequestParam String userEmail,
            @RequestParam String followedUserEmail) {
        try {
            List<User> users = userService.unfollowUser(userEmail, followedUserEmail);
            User follower = users.get(0);
            User followed = users.get(1);
            return ResponseEntity.ok("User " + follower.getName() + " (" + follower.getUserId()
                    + ") has now unfollowed user " + followed.getName() + " (" + followed.getUserId() + ")");
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
            List<User> users = userService.followUserByUserId(userId, followedUserId);
            User follower = users.get(0);
            User followed = users.get(1);
            return ResponseEntity.ok("User " + follower.getName() + " (" + follower.getUserId()
                    + ") is now following user " + followed.getName() + " (" + followed.getUserId() + ")");
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
            List<User> users = userService.unfollowUserByUserId(userId, followedUserId);
            User follower = users.get(0);
            User followed = users.get(1);
            return ResponseEntity.ok("User " + follower.getName() + " (" + follower.getUserId()
                    + ") has now unfollowed user " + followed.getName() + " (" + followed.getUserId() + ")");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // UC-7: Get Followers
    @GetMapping("/followers")
    public List<User> getFollowers(@RequestParam String userEmail) {
        return userService.getFollowersByEmail(userEmail);
    }

    // UC-7: Get Following
    @GetMapping("/following")
    public List<User> getFollowing(@RequestParam String userEmail) {
        return userService.getFollowingByEmail(userEmail);
    }

    // UC-7: Get Followers by UserId
    @GetMapping("/followers/by-id")
    public List<User> getFollowersById(@RequestParam String userId) {
        return userService.getFollowersByUserId(userId);
    }

    // UC-7: Get Following by UserId
    @GetMapping("/following/by-id")
    public List<User> getFollowingById(@RequestParam String userId) {
        return userService.getFollowingByUserId(userId);
    }

    // UC-8: Get Mutual Connections
    @GetMapping("/mutual")
    public List<User> getMutualConnections(
            @RequestParam String userEmail,
            @RequestParam String otherUserEmail) {
        return userService.getMutualConnectionsByEmail(userEmail, otherUserEmail);
    }

    // UC-8: Get Mutual Connections by UserId
    @GetMapping("/mutual/by-id")
    public List<User> getMutualConnectionsById(
            @RequestParam String userId,
            @RequestParam String otherUserId) {
        return userService.getMutualConnectionsByUserId(userId, otherUserId);
    }
}
