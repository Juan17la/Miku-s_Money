package com.mikusmoney.mikusMoney.services;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Service responsible for managing HTTP cookies in the application.
 * Handles creation, deletion, and retrieval of authentication cookies.
 */
@Service
public class CookiesService {

    private static final String AUTH_COOKIE_NAME = "AUTH-TOKEN";
    private static final int COOKIE_MAX_AGE = 24 * 60 * 60; // 24 hours in seconds

    /**
     * Sets an authentication cookie in the HTTP response.
     * 
     * @param response The HTTP response where the cookie will be added
     * @param token The JWT token to store in the cookie
     */
    public void setAuthCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie(AUTH_COOKIE_NAME, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); 
        cookie.setPath("/"); // Cookie for all paths
        cookie.setMaxAge(COOKIE_MAX_AGE);
        // cookie.setSameSite("Strict"); 
        response.addCookie(cookie);
    }

    /**
     * Clears the authentication cookie by setting its max age to 0.
     * 
     * @param response The HTTP response where the cookie deletion will be applied
     */
    public void clearAuthCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(AUTH_COOKIE_NAME, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0); // Delete cookie
        response.addCookie(cookie);
    }

    /**
     * Extracts the authentication token from request cookies.
     * 
     * @param request The HTTP request containing cookies
     * @return The JWT token string or null if not found
     */
    public String getTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (AUTH_COOKIE_NAME.equals(cookie.getName())) {
                    String token = cookie.getValue();
                    if (StringUtils.hasText(token)) {
                        return token;
                    }
                }
            }
        }
        return null;
    }
}
