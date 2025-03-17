package com.example.taskmanagement.config;

import com.example.taskmanagement.mapper.TaskMapper;
import com.example.taskmanagement.mapper.TestTaskMapper;
import com.example.taskmanagement.model.User;
import com.example.taskmanagement.model.Role;
import com.example.taskmanagement.repository.TaskRepository;
import com.example.taskmanagement.repository.UserRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@TestConfiguration
public class TestConfig {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private TaskRepository taskRepository;

    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
        return username -> User.builder()
                .id(1L)
                .email(username)
                .firstName("Test")
                .lastName("User")
                .role(Role.ADMIN)
                .password("password")
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Primary
    public TaskMapper taskMapper() {
        return new TestTaskMapper();
    }
} 