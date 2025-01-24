package com.example.backend_breakable_toy_i_todoapp.dao;

import com.example.backend_breakable_toy_i_todoapp.model.Task;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class TaskDAO implements TaskDAOInterface{
    private final LinkedHashMap<UUID, Task> tasks;
    public TaskDAO() {
        tasks = new LinkedHashMap<>();
        loadTasksFromFile();
    }

    private void loadTasksFromFile() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("tasks.txt"))))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String name = parts[0].trim();
                    String priority = parts[1].trim();
                    Task task = new Task(name, priority);
                    tasks.put(task.getId(), task);
                }
            }
        } catch (IOException | NullPointerException e) {
            throw new RuntimeException("Failed to load tasks from file: " + "tasks.txt", e);
        }
    }

    public void addTask(Task newTask) {
        if (newTask == null || newTask.getName() == null || newTask.getPriority() == null) {
            throw new IllegalArgumentException("Task and its required fields must not be null");
        }
        tasks.put(newTask.getId(), newTask);
    }

    public LinkedHashMap<UUID, Task> getAll() {
        return tasks;
    }

    public Task getTask(UUID id) {
        if (!tasks.containsKey(id)) {
            throw new NoSuchElementException("Task with ID " + id + " not found");
        }
        return tasks.get(id);
    }

    public void deleteTask(UUID id) {
        if (!tasks.containsKey(id)) {
            throw new NoSuchElementException("Task with ID " + id + " not found");
        }
        tasks.remove(id);
    }

    public void updateTask(UUID id, Task updatedTask) {
        if (!tasks.containsKey(id)) {
            throw new NoSuchElementException("Task with ID " + id + " not found");
        }
        tasks.replace(id, updatedTask);
    }

    public boolean hasTask(UUID id){
        return !tasks.containsKey(id);
    }

    public void setDoneDate(UUID id){
        Task targetTask = tasks.get(id);
        targetTask.setDoneDate();
    }
    public void unsetDoneDate(UUID id){
        Task targetTask = tasks.get(id);
        targetTask.unsetDoneDate();
    }

    public List<Task> getTasksForAverageDetails() {
        return tasks.values().stream()
                .filter(task -> task.getCreatedAt() != null && task.getDoneDate() != null)
                .collect(Collectors.toList());
    }
}