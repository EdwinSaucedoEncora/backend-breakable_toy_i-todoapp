package com.example.backend_breakable_toy_i_todoapp.dao;

import com.example.backend_breakable_toy_i_todoapp.model.Task;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.UUID;

@Repository
public class TaskDAO implements TaskDAOInterface{
    private final LinkedHashMap<UUID, Task> tasks;
    public TaskDAO(){
        tasks = new LinkedHashMap<>();
        Task task = new Task("Task 1", "high");
        Task task2 = new Task("Task 2", "medium");
        Task task3 = new Task("Task 3", "low");
        tasks.put(task.getId(), task);
        tasks.put(task2.getId(), task2);
        tasks.put(task3.getId(), task3);
    }
    public UUID addTask(Task newTask){
        tasks.put(newTask.getId(), newTask);
        return newTask.getId();
    }
    public LinkedHashMap<UUID, Task> getAll() {
        return tasks;
    }
    public Task getTask(UUID id){
        return tasks.get(id);
    }
    public void deleteTask(UUID id){
        tasks.remove(id);
    }

    public void updateTask(UUID id, Task updatedTask){
        tasks.replace(id, updatedTask);
    }

    public boolean hasTask(UUID id){
        return tasks.containsKey(id);
    }

    public void setDoneDate(UUID id){
        Task targetTask = tasks.get(id);
        targetTask.setDoneDate();
    }
    public void unsetDoneDate(UUID id){
        Task targetTask = tasks.get(id);
        targetTask.unsetDoneDate();
    }

}