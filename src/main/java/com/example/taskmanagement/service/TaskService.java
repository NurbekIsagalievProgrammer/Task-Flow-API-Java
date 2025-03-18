package com.example.taskmanagement.service;

import com.example.taskmanagement.dto.task.TaskRequest;
import com.example.taskmanagement.dto.task.TaskResponse;
import com.example.taskmanagement.exception.AccessDeniedException;
import com.example.taskmanagement.exception.ResourceNotFoundException;
import com.example.taskmanagement.mapper.TaskMapper;
import com.example.taskmanagement.model.Role;
import com.example.taskmanagement.model.Task;
import com.example.taskmanagement.model.TaskStatus;
import com.example.taskmanagement.model.User;
import com.example.taskmanagement.model.Priority;
import com.example.taskmanagement.repository.TaskRepository;
import com.example.taskmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    @Transactional
    public TaskResponse createTask(TaskRequest request) {
        User currentUser = getCurrentUser();
        if (currentUser.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Only admin can create tasks");
        }

        Task task = taskMapper.toEntity(request);
        task.setAuthor(currentUser);
        
        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assignee not found"));
            task.setAssignee(assignee);
        }
        
        return taskMapper.toDto(taskRepository.save(task));
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> getTasks(Pageable pageable) {
        User currentUser = getCurrentUser();
        Page<Task> tasks;
        
        if (currentUser.getRole() == Role.ADMIN) {
            tasks = taskRepository.findAll(pageable);
        } else {
            tasks = taskRepository.findByAssignee(currentUser, pageable);
        }
        
        return tasks.map(taskMapper::toDto);
    }

    @Transactional(readOnly = true)
    public TaskResponse getTask(Long id) {
        Task task = getTaskEntity(id);
        checkTaskAccess(task);
        return taskMapper.toDto(task);
    }

    @Transactional
    public TaskResponse updateTask(Long id, TaskRequest request) {
        Task task = getTaskEntity(id);
        if (getCurrentUser().getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Only admin can update tasks");
        }
        
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        
        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assignee not found"));
            task.setAssignee(assignee);
        }
        
        return taskMapper.toDto(taskRepository.save(task));
    }

    @Transactional
    public TaskResponse updateTaskStatus(Long id, TaskStatus status) {
        Task task = getTaskEntity(id);
        checkTaskAccess(task);
        task.setStatus(status);
        return taskMapper.toDto(taskRepository.save(task));
    }

    @Transactional
    public void deleteTask(Long id) {
        Task task = getTaskEntity(id);
        if (getCurrentUser().getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Only admin can delete tasks");
        }
        taskRepository.delete(task);
    }

    private Task getTaskEntity(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
    }

    private void checkTaskAccess(Task task) {
        User currentUser = getCurrentUser();
        if (currentUser.getRole() == Role.ADMIN) {
            return;
        }
        if (task.getAssignee() == null || !task.getAssignee().equals(currentUser)) {
            throw new AccessDeniedException("Only assignee can access this task");
        }
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

    public boolean isAssignee(Long taskId, UserDetails userDetails) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        return task.getAssignee() != null && 
               task.getAssignee().getEmail().equals(userDetails.getUsername());
    }
} 