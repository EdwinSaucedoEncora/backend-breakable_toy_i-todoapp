package com.example.backend_breakable_toy_i_todoapp.config;

public class TaskDataException extends RuntimeException {
    public TaskDataException(String message) {
        super(message);
    }
}
