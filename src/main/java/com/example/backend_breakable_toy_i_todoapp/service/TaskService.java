package com.example.backend_breakable_toy_i_todoapp.service;

import com.example.backend_breakable_toy_i_todoapp.dao.TaskDAO;
import com.example.backend_breakable_toy_i_todoapp.model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// Repository must be injected (DAO)
@Service
public class TaskService {
    @Autowired
    private TaskDAO taskDAO;

    public List<Task> getAllTasks(){
        return new ArrayList<Task>(taskDAO.getAll().values());
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
    public void updateTask(UUID id, Task updatedTask){
        taskDAO.updateTask(id, updatedTask);
    }
    //
}
