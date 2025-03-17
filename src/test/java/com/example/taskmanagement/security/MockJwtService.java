package com.example.taskmanagement.security;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;

@TestConfiguration
public class MockJwtService {
    
    @Bean
    @Primary
    public JwtService jwtService() {
        return new JwtService() {
            private final String secretKey = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
            private final long jwtExpiration = 86400000;

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