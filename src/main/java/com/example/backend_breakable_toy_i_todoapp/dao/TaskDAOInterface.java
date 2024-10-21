package com.example.backend_breakable_toy_i_todoapp.dao;

import com.example.backend_breakable_toy_i_todoapp.model.Task;

import java.util.LinkedHashMap;
import java.util.UUID;

public interface TaskDAOInterface {
    public UUID addTask(Task newTask);
    public LinkedHashMap<UUID, Task> getAll();
    public Task getTask(UUID id);
    public void deleteTask(UUID id);
    public void updateTask(UUID id, Task updatedTask);
    public boolean hasTask(UUID id);
    public void setDoneDate(UUID id);
    public void unsetDoneDate(UUID id);
}
