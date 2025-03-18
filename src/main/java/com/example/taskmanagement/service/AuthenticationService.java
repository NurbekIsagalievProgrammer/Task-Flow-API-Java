package com.example.taskmanagement.service;

import com.example.taskmanagement.dto.auth.AuthenticationRequest;
import com.example.taskmanagement.dto.auth.AuthenticationResponse;
import com.example.taskmanagement.dto.auth.RegisterRequest;
import com.example.taskmanagement.exception.ResourceNotFoundException;
import com.example.taskmanagement.exception.UserAlreadyExistsException;
import com.example.taskmanagement.model.Role;
import com.example.taskmanagement.model.User;
import com.example.taskmanagement.repository.UserRepository;
import com.example.taskmanagement.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("User with this email already exists");
        }

        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);
        var token = jwtService.generateToken(user);
        
        return AuthenticationResponse.builder()
                .token(token)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            System.out.println("Raw password from request: " + request.getPassword());
            var user = userRepository.findByEmail(request.getEmail());
            if (user.isPresent()) {
                System.out.println("Stored password hash: " + user.get().getPassword());
                System.out.println("Attempting to match passwords...");
            }
            
            // Проверяем существование пользователя
            System.out.println("User found in DB: " + (user.isPresent() ? "yes" : "no"));
            
            if (user.isPresent()) {
                System.out.println("User role: " + user.get().getRole());
                System.out.println("Password length: " + user.get().getPassword().length());
            }
            
            // Пытаемся аутентифицировать
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            
            System.out.println("Authentication successful");
            
            var token = jwtService.generateToken(user.orElseThrow());
            return AuthenticationResponse.builder()
                    .token(token)
                    .build();
            
        } catch (Exception e) {
            System.out.println("Authentication error: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void makeAdmin(Long userId) {
        try {
            System.out.println("Attempting to make user " + userId + " an admin");
            System.out.println("Current authentication principal: " + 
                SecurityContextHolder.getContext().getAuthentication().getPrincipal());
            System.out.println("Current authentication authorities: " + 
                SecurityContextHolder.getContext().getAuthentication().getAuthorities());
            
            var userExists = userRepository.existsById(userId);
            System.out.println("User exists: " + userExists);
            
            var user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
            
            System.out.println("Found user: " + user.getEmail() + ", current role: " + user.getRole());
            user.setRole(Role.ADMIN);
            userRepository.save(user);
            System.out.println("Successfully made user an admin");
        } catch (Exception e) {
            System.out.println("Error in makeAdmin: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
} 