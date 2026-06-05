package com.chaty.dto;

public class AuthResponse {
    private String token;
    private String userId;
    private String name;
    private String preferredLanguage;

    public AuthResponse(String token, String userId, String name, String preferredLanguage) {
        this.token = token;
        this.userId = userId;
        this.name = name;
        this.preferredLanguage = preferredLanguage;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getPreferredLanguage() { return preferredLanguage; }
    public void setPreferredLanguage(String preferredLanguage) { this.preferredLanguage = preferredLanguage; }
}
