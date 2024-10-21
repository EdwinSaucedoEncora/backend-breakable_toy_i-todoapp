package com.example.backend_breakable_toy_i_todoapp.service;

import com.example.backend_breakable_toy_i_todoapp.dao.TaskDAO;
import com.example.backend_breakable_toy_i_todoapp.model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TaskService {
    @Autowired
    private TaskDAO taskDAO;
    protected final int PAGE_SIZE = 10;
    protected final List<String> priorities = List.of("high", "medium", "low");

    public List<Task> getAllTasks(String filter, Integer page){
        List<String> filters = List.of(filter.split("&"));
        List <Task> tasks = new ArrayList<Task>(taskDAO.getAll().values());
        for(String filtered : filters){
            if(filtered.contains("status")){
                // Filter by status
                if(Arrays.asList(filtered.split("=")).contains("done")){
                    tasks = tasks.stream().filter(task -> task.getDoneDate() != null).toList();
                }
                else if(Arrays.asList(filtered.split("=")).contains("undone")) {
                    tasks = tasks.stream().filter(task -> task.getDoneDate() == null).toList();
                }
            }
            if(filtered.contains("priority")){
                // Filter by priority
                if(Arrays.asList(filtered.split("=")).contains("high")){
                    tasks = tasks.stream().filter(task -> task.getPriority().equals("high")).toList();
                }
                else if(Arrays.asList(filtered.split("=")).contains("medium")) {
                    tasks = tasks.stream().filter(task -> task.getPriority().equals("medium")).toList();
                }
                else if(Arrays.asList(filtered.split("=")).contains("low")) {
                    tasks = tasks.stream().filter(task -> task.getPriority().equals("low")).toList();
                }
            }
            if(filtered.contains("name")){
               String name = Arrays.asList(filtered.split("=")).get(1);
               if(name != null){
                   // Filter if contains similar name
                   tasks = tasks.stream()
                           .filter(task -> task.getName().toLowerCase().contains(name.toLowerCase()))
                           .toList();
               }
            }
        }
        // Used for autoincremental index;
        AtomicInteger index = new AtomicInteger();
        // Get autoincrement index and filter by pagination
        return tasks.stream()
                .map(el -> index.getAndIncrement())
                .filter(i -> (i < (page * PAGE_SIZE) && i >= ((page - 1) * PAGE_SIZE)))
                .map(tasks::get).toList();
    }

    public Task getTaskById(UUID id){
        return  taskDAO.getTask(id);
    }

    public ResponseEntity<String> deleteTaskById(UUID id){
        if(taskDAO.hasTask(id)){
            taskDAO.deleteTask(id);
            return new ResponseEntity<>("Task " + id.toString() + " was deleted!", HttpStatus.OK);
        }
        return  new ResponseEntity<>("Task not found", HttpStatus.NOT_FOUND);
    }
    public  ResponseEntity<String> addTask(Task newTask){
        if(newTask.hasRequiredFields()){
            return  new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        if(!priorities.contains(newTask.getPriority())){
            return  new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        return  new ResponseEntity<String>(taskDAO.addTask(newTask).toString(), HttpStatus.OK);
    }
    public ResponseEntity<String> updateTask(UUID id, Task updatedTask){
        if(updatedTask.hasRequiredFields()){
            return new ResponseEntity<>("Provided task has missing properties", HttpStatus.BAD_REQUEST);
        }
        if(!priorities.contains(updatedTask.getPriority())){
            return  new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
        if(!taskDAO.hasTask(id)){
            return  new ResponseEntity<String>("Task not found", HttpStatus.BAD_REQUEST);
        }
        taskDAO.updateTask(id, updatedTask);
        return new ResponseEntity<String>("Task " + id.toString() + " updated.", HttpStatus.OK);
    }

    public ResponseEntity<String> setDoneDateById(UUID id) {
        if(!taskDAO.hasTask(id)){
            return new ResponseEntity<>("Task not found for update", HttpStatus.BAD_REQUEST);
        }
        taskDAO.setDoneDate(id);
        return new ResponseEntity<>("Task done date applied.", HttpStatus.OK);
    }

    public ResponseEntity<String> unsetDoneDateById(UUID id) {
        if(!taskDAO.hasTask(id)){
            return new ResponseEntity<>("Task not found for update", HttpStatus.BAD_REQUEST);
        }
        taskDAO.unsetDoneDate(id);
        return new ResponseEntity<>("Task done date remove.", HttpStatus.OK);
    }
}
