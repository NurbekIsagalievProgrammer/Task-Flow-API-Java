package com.example.taskmanagement.dto.comment;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentResponse {
    private Long id;
    private String content;
    private UserDto author;
    private LocalDateTime createdAt;
    
    @Data
    public static class UserDto {
        private Long id;
        private String email;
        private String firstName;
        private String lastName;
    }
} 