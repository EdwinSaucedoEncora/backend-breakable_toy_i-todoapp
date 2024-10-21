package com.example.backend_breakable_toy_i_todoapp.dao;

import com.example.backend_breakable_toy_i_todoapp.model.Task;

import java.util.LinkedHashMap;
import java.util.UUID;

public interface TaskDAOInterface {
    void addTask(Task task);
    void deleteTask(UUID id);
    LinkedHashMap<String, Task> getAll();
}
