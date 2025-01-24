package com.example.backend_breakable_toy_i_todoapp.config;

public class MissingTaskFieldsException extends RuntimeException {
    public MissingTaskFieldsException(String message) {
        super(message);
    }
}
