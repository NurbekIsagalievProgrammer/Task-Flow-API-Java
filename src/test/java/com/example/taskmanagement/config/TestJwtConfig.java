package com.example.taskmanagement.config;

import com.example.taskmanagement.security.JwtService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;

@TestConfiguration
public class TestJwtConfig {
    
    @Bean
    @Primary
    public JwtService jwtService() {
        return new JwtService() {
            @Override
            public String extractUsername(String token) {
                return "test@example.com";
            }

            @Override
            public String generateToken(UserDetails userDetails) {
                return "test-token";
            }

            @Override
            public boolean isTokenValid(String token, UserDetails userDetails) {
                return true;
            }
        };
    }
} 