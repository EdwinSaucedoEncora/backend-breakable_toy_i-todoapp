package com.example.backend_breakable_toy_i_todoapp.service;

import com.example.backend_breakable_toy_i_todoapp.model.Task;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public interface TaskServiceInterface {
    public ResponseEntity<List<Task>> getAllTasks(String filter, Integer page);
    public ResponseEntity<Task> getTaskById(UUID id);
    public ResponseEntity<String> deleteTaskById(UUID id);
    public ResponseEntity<String> addTask(Task newTask);
    public ResponseEntity<String> updateTask(UUID id, Task updatedTask);
    public ResponseEntity<String> setDoneDateById(UUID id);
    public ResponseEntity<String> unsetDoneDateById(UUID id);

}
