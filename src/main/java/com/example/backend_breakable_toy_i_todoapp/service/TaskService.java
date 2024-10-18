package com.example.backend_breakable_toy_i_todoapp.service;

import com.example.backend_breakable_toy_i_todoapp.dao.TaskDAO;
import com.example.backend_breakable_toy_i_todoapp.model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

// Repository must be injected (DAO)
@Service
public class TaskService {
    @Autowired
    private TaskDAO taskDAO;

    public ArrayList<Task> getAllTasks(){
        return  taskDAO.getAll();
    }

    public Task getTaskById(UUID id){
        return  taskDAO.getTask(id);
    }

    public void deleteTaskById(UUID id){
        taskDAO.deleteTask(id);
    }
    public void addTask(Task newTask){
        taskDAO.addTask(newTask);
    }
    public void updateTask(Task updatedTask){
        taskDAO.updateTask(updatedTask);
    }
}
