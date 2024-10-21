package com.example.backend_breakable_toy_i_todoapp.controller;

import com.example.backend_breakable_toy_i_todoapp.model.Task;
import com.example.backend_breakable_toy_i_todoapp.service.TaskService;
import com.example.backend_breakable_toy_i_todoapp.service.TaskServiceInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value="/todos")
public class ToDoController implements TaskServiceInterface {
    @Autowired TaskService service;
    @RequestMapping
    public ResponseEntity<List<Task>> getAllTasks() {

        return new ResponseEntity<>(service.getAllTasks(), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Task> getTaskById(@PathVariable UUID id){
        return new ResponseEntity<>(service.getTaskById(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<String> updateTask(@PathVariable UUID id, @RequestBody Task updatedTask){
        return service.updateTask(id, updatedTask);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteTaskById(@PathVariable UUID id){
         return service.deleteTaskById(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<String> addTask(@RequestBody Task newTask){
        return service.addTask(newTask);
    }

    @RequestMapping(value = "{id}/done", method = RequestMethod.PUT)
    public ResponseEntity<String> setDoneDateById(@PathVariable UUID id){
        return service.setDoneDateById(id);
    }

    @RequestMapping(value = "{id}/undone", method = RequestMethod.PUT)
    public ResponseEntity<String> unsetDoneDateById(@PathVariable UUID id){
        return service.unsetDoneDateById(id);
    }
}

