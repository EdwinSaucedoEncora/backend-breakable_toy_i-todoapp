package com.example.backend_breakable_toy_i_todoapp.controller;

import com.example.backend_breakable_toy_i_todoapp.model.Task;
import com.example.backend_breakable_toy_i_todoapp.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.UUID;

@RestController
@RequestMapping(value="/todos")
public class ToDoController {
    @Autowired TaskService service;
    @RequestMapping
    public ArrayList<Task> getAllTasks() {
        return service.getAllTasks();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Task getTask(@PathVariable UUID id){
//        return new Task("TaskExample", "High", true);
        return service.getTaskById(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteTask(@PathVariable UUID id){
         service.deleteTaskById(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    public void addTask(@RequestBody Task newTask){
        System.out.println(newTask);
        service.addTask(newTask);
    }
}

