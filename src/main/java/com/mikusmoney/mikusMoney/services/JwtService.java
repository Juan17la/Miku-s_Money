package com.mikusmoney.mikusMoney.services;

import com.mikusmoney.mikusMoney.entity.Miku;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j
public class JwtService {

    @Value("${jwt.secret:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}")
    private String secretKey;

    @Value("${jwt.expiration:86400000}") // 24 horas por defecto
    private Long jwtExpiration;

    public String getToken(Miku miku) {
        log.debug("Generating JWT token for user: {} (ID: {})", miku.getFullName(), miku.getId());
        return getToken(new HashMap<>(), miku);
    }

    private String getToken(Map<String, Object> extraClaims, Miku miku) {
        Date issuedAt = new Date(System.currentTimeMillis());
        Date expirationDate = new Date(System.currentTimeMillis() + jwtExpiration);
        
        log.debug("Creating token - Subject (User ID): {}, Issued At: {}, Expires At: {}", 
                miku.getId(), issuedAt, expirationDate);
        
        String token = Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(miku.getId().toString())
                .claim("name", miku.getName())
                .claim("lastName", miku.getLastName())
                .claim("publicCode", miku.getPublicCode())
                .setIssuedAt(issuedAt)
                .setExpiration(expirationDate)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
        
        log.info("JWT token generated successfully for user ID: {}", miku.getId());
        return token;
    }

    private Key getSignInKey() {
        try {
            log.debug("Decoding secret key from Base64 for JWT signing/validation");
            byte[] keyBytes = Decoders.BASE64.decode(secretKey);
            log.debug("Secret key decoded successfully. Key size: {} bytes", keyBytes.length);
            
            if (keyBytes.length < 32) {
                log.warn("Secret key is shorter than recommended (256 bits / 32 bytes). Current size: {} bytes", keyBytes.length);
            }
            
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (IllegalArgumentException e) {
            log.error("Failed to decode Base64 secret key. Ensure jwt.secret in application.properties is valid Base64. Error: {}", e.getMessage());
            throw new RuntimeException("Invalid JWT secret key configuration", e);
        }
    }

    public String getUserIdFromToken(String token) {
        String userId = getClaim(token, Claims::getSubject);
        log.debug("Extracted user ID from token: {}", userId);
        return userId;
    }

    public boolean isTokenValid(String token, Long userId) {
        try {
            final String tokenUserId = getUserIdFromToken(token);
            final boolean userIdMatches = tokenUserId.equals(userId.toString());
            final boolean tokenNotExpired = !isTokenExpired(token);
            
            log.debug("Token validation - Expected user ID: {}, Token user ID: {}, User ID matches: {}, Token expired: {}",
                    userId, tokenUserId, userIdMatches, !tokenNotExpired);
            
            boolean isValid = userIdMatches && tokenNotExpired;
            
            if (!isValid) {
                if (!userIdMatches) {
                    log.warn("Token validation failed: User ID mismatch. Expected: {}, Got: {}", userId, tokenUserId);
                }
                if (!tokenNotExpired) {
                    Date expiration = getExpiration(token);
                    log.warn("Token validation failed: Token expired at {}. Current time: {}", 
                            expiration, new Date());
                }
            }
            
            return isValid;
        } catch (Exception e) {
            log.error("Error validating token: {}", e.getMessage(), e);
            return false;
        }
    }

    private Claims getAllClaims(String token) {
        try {
            log.debug("Parsing JWT token to extract claims");
            Claims claims = Jwts
                    .parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            log.debug("Token claims extracted - Subject: {}, Issued At: {}, Expiration: {}",
                    claims.getSubject(), claims.getIssuedAt(), claims.getExpiration());
            
            return claims;
        } catch (Exception e) {
            log.error("Failed to parse JWT claims. Error type: {}, Message: {}", 
                    e.getClass().getSimpleName(), e.getMessage());
            throw e;
        }
    }

    public <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Date getExpiration(String token) {
        return getClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        Date expiration = getExpiration(token);
        Date now = new Date();
        boolean expired = expiration.before(now);
        
        log.debug("Checking token expiration - Expires at: {}, Current time: {}, Is expired: {}",
                expiration, now, expired);
        
        return expired;
    }
}
