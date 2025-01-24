package com.example.backend_breakable_toy_i_todoapp.controller;

import com.example.backend_breakable_toy_i_todoapp.model.Task;
import com.example.backend_breakable_toy_i_todoapp.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost:8080")
@RequestMapping(value="/todos")
public class ToDoController {
    @Autowired TaskService service;

    @GetMapping
    public ResponseEntity<Page<Task>> getAllTasks(
            @RequestParam(defaultValue = "all") String status,
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "all") String priority,
            Pageable pageable) {

        Page<Task> tasks = service.getAllTasks(status, name, priority, pageable);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable UUID id) {
        Task task = service.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTaskById(@PathVariable UUID id) {
        return service.deleteTaskById(id);
    }
    @PutMapping("/{id}")
    public ResponseEntity<String> updateTask(@PathVariable UUID id, @RequestBody Task updatedTask) {
        return service.updateTask(id, updatedTask);
    }


    @PostMapping
    public ResponseEntity<String> addTask(@RequestBody Task newTask) {
        return service.addTask(newTask);
    }

    @PutMapping("{id}/done")
    public ResponseEntity<String> setDoneDateById(@PathVariable UUID id) {
        return service.setDoneDateById(id);
    }

    @PutMapping("{id}/undone")
    public ResponseEntity<String> unsetDoneDateById(@PathVariable UUID id) {
        return service.unsetDoneDateById(id);
    }
}

