package com.mediqueue.patient.service;

import com.mediqueue.patient.data.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

/**
 * Service responsible for JWT (JSON Web Token) operations.
 *
 * Handles:
 * - Access token generation (short-lived)
 * - Refresh token generation (long-lived)
 * - Token validation
 * - Claim extraction
 *
 * Uses HMAC SHA-256 (HS256) for signing tokens.
 */
@Service // Marks this as a Spring service
public class JwtService {

    /**
     * Secret key used for signing JWT.
     * Must be strong and kept secure (ideally in env variables or vault).
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * Expiration time for access token (in milliseconds).
     * Typically short-lived (e.g., 15 minutes).
     */
    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * Expiration time for refresh token (in milliseconds).
     * Typically long-lived (e.g., days/weeks).
     */
    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    /**
     * Generate JWT access token.
     *
     * Contains:
     * - subject → userId
     * - claims → email, role
     * - issued time
     * - expiration time
     *
     * @param userId Unique ID of the user
     * @param email  User's email
     * @param role   User role (e.g., ADMIN, DOCTOR)
     * @return Signed JWT access token
     */
    public String generateAccessToken(final String userId, final String email, final String role) {

        final String formattedRole = role.startsWith("ROLE_") ? role : "ROLE_" + role;

        return Jwts.builder()
                .setSubject(userId) // Sets user identity
                .addClaims(Map.of("email", email, "role", formattedRole)) // Custom claims
                .setIssuedAt(new Date()) // Token creation time
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // Expiry
                .signWith(getKey(), SignatureAlgorithm.HS256) // Sign with secret key
                .compact(); // Build token
    }

    /**
     * Generate JWT refresh token.
     *
     * Contains:
     * - subject → userId
     * - longer expiration time
     *
     * Used to issue new access tokens without re-login.
     *
     * @param userCode The code that identifies the user uniquely.
     * @return Signed JWT refresh token
     */
    public String generateRefreshToken(final String userCode) {
        return Jwts.builder()
                .setSubject(userCode)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extract all claims from a JWT token.
     *
     * Validates signature and parses token.
     *
     * @param token JWT token
     * @return Claims object containing all token data
     */
    public Claims extractClaims(final String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey()) // Verify signature using secret key
                .build()
                .parseClaimsJws(token) // Parse token
                .getBody(); // Extract claims
    }

    /**
     * Validate JWT token.
     *
     * Checks:
     * - Signature validity
     * - Expiration
     *
     * @param token JWT token
     * @return true if valid, false otherwise
     */
    public boolean isTokenValid(final String token) {
        try {
            extractClaims(token); // Will throw exception if invalid
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extract user ID (subject) from token.
     *
     * @param token JWT token
     * @return userId stored in token subject
     */
    public String extractUserId(final String token) {
        return extractClaims(token).getSubject();
    }

    /**
     * Extract user role from the JWT claims.
     *
     * @param token JWT token
     * @return role stored in the token claims
     */
    public String extractRole(final String token) {
        return extractClaims(token).get("role", String.class);
    }

    /**
     * Generate signing key from secret.
     *
     * Converts string secret into a secure HMAC key.
     *
     * IMPORTANT:
     * - Secret must be sufficiently long (>= 256 bits for HS256)
     *
     * @return Key used for signing and validation
     */
    private Key getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
