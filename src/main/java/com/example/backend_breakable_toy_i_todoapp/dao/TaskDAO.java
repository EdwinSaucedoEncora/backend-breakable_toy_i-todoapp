package com.example.backend_breakable_toy_i_todoapp.dao;

import com.example.backend_breakable_toy_i_todoapp.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.UUID;
// Bean (Controller, DAO, Service) // @Repository || @Bean
// To add sample information recommended to use as /utils package with data samples for testing
// It is recommended to implement interface many cases on DAO and Service and in controller if many methods will be
// implemented.
@Repository
public class TaskDAO implements TaskDAOInterface{
    private static final Logger logger = LoggerFactory.getLogger(TaskDAO.class);
    // Change to List interface
    private final LinkedHashMap<String, Task> tasks;
    public TaskDAO(){
        tasks = new LinkedHashMap<>();
        Task task = new Task("Task 1", "High");
        Task task2 = new Task("Task 2", "Medium");
        Task task3 = new Task("Task 3", "Low");
        task2.setID(UUID.fromString("2e96986b-8b32-4ace-8582-4a8369c5720a"));
        tasks.put(task.getId().toString(), task);
        tasks.put(task2.getId().toString(), task2);
        tasks.put(task3.toString(), task3);
    }
    public void addTask(Task newTask){
        tasks.put(newTask.getId().toString(), newTask);
    }
    public LinkedHashMap<String, Task> getAll() {
        return tasks;
    }
    public Task getTask(UUID id){
        return tasks.get(id.toString());
    }
    public void deleteTask(UUID id){
        tasks.remove(id.toString());
    }

    public void updateTask(UUID id, Task updatedTask){
        tasks.replace(id.toString(), updatedTask);
        // tasks Hash
        // select with id => update task all =>
        // select task => update task all => remove task => add task // If not hash
    }
    // Do manual calculation for the last endpoint
    // new Date()
}