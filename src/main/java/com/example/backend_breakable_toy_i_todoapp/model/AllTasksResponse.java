package com.example.backend_breakable_toy_i_todoapp.model;

import java.util.List;

public class AllTasksResponse {
    public List<Task> tasks;
    public int total;
    public AllTasksResponse(List<Task>tasks, int total){
        this.tasks = tasks;
        this.total = total;
    }
}
