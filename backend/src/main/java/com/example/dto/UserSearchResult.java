package com.example.dto;

public class UserSearchResult {
    private String userId;
    private String name;
    private String username;
    private String bio;
    private Integer followCount;

    public UserSearchResult() {
    }

    public UserSearchResult(String userId, String name, String username, String bio) {
        this.userId = userId;
        this.name = name;
        this.username = username;
        this.bio = bio;
    }

    public UserSearchResult(String userId, String name, String username, String bio, Integer followCount) {
        this.userId = userId;
        this.name = name;
        this.username = username;
        this.bio = bio;
        this.followCount = followCount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Integer getFollowCount() {
        return followCount;
    }

    public void setFollowCount(Integer followCount) {
        this.followCount = followCount;
    }
}
