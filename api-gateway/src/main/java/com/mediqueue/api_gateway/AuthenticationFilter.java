package com.mediqueue.api_gateway;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.List;

/**
 * AuthenticationFilter
 *
 * This is a custom Spring Cloud Gateway filter used to:
 * ✅ Validate JWT token from incoming requests
 * ✅ Allow public endpoints without authentication
 * ✅ Extract user details from token
 * ✅ Forward user info to downstream services via headers
 */
@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    /**
     * Secret key used to sign/validate JWT tokens
     * Injected from application.yml
     */
    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * List of public endpoints that don't require authentication
     */
    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/patients/auth/login",
            "/api/patients/auth/register",
            "/patient-service/v3/api-docs",
            "/swagger-ui"
    );

    /**
     * Constructor required by AbstractGatewayFilterFactory
     */
    public AuthenticationFilter() {
        super(Config.class);
    }

    /**
     * Main filter logic
     *
     * This method is executed for every incoming request
     */
    @Override
    public GatewayFilter apply(final Config config) {
        return (exchange, chain) -> {

            // Get request path
            final String path = exchange.getRequest().getPath().value();

            // ✅ Allow public endpoints without JWT validation
            if (PUBLIC_PATHS.stream().anyMatch(path::startsWith)) {
                return chain.filter(exchange);
            }

            // Extract Authorization header
            final String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            // ❌ If header missing or invalid → return 401
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return unauthorized(exchange);
            }

            // Extract token (remove "Bearer ")
            final String token = authHeader.substring(7);

            try {
                // Parse JWT and extract claims
                final Claims claims = extractClaims(token);

                // Add user info to request headers for downstream services
                final ServerWebExchange mutatedExchange = exchange.mutate()
                        .request(r -> r.header("X-User-Id", claims.getSubject())
                                .header("X-User-Role", claims.get("role", String.class)))
                        .build();

                // Continue filter chain
                return chain.filter(mutatedExchange);

            } catch (Exception e) {
                System.err.println("JWT Validation failed: " + e.getMessage());
                // ❌ Invalid token → return 401
                return unauthorized(exchange);
            }
        };
    }

    /**
     * Extracts claims from JWT token
     */
    private Claims extractClaims(String token) {
        final Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Returns 401 Unauthorized response
     */
    private Mono<Void> unauthorized(final ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    /**
     * Empty config class required by Spring Gateway
     */
    public static class Config {}
}