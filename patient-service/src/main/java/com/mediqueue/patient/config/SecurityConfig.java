package com.mediqueue.patient.config;

//import com.mediqueue.patient.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Configuration // Marks this as a Spring configuration class
@EnableWebSecurity // Enables Spring Security
@EnableMethodSecurity // Allows @PreAuthorize, @Secured, etc.
@RequiredArgsConstructor // Lombok: injects final fields via constructor
public class SecurityConfig {

    // Service for JWT operations (currently not used here)
    //private final JwtService jwtService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF (not needed for stateless APIs)
                .csrf(AbstractHttpConfigurer::disable)

                // Enable CORS with custom configuration
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Make session stateless (no session stored on server)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Define authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints (no authentication required)
                        .requestMatchers("/api/patients/auth/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        // ✅ Allow Swagger
                        .requestMatchers(
                                "/patient-service/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        // Role-based access
                        .requestMatchers("/api/appointments/queue/**")
                        .hasAnyRole("DOCTOR", "ADMIN")

                        // All other requests require authentication
                        .anyRequest().authenticated()
                )

                // Add custom JWT filter before Spring's auth filter
                .addFilterBefore(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public OncePerRequestFilter jwtAuthFilter() {
        return new OncePerRequestFilter() {

            @Override
            protected void doFilterInternal(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain filterChain)
                    throws ServletException, IOException {

                // 🔥 IMPORTANT:
                // These headers are injected by API Gateway after JWT validation
                String userId = request.getHeader("X-User-Id");
                String role   = request.getHeader("X-User-Role");

                // If headers exist and no authentication is set yet
                if (userId != null && role != null &&
                        SecurityContextHolder.getContext().getAuthentication() == null) {

                    final var formattedRole = role.startsWith("ROLE_") ? role : "ROLE_" + role;

                    // Create authentication object
                    final var auth = new UsernamePasswordAuthenticationToken(
                            userId, // principal (user identity)
                            null,   // credentials (not needed here)
                            List.of(new SimpleGrantedAuthority(formattedRole)) // roles
                    );

                    // Set authentication in security context
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }

                // Continue filter chain
                filterChain.doFilter(request, response);
            }
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt is recommended for password hashing
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Allow requests from Angular frontend
        config.setAllowedOrigins(List.of("http://localhost:4200"));

        // Allowed HTTP methods
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Allow all headers
        config.setAllowedHeaders(List.of("*"));

        // Allow cookies/auth headers
        config.setAllowCredentials(true);

        // Apply this config to all endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
