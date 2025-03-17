package com.example.taskmanagement.mapper;

import com.example.taskmanagement.dto.task.TaskRequest;
import com.example.taskmanagement.dto.task.TaskResponse;
import com.example.taskmanagement.model.Task;
import org.springframework.stereotype.Component;

@Component
public class TestTaskMapper implements TaskMapper {
    @Override
    public Task toEntity(TaskRequest request) {
        return Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .build();
    }

    @Override
    public TaskResponse toDto(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
} 