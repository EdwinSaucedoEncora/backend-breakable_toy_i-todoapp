package com.example.backend_breakable_toy_i_todoapp.controller;

import com.example.backend_breakable_toy_i_todoapp.model.AllTasksResponse;
import com.example.backend_breakable_toy_i_todoapp.model.AverageDetails;
import com.example.backend_breakable_toy_i_todoapp.model.Task;
import com.example.backend_breakable_toy_i_todoapp.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@CrossOrigin(origins = "http://localhost:8080")
@RequestMapping(value="/todos")
public class ToDoController {
    @Autowired TaskService service;
    @RequestMapping
    public ResponseEntity<AllTasksResponse> getAllTasks(
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "all") String priority,
            @RequestParam(defaultValue = "all") String status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(required = false) String sort
    ) {
        try {
            return new ResponseEntity<>(service.getAllTasks(status, name, priority, page, sort), HttpStatus.OK);
        } catch (RuntimeException e) {
            // If any error return an empty list to avoid rendering errors
            return new ResponseEntity<>(new AllTasksResponse(null, 0), HttpStatus.BAD_REQUEST);
        }
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

    @RequestMapping(value = "/{id}/done", method = RequestMethod.PUT)
    public ResponseEntity<String> setDoneDateById(@PathVariable UUID id){
        return service.setDoneDateById(id);
    }

    @RequestMapping(value = "/{id}/undone", method = RequestMethod.PUT)
    public ResponseEntity<String> unsetDoneDateById(@PathVariable UUID id){
        return service.unsetDoneDateById(id);
    }

    @RequestMapping(value = "/metrics", method = RequestMethod.GET)
    public ResponseEntity<AverageDetails> getAverageDetails(){
        return service.getAverageDetails();
    }
}

