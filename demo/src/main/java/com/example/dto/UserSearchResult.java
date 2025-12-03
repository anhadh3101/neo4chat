package com.example.dto;

import java.util.UUID;

public class UserSearchResult {
    private UUID id;
    private String name;
    private String username;
    private String bio;

    public UserSearchResult() {
    }

    public UserSearchResult(UUID id, String name, String username, String bio) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.bio = bio;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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
}
