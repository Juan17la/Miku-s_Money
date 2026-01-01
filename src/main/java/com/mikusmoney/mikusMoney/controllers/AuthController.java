package com.mikusmoney.mikusMoney.controllers;

import jakarta.servlet.http.Cookie;
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

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;


@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/v0.1/auth/")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    
    @PostMapping("register")
    public ResponseEntity<AuthResponse> registerUser(
        @RequestBody MikuCreateRequest mikuCreateRequest,
        HttpServletResponse response
    ) {
        AuthResponse authResponse = authService.signUp(mikuCreateRequest);
        setAuthCookie(response, authResponse.getToken());
        
        return ResponseEntity.ok(AuthResponse.builder()
                .message("Registration successful")
                .build());
    }

    @PostMapping("login")
    public ResponseEntity<AuthResponse> loginUser(
        @RequestBody LoginRequest loginRequest,
        HttpServletResponse response
    ) {    
        AuthResponse authResponse = authService.login(loginRequest.getEmail(), loginRequest.getPinCode());
        setAuthCookie(response, authResponse.getToken());
        
        return ResponseEntity.ok(AuthResponse.builder()
                .message("Login successful")
                .build());
    }
    
    @PostMapping("logout")
    public ResponseEntity<AuthResponse> logout(HttpServletResponse response) {
        clearAuthCookie(response);
        return ResponseEntity.ok(AuthResponse.builder()
                .message("Logout successful")
                .build());
    }
    
    @GetMapping("me")
    public ResponseEntity<MikuResponse> me() {
        return ResponseEntity.ok(authService.getCurrentUser());
    }
    


    private void setAuthCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("AUTH-TOKEN", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); 
        cookie.setPath("/"); // Cookie for all paths
        cookie.setMaxAge(24 * 60 * 60); // 24 hours in seconds
        // cookie.setSameSite("Strict"); 
        response.addCookie(cookie);
    }
    

    private void clearAuthCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("AUTH-TOKEN", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0); // Delete cookie
        response.addCookie(cookie);
    }

}
