package com.example.backend_breakable_toy_i_todoapp.config;

public class InvalidTaskDataException extends RuntimeException {
    public InvalidTaskDataException(String message) {
        super(message);
    }
}
