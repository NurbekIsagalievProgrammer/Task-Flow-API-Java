package com.example.taskmanagement.util;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordEncoderTest {
    
    @Test
    public void generateHash() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "admin123";
        String hash = encoder.encode(password);
        System.out.println("Hash for password '" + password + "': " + hash);
        
        // Проверяем что хеш работает
        boolean matches = encoder.matches(password, hash);
        System.out.println("Password matches: " + matches);
    }
} 