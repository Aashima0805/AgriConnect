package com.agriconnect.dto;

public class AuthResponse {

    private String status;
    private String message;
    private UserResponse user;

    public AuthResponse() {
    }

    public AuthResponse(String status, String message, UserResponse user) {
        this.status = status;
        this.message = message;
        this.user = user;
    }

    public static AuthResponse success(String message, UserResponse user) {
        return new AuthResponse("success", message, user);
    }

    public static AuthResponse error(String message) {
        return new AuthResponse("error", message, null);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }
}
