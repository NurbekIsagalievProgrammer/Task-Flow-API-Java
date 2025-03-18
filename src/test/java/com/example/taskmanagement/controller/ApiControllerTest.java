package com.example.taskmanagement.controller;

import com.example.taskmanagement.dto.task.TaskRequest;
import com.example.taskmanagement.dto.task.TaskResponse;
import com.example.taskmanagement.dto.comment.CommentRequest;
import com.example.taskmanagement.dto.auth.RegisterRequest;
import com.example.taskmanagement.dto.auth.AuthenticationRequest;
import com.example.taskmanagement.model.Priority;
import com.example.taskmanagement.model.TaskStatus;
import com.example.taskmanagement.service.TaskService;
import com.example.taskmanagement.service.CommentService;
import com.example.taskmanagement.service.AuthenticationService;
import com.example.taskmanagement.security.JwtService;
import com.example.taskmanagement.config.TestSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({TaskController.class, AuthController.class, CommentController.class})
@Import(TestSecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    @MockBean
    private CommentService commentService;

    @MockBean
    private AuthenticationService authService;

    @MockBean
    private JwtService jwtService;

    // ADMIN TESTS
    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanCreateTask() throws Exception {
        TaskRequest request = new TaskRequest("Test Task", "Description", Priority.HIGH, 1L);
        TaskResponse response = new TaskResponse(1L, "Test Task", "Description", 
            TaskStatus.PENDING, Priority.HIGH, LocalDateTime.now(), LocalDateTime.now());
        
        when(taskService.createTask(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanGetAllTasks() throws Exception {
        List<TaskResponse> tasks = List.of(
            new TaskResponse(1L, "Test", "Description", TaskStatus.PENDING, 
                Priority.LOW, LocalDateTime.now(), LocalDateTime.now())
        );
        Page<TaskResponse> page = new PageImpl<>(tasks);
        when(taskService.getTasks(any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/tasks")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "priority,desc"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanGetTaskById() throws Exception {
        TaskResponse response = new TaskResponse(1L, "Test", "Description",
            TaskStatus.PENDING, Priority.LOW, LocalDateTime.now(), LocalDateTime.now());
        when(taskService.getTask(any())).thenReturn(response);

        mockMvc.perform(get("/api/v1/tasks/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanUpdateTask() throws Exception {
        TaskRequest request = new TaskRequest("Updated Task", "New Description", Priority.HIGH, 1L);
        TaskResponse response = new TaskResponse(1L, "Updated Task", "New Description",
            TaskStatus.PENDING, Priority.HIGH, LocalDateTime.now(), LocalDateTime.now());
        
        when(taskService.updateTask(any(), any())).thenReturn(response);

        mockMvc.perform(put("/api/v1/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanDeleteTask() throws Exception {
        mockMvc.perform(delete("/api/v1/tasks/1"))
                .andExpect(status().isNoContent());
    }

    // USER TESTS
    @Test
    @WithMockUser(roles = "USER")
    void userCanGetTask() throws Exception {
        TaskResponse response = new TaskResponse(1L, "Test", "Description",
            TaskStatus.PENDING, Priority.LOW, LocalDateTime.now(), LocalDateTime.now());
        when(taskService.getTask(any())).thenReturn(response);

        mockMvc.perform(get("/api/v1/tasks/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void userCanUpdateTaskStatus() throws Exception {
        TaskResponse response = new TaskResponse(1L, "Test", "Description",
            TaskStatus.IN_PROGRESS, Priority.LOW, LocalDateTime.now(), LocalDateTime.now());
        when(taskService.updateTaskStatus(any(), any())).thenReturn(response);

        mockMvc.perform(patch("/api/v1/tasks/1/status")
                .param("status", "IN_PROGRESS"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void userCanAddComment() throws Exception {
        CommentRequest request = new CommentRequest("Test comment");

        mockMvc.perform(post("/api/v1/tasks/1/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void userCanGetComments() throws Exception {
        mockMvc.perform(get("/api/v1/tasks/1/comments")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "createdAt,desc"))
                .andExpect(status().isOk());
    }

    // AUTH TESTS
    @Test
    void canRegisterNewUser() throws Exception {
        RegisterRequest request = new RegisterRequest(
            "John", "Doe", "john@example.com", "password"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void canAuthenticate() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest(
            "john@example.com", "password"
        );

        mockMvc.perform(post("/api/v1/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    // NEGATIVE TESTS
    @Test
    @WithMockUser(roles = "USER")
    void userCannotCreateTask() throws Exception {
        TaskRequest request = new TaskRequest("Test", "Description", Priority.LOW, 1L);

        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void userCannotDeleteTask() throws Exception {
        mockMvc.perform(delete("/api/v1/tasks/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void userCannotUpdateOthersTask() throws Exception {
        TaskRequest request = new TaskRequest("Test", "Description", Priority.LOW, 1L);
        when(taskService.updateTask(any(), any()))
            .thenThrow(new com.example.taskmanagement.exception.AccessDeniedException("Access denied"));

        mockMvc.perform(put("/api/v1/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void invalidPaginationParametersShouldReturnBadRequest() throws Exception {
        when(taskService.getTasks(any()))
            .thenThrow(new IllegalArgumentException("Invalid pagination parameters"));

        mockMvc.perform(get("/api/v1/tasks")
                .param("page", "-1")
                .param("size", "0")
                .param("sort", "invalidField,invalid"))
                .andExpect(status().isBadRequest());
    }
} 