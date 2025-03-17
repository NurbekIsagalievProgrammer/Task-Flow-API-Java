package com.example.taskmanagement.service;

import com.example.taskmanagement.dto.task.TaskRequest;
import com.example.taskmanagement.dto.task.TaskResponse;
import com.example.taskmanagement.mapper.TaskMapper;
import com.example.taskmanagement.model.*;
import com.example.taskmanagement.repository.TaskRepository;
import com.example.taskmanagement.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskService taskService;

    private User testUser;
    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("admin@example.com");
        testUser.setPassword("password");
        testUser.setRole(Role.ADMIN);

        // Настраиваем SecurityContext
        var authentication = new UsernamePasswordAuthenticationToken(
            testUser,
            null,
            Collections.singleton(new SimpleGrantedAuthority("ROLE_" + testUser.getRole().name()))
        );
        
        securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Настраиваем поведение UserRepository
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));

        // Настраиваем только toEntity, так как toDto будет разным для разных тестов
        when(taskMapper.toEntity(any(TaskRequest.class))).thenReturn(
            new Task(null, "Test Task", "Description", TaskStatus.PENDING, Priority.HIGH, 
                    null, testUser, new ArrayList<>(), LocalDateTime.now(), LocalDateTime.now())
        );

        when(taskMapper.toDto(any(Task.class))).thenReturn(
            new TaskResponse(1L, "Test Task", "Description", TaskStatus.PENDING, Priority.HIGH, LocalDateTime.now(), LocalDateTime.now())
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createTaskTest() {
        TaskRequest request = new TaskRequest(
            "Test Task",
            "Description",
            Priority.HIGH,
            null
        );

        Task mockTask = new Task(
            1L,                  // id
            "Test Task",         // title
            "Description",       // description
            TaskStatus.PENDING,  // status
            Priority.HIGH,       // priority
            null,               // assignee
            testUser,           // creator
            new ArrayList<>(),   // comments
            LocalDateTime.now(), // createdAt
            LocalDateTime.now()  // updatedAt
        );

        when(taskRepository.save(any())).thenReturn(mockTask);

        TaskResponse response = taskService.createTask(request);
        assertNotNull(response);
        assertEquals("Test Task", response.getTitle());
        assertEquals(TaskStatus.PENDING, response.getStatus());
    }

    @Test
    void updateTaskStatusTest() {
        Task mockTask = new Task(
            1L,                  // id
            "Test Task",         // title
            "Description",       // description
            TaskStatus.PENDING,  // status
            Priority.HIGH,       // priority
            null,               // assignee
            testUser,           // creator
            new ArrayList<>(),   // comments
            LocalDateTime.now(), // createdAt
            LocalDateTime.now()  // updatedAt
        );

        // Когда задача найдена по ID
        when(taskRepository.findById(1L)).thenReturn(Optional.of(mockTask));

        // Когда задача сохраняется с новым статусом
        when(taskRepository.save(any())).thenAnswer(invocation -> {
            Task savedTask = invocation.getArgument(0);
            // Возвращаем задачу с обновленным статусом
            return savedTask;
        });

        // Когда маппер преобразует задачу в DTO
        when(taskMapper.toDto(any())).thenAnswer(invocation -> {
            Task task = invocation.getArgument(0);
            return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(), // Важно: здесь будет обновленный статус
                task.getPriority(),
                task.getCreatedAt(),
                task.getUpdatedAt()
            );
        });

        TaskResponse updated = taskService.updateTaskStatus(1L, TaskStatus.IN_PROGRESS);
        assertEquals(TaskStatus.IN_PROGRESS, updated.getStatus());
    }
} 