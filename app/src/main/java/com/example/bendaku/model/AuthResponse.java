package com.example.bendaku.model;

public class AuthResponse {
    private String jwt;
    private User user;
    
    public AuthResponse() {}
    
    public AuthResponse(String jwt, User user) {
        this.jwt = jwt;
        this.user = user;
    }
    
    public String getJwt() { return jwt; }
    public void setJwt(String jwt) { this.jwt = jwt; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}

