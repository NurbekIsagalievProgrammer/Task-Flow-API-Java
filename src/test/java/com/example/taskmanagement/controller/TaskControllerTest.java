package com.example.taskmanagement.controller;

import com.example.taskmanagement.dto.task.TaskRequest;
import com.example.taskmanagement.model.Priority;
import com.example.taskmanagement.service.TaskService;
import com.example.taskmanagement.security.JwtService;
import com.example.taskmanagement.config.TestSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TaskController.class)
@Import(TestSecurityConfig.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    @MockBean
    private JwtService jwtService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanCreateTask() throws Exception {
        TaskRequest request = new TaskRequest(
            "Test Task",
            "Description",
            Priority.HIGH,
            null
        );

        when(taskService.createTask(any())).thenReturn(null);
        when(jwtService.isTokenValid(any(), any())).thenReturn(true);

        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void userCannotCreateTask() throws Exception {
        TaskRequest request = new TaskRequest(
            "Test Task",
            "Description",
            Priority.HIGH,
            null
        );

        when(jwtService.isTokenValid(any(), any())).thenReturn(true);

        mockMvc.perform(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
} 