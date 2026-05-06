package com.minierp.controllers;

import com.minierp.services.AuthService;

public class LoginController {
    private final AuthService authService = AuthService.getInstance();

    public boolean login(String username, String password) {
        if (username == null || username.isBlank()) return false;
        if (password == null || password.isBlank()) return false;
        return authService.login(username.trim(), password.trim());
    }

    public void logout() { authService.logout(); }
    public String getRole() { return authService.getCurrentRole(); }
    public boolean isLoggedIn() { return authService.isLoggedIn(); }
}
