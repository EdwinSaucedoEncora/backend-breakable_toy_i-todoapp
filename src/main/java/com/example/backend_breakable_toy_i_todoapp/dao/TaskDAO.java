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
public class TaskDAO {
    private final ArrayList<Task> tasks;
    public TaskDAO(){
        tasks = new ArrayList<Task>();
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
        for(Task task : tasks){
            if(task.getId().equals(id)){
                return  task;
            }
        }
        return null;
    }
    public void deleteTask(UUID id){
        tasks.removeIf(task -> task.getId().equals(id));
        for(Task task : tasks){
            System.out.println(task.getId());
        }
    }
}
