package com.example.tasklist;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import com.example.tasklist.model.Task;
import com.example.tasklist.service.TaskService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootTest
public class TaskServiceTest {

    @Autowired
    private TaskService taskService;

    @Test
    public void testCreateTask() {
        Task task = new Task();
        task.setTitle("Test Task");
        task.setDescription("Test Description");

        Task savedTask = taskService.createTask(task);

        assertNotNull(savedTask.getId());
        assertEquals("Test Task", savedTask.getTitle());
        assertEquals("Test Description", savedTask.getDescription());
    }

    // ... остальные тесты
} 