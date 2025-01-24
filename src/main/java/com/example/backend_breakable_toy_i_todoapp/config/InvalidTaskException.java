package com.example.backend_breakable_toy_i_todoapp.config;

public class InvalidTaskException extends RuntimeException {
    public InvalidTaskException(String message) {
        super(message);
    }
}