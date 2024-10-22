package com.example.backend_breakable_toy_i_todoapp.model;

import java.time.LocalDate;
import java.util.UUID;
import java.time.temporal.ChronoUnit;

public class Task {
    private final UUID id;
    private String name;
    private LocalDate dueDate;
    private LocalDate doneDate;
    private final LocalDate createdAt;
    private LocalDate updatedAt;
    private String priority;

    public Task(){
        this.createdAt = LocalDate.now();
        this.id = UUID.randomUUID();
        setUpdatedAt();
    }



    public Task(Task newTask){
        setPriority(newTask.getPriority());
        setName(newTask.getName());
        setDueDate(newTask.getDueDate());
        setUpdatedAt();
        setDoneDate();
        this.createdAt = newTask.createdAt;
        this.id = newTask.id;
    }
    public Task(String name, String priority){
        this.createdAt = LocalDate.now();
        this.id = UUID.randomUUID();
        setName(name);
        setPriority(priority);
        this.updatedAt = LocalDate.now();
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setDoneDate() {
        setUpdatedAt();
        this.doneDate = LocalDate.now();
    }

    public void setUpdatedAt() {
        this.updatedAt = LocalDate.now();
    }

    public void unsetDoneDate() {
        setUpdatedAt();
        this.doneDate = null;
    }

    public boolean hasRequiredFields(){
        return this.name == null ||
                this.priority == null ||
                this.createdAt == null ||
                this.updatedAt == null ||
                this.id == null;
    }
    public UUID getId() {
        return id;
    }

    public String getPriority() {
        return priority;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getDoneDate() {
        return doneDate;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public String getName() {
        return name;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public long getDiffDays(){
        if(dueDate == null || dueDate.isBefore(LocalDate.now())) {
            return 0;
        }
        return Math.abs(ChronoUnit.DAYS.between(dueDate, LocalDate.now()));
    }
}
