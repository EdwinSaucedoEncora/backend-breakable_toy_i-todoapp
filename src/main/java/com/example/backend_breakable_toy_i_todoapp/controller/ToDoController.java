package com.example.backend_breakable_toy_i_todoapp.controller;

import com.example.backend_breakable_toy_i_todoapp.model.Task;
import com.example.backend_breakable_toy_i_todoapp.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value="/todos")
public class ToDoController {
    @Autowired TaskService service;
    private static final Logger logger = LoggerFactory.getLogger(ToDoController.class);
    // ResponseEntity => Body, Status
    @RequestMapping
    // public array <interface> => e.g List<Task>
    // notation @QueryParam => para filtro de datos (String | Int)
    public List<Task> getAllTasks() {
        //logger.debug("Hello Encora!");
        //Specify into Config depend on the environment;
//        logger.info("Hello Info Encora");
//        logger.error("Hello Error Encora");
        //toString @override
        //ToString
        return service.getAllTasks();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Task getTask(@PathVariable UUID id){
        return service.getTaskById(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public void updateTask(@PathVariable UUID id, @RequestBody Task updatedTask){
        service.updateTask(id, updatedTask);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteTask(@PathVariable UUID id){
         service.deleteTaskById(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    public void addTask(@RequestBody Task newTask){
        service.addTask(newTask);
    }
    // Test crear mock de llamada, en test revisar que se mande a llamar el endpoint que se quiere, y el status que se devolvio, y el body
    // JUnit | Mockito => Juntos

}

