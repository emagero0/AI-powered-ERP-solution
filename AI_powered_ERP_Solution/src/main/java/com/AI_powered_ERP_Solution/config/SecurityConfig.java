package com.AI_powered_ERP_Solution.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF since this is a stateless API
                .csrf(csrf -> csrf.disable())
                // Set session creation policy to stateless
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Permit access to Swagger UI and API docs
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/swagger-ui.html", "/api/swagger-ui/**", "/api-docs/**").permitAll()
                        // Require authentication for API endpoints
                        .requestMatchers("/api/**").authenticated()
                        // Permit all other requests (for now, since we're mocking Dynamics365Service)
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}
