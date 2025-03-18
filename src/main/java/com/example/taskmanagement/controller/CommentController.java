package com.example.taskmanagement.controller;

import com.example.taskmanagement.dto.comment.CommentRequest;
import com.example.taskmanagement.dto.comment.CommentResponse;
import com.example.taskmanagement.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tasks/{taskId}/comments")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Comments", description = "Task comments API")
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    @Operation(summary = "Add comment to task")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long taskId,
            @RequestBody @Valid CommentRequest request
    ) {
        return ResponseEntity.ok(commentService.createComment(taskId, request));
    }

    @GetMapping
    @Operation(summary = "Get task comments")
    public ResponseEntity<Page<CommentResponse>> getComments(
            @PathVariable Long taskId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(commentService.getTaskComments(taskId, pageable));
    }
} 