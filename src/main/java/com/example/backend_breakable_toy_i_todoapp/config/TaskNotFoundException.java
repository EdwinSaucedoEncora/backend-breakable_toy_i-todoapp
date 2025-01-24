package com.example.backend_breakable_toy_i_todoapp.config;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(String message) {
        super(message);
    }
}
