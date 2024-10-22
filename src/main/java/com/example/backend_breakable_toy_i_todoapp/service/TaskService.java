package com.example.backend_breakable_toy_i_todoapp.service;

import com.example.backend_breakable_toy_i_todoapp.dao.TaskDAO;
import com.example.backend_breakable_toy_i_todoapp.model.AllTasksResponse;
import com.example.backend_breakable_toy_i_todoapp.model.AverageDetails;
import com.example.backend_breakable_toy_i_todoapp.model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class TaskService implements  TaskServiceInterface{
    @Autowired
    private TaskDAO taskDAO;
    protected final int PAGE_SIZE = 10;
    protected final List<String> priorities = List.of("high", "medium", "low");

    public AllTasksResponse getAllTasks(String status, String name, String priority, Integer page){
        List <Task> tasks = new ArrayList<Task>(taskDAO.getAll().values());
        int size = tasks.size();
        if(status != null){
            // Filter by status
            if(status.equals("done")){
                tasks = tasks.stream().filter(task -> task.getDoneDate() != null).toList();
            }
            else if(status.equals("undone")) {
                tasks = tasks.stream().filter(task -> task.getDoneDate() == null).toList();
            }
        }
        if(priority != null){
            // Filter by priority
            if(priority.equals("high")){
                tasks = tasks.stream().filter(task -> task.getPriority().equals("high")).toList();
            }
            else if(priority.equals("medium")) {
                tasks = tasks.stream().filter(task -> task.getPriority().equals("medium")).toList();
            }
            else if(priority.equals("low")) {
                tasks = tasks.stream().filter(task -> task.getPriority().equals("low")).toList();
            }
        }
        if(name != null){
           tasks = tasks.stream()
                   .filter(task -> task.getName().toLowerCase().contains(name.toLowerCase()))
                   .toList();

        }
        // Used for autoincremental index;
        AtomicInteger index = new AtomicInteger();
        // Get autoincrement index and filter by pagination
        List<Task> filtered = tasks.stream()
                .map(el -> index.getAndIncrement())
                .filter(i -> (i < (page * PAGE_SIZE) && i >= ((page - 1) * PAGE_SIZE)))
                .map(tasks::get).toList();
        AllTasksResponse tasksResponse  = new AllTasksResponse(filtered, size);
        return tasksResponse;
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
//        if(updatedTask.hasRequiredFields()){
//            return new ResponseEntity<>("Provided task has missing properties", HttpStatus.BAD_REQUEST);
//        }
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

    public ResponseEntity<AverageDetails> getAverageDetails(){
        return new ResponseEntity<>(taskDAO.getAverageDetails(), HttpStatus.OK);
    }
}
