package com.example.backend_breakable_toy_i_todoapp.controller;

import com.example.backend_breakable_toy_i_todoapp.model.Task;
import com.example.backend_breakable_toy_i_todoapp.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ToDoController.class) // Use this annotation for controller-specific tests
public class ToDoControllerTest {

    @MockBean // Use @MockBean to mock the service and inject it into the controller
    private TaskService taskService;

    @Autowired
    private MockMvc mockMvc;  // MockMvc is automatically injected when using @WebMvcTest


    @Test
    public void testGetAllTasks() throws Exception {
        Task task = new Task("Test Task", "high", LocalDate.parse("2025-01-24"));
        when(taskService.getAllTasks("all", "", "all", Pageable.unpaged())).thenReturn(new PageImpl<>(Collections.singletonList(task)));

        mockMvc.perform(get("/todos")
                        .param("status", "all")
                        .param("name", "")
                        .param("priority", "all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Test Task"));
    }

    @Test
    public void testGetTaskById() throws Exception {
        UUID taskId = UUID.randomUUID();
        Task task = new Task("Test Task", "high", LocalDate.parse("2025-01-24"));
        task.setId(taskId);

        when(taskService.getTaskById(taskId)).thenReturn(task);

        mockMvc.perform(get("/todos/{id}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Task"));
    }

    @Test
    public void testDeleteTaskById() throws Exception {
        UUID taskId = UUID.randomUUID();
        when(taskService.deleteTaskById(taskId)).thenReturn(new ResponseEntity<>("Task deleted", HttpStatus.OK));

        mockMvc.perform(delete("/todos/{id}", taskId))
                .andExpect(status().isOk())
                .andExpect(content().string("Task deleted"));
    }

    @Test
    public void testUpdateTask() throws Exception {
        UUID taskId = UUID.randomUUID();
        Task updatedTask = new Task("Updated Task", "medium", LocalDate.parse("2025-02-01"));

        when(taskService.updateTask(eq(taskId), eq(updatedTask)))
                .thenReturn(new ResponseEntity<>("Task updated", HttpStatus.OK));

        mockMvc.perform(put("/todos/{id}", taskId)
                        .contentType("application/json")
                        .content("{\"name\": \"Updated Task\", \"priority\": \"medium\", \"createdAt\": \"2025-02-01\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Task updated"));
    }

    @Test
    public void testAddTask() throws Exception {
        Task newTask = new Task("New Task", "low", LocalDate.parse("2025-02-01"));

        when(taskService.addTask(newTask)).thenReturn(new ResponseEntity<>("Task added", HttpStatus.CREATED));

        mockMvc.perform(post("/todos")
                        .contentType("application/json")
                        .content("{\"name\": \"New Task\", \"priority\": \"low\", \"createdAt\": \"2025-02-01\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().string("Task added"));
    }

    @Test
    public void testSetDoneDateById() throws Exception {
        UUID taskId = UUID.randomUUID();
        when(taskService.setDoneDateById(taskId)).thenReturn(new ResponseEntity<>("Task done date applied.", HttpStatus.OK));

        mockMvc.perform(put("/todos/{id}/done", taskId))
                .andExpect(status().isOk())
                .andExpect(content().string("Task done date applied."));
    }

    @Test
    public void testUnsetDoneDateById() throws Exception {
        UUID taskId = UUID.randomUUID();
        when(taskService.unsetDoneDateById(taskId)).thenReturn(new ResponseEntity<>("Task done date removed.", HttpStatus.OK));

        mockMvc.perform(put("/todos/{id}/undone", taskId))
                .andExpect(status().isOk())
                .andExpect(content().string("Task done date removed."));
    }
}
