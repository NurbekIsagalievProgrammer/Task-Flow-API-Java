package com.example.tasklist.service;

import com.example.tasklist.model.Task;
import java.util.List;

public interface TaskService {
    Task createTask(Task task);
    Task getTask(Long id);
    List<Task> getAllTasks();
    Task updateTask(Long id, Task task);
    void deleteTask(Long id);
} 