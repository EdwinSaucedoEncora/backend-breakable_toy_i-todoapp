package com.example.backend_breakable_toy_i_todoapp.dao;

import com.example.backend_breakable_toy_i_todoapp.model.Task;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.UUID;
// Bean (Controller, DAO, Service) // @Repository || @Bean
// To add sample information recommended to use as /utils package with data samples for testing
// It is recommended to implement interface many cases on DAO and Service and in controller if many methods will be
// implemented.
@Repository
public class TaskDAO implements TaskDAOInterface{
    private final ArrayList<Task> tasks;
    public TaskDAO(){
        tasks = new ArrayList<>();
        Task task2 = new Task("Task 2", "Medium");
        task2.setID(UUID.fromString("2e96986b-8b32-4ace-8582-4a8369c5720a"));
        tasks.add(new Task("Task 1", "High"));
        tasks.add(task2);
        tasks.add(new Task("Task 3", "Low"));
    }
    public void addTask(Task newTask){
        tasks.add(newTask);
    }
    public ArrayList<Task> getAll() {
        return tasks;
    }
    public Task getTask(UUID id){
        return tasks.stream().filter(task -> task.getId().equals(id)).findAny().orElse(null);
    }
    public void deleteTask(UUID id){
        tasks.removeIf(task -> task.getId().equals(id));
    }

    public void updateTask(Task updatedTask){
        tasks.stream().filter(task -> task.getId().equals(updatedTask.getId())).findAny().ifPresent(target -> target.updateTask(updatedTask));
    }
}
