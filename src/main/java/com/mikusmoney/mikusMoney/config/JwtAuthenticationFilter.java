package com.mikusmoney.mikusMoney.config;

import com.mikusmoney.mikusMoney.entity.Miku;
import com.mikusmoney.mikusMoney.repository.MikuRepository;
import com.mikusmoney.mikusMoney.services.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final MikuRepository mikuRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        final String token = getTokenFromRequest(request);
        final String requestURI = request.getRequestURI();
        
        if (token == null) {
            log.debug("No JWT token found in request to: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            log.debug("Processing JWT token for request to: {}", requestURI);
            
            String userId = jwtService.getUserIdFromToken(token);
            log.debug("Extracted user ID from token: {}", userId);
            
            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Parse userId to Long
                Long userIdLong;
                try {
                    userIdLong = Long.parseLong(userId);
                } catch (NumberFormatException e) {
                    log.error("Invalid user ID format in token. Expected numeric ID but got: '{}'. " +
                            "Verify that the JWT subject contains the user ID, not email or username.", userId);
                    filterChain.doFilter(request, response);
                    return;
                }
                
                // Find user in database
                Miku miku = mikuRepository.findById(userIdLong).orElse(null);
                
                if (miku == null) {
                    log.warn("User with ID {} not found in database. Token may be for a deleted user.", userIdLong);
                    filterChain.doFilter(request, response);
                    return;
                }
                
                // Validate token
                if (jwtService.isTokenValid(token, miku.getId())) {
                    log.debug("Token is valid for user: {} (ID: {})", miku.getFullName(), miku.getId());
                    
                    // Create authentication token with USER authority
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            miku,
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                    );
                    
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    
                    log.info("Successfully authenticated user: {} (ID: {}) for request: {}", 
                            miku.getFullName(), miku.getId(), requestURI);
                } else {
                    log.warn("Token validation failed for user ID: {}. Token may be expired or tampered.", userIdLong);
                }
            }
        } catch (SignatureException e) {
            log.error("Invalid JWT signature. The token signature does not match. " +
                    "Verify that the secret key used for signing matches the one used for validation. " +
                    "Error: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.warn("JWT token has expired. Issued at: {}, Expiration: {}, Current time: {}", 
                    e.getClaims().getIssuedAt(), e.getClaims().getExpiration(), new java.util.Date());
        } catch (MalformedJwtException e) {
            log.error("Malformed JWT token. The token structure is invalid. Error: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT token is null, empty, or only whitespace. Error: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during JWT authentication. Request URI: {}, Error type: {}, Message: {}", 
                    requestURI, e.getClass().getSimpleName(), e.getMessage(), e);
        }
        
        filterChain.doFilter(request, response);
    }

    /**
     * Extracts JWT token from request.
     * First checks for AUTH-TOKEN cookie (primary method),
     * then falls back to Authorization header (for backwards compatibility).
     * 
     * @param request The HTTP request
     * @return JWT token string or null if not found
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        // Priority 1: Check for token in cookies
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("AUTH-TOKEN".equals(cookie.getName())) {
                    String token = cookie.getValue();
                    if (StringUtils.hasText(token)) {
                        log.debug("JWT token found in cookie");
                        return token;
                    }
                }
            }
        }
        
        // Priority 2: Fallback to Authorization header for backwards compatibility
        final String authHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            log.debug("JWT token found in Authorization header");
            return authHeader.substring(7);
        }
        
        return null;
    }
}
