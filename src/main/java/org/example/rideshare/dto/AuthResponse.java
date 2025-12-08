package org.example.rideshare.dto;

public class AuthResponse {
    private String token;
    private String role;
    private String username;

    public AuthResponse(String token, String username, String role) {
        this.token = token;
        this.username = username;
        this.role = role;
    }

    public String getToken() { return token; }
    public String getRole() { return role; }
    public String getUsername() { return username; }
}
