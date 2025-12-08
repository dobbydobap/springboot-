package org.example.rideshare.dto;

public class RegisterRequest {
    private String username;
    private String password;
    private String role; // ROLE_USER or ROLE_DRIVER

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
