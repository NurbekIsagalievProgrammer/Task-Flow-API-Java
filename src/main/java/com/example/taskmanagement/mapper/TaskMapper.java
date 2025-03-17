package com.example.taskmanagement.mapper;

import com.example.taskmanagement.dto.task.TaskRequest;
import com.example.taskmanagement.dto.task.TaskResponse;
import com.example.taskmanagement.model.Task;

public interface TaskMapper {
    Task toEntity(TaskRequest request);
    TaskResponse toDto(Task task);
} 