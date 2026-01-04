package com.mikusmoney.mikusMoney.controllers;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mikusmoney.mikusMoney.dto.authenticationDTOs.AuthResponse;
import com.mikusmoney.mikusMoney.dto.authenticationDTOs.LoginRequest;
import com.mikusmoney.mikusMoney.dto.mikuDTOs.MikuCreateRequest;
import com.mikusmoney.mikusMoney.dto.mikuDTOs.MikuResponse;
import com.mikusmoney.mikusMoney.services.AuthService;
import com.mikusmoney.mikusMoney.services.CookiesService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;


@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/v0.1/auth/")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CookiesService cookiesService;
    
    @PostMapping("register")
    public ResponseEntity<AuthResponse> registerUser(
        @RequestBody MikuCreateRequest mikuCreateRequest,
        HttpServletResponse response
    ) {
        AuthResponse authResponse = authService.signUp(mikuCreateRequest);
        cookiesService.setAuthCookie(response, authResponse.getToken());
        
        return ResponseEntity.ok(AuthResponse.builder()
                .message("Registration successful")
                .build());
    }

    @PostMapping("login")
    public ResponseEntity<AuthResponse> loginUser(
        @RequestBody LoginRequest loginRequest,
        HttpServletResponse response
    ) {    
        AuthResponse authResponse = authService.login(loginRequest);
        cookiesService.setAuthCookie(response, authResponse.getToken());
        
        return ResponseEntity.ok(AuthResponse.builder()
                .message("Login successful")
                .build());
    }
    
    @PostMapping("logout")
    public ResponseEntity<AuthResponse> logout(HttpServletResponse response) {
        cookiesService.clearAuthCookie(response);
        return ResponseEntity.ok(AuthResponse.builder()
                .message("Logout successful")
                .build());
    }
    
    @GetMapping("me")
    public ResponseEntity<MikuResponse> me() {
        return ResponseEntity.ok(authService.getCurrentUser());
    }

}
