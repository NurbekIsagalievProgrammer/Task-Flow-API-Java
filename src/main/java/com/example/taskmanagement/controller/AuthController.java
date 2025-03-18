package com.example.taskmanagement.controller;

import com.example.taskmanagement.dto.auth.AuthenticationRequest;
import com.example.taskmanagement.dto.auth.AuthenticationResponse;
import com.example.taskmanagement.dto.auth.RegisterRequest;
import com.example.taskmanagement.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication management API")
public class AuthController {
    private final AuthenticationService authService;

    @PostMapping("/register")
    @Operation(summary = "Register new user")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody @Valid RegisterRequest request
    ) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/authenticate")
    @Operation(summary = "Authenticate user")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody @Valid AuthenticationRequest request
    ) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @PutMapping("/users/{id}/make-admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Make user an admin",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    public ResponseEntity<Void> makeAdmin(@PathVariable Long id) {
        authService.makeAdmin(id);
        return ResponseEntity.ok().build();
    }
}



















