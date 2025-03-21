package com.example.taskmanagement.service;

import com.example.taskmanagement.dto.comment.CommentRequest;
import com.example.taskmanagement.dto.comment.CommentResponse;
import com.example.taskmanagement.exception.ResourceNotFoundException;
import com.example.taskmanagement.mapper.CommentMapper;
import com.example.taskmanagement.model.Comment;
import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.model.User;
import com.example.taskmanagement.repository.CommentRepository;
import com.example.taskmanagement.repository.TaskRepository;
import com.example.taskmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import com.example.taskmanagement.exception.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@Service
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class CommentService {
    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final CommentMapper commentMapper;
    private final UserRepository userRepository;

    @Transactional
    public CommentResponse createComment(Long taskId, CommentRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
                
        Comment comment = commentMapper.toEntity(request);
        comment.setTask(task);
        comment.setAuthor(getCurrentUser());
        
        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Transactional(readOnly = true)
    public Page<CommentResponse> getTaskComments(Long taskId, Pageable pageable) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
                
        return commentRepository.findByTask(task, pageable)
                .map(commentMapper::toDto);
    }

    private User getCurrentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Auth: " + auth);
        System.out.println("Principal type: " + auth.getPrincipal().getClass());
        System.out.println("Principal: " + auth.getPrincipal());
        System.out.println("Authorities: " + auth.getAuthorities());
        
        if (auth.getPrincipal() instanceof User) {
            return (User) auth.getPrincipal();
        } else if (auth.getPrincipal() instanceof UserDetails userDetails) {
          return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        }
        
        throw new AccessDeniedException("Invalid authentication");
    }
} 