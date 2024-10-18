package com.example.backend_breakable_toy_i_todoapp.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.time.LocalDate;
import java.util.UUID;

// !Developer notes
// !Ponerlo en el dao como una entidad (entity) dao/entity
// !Notacion para evitar hacer getters y setters <- lombok
// !Para operaciones de fechas utilizar Date

public class Task {
    private static final Log log = LogFactory.getLog(Task.class);
    private UUID id;
    private String name;
    private LocalDate dueDate;
    private LocalDate doneDate;
    private final LocalDate createdAt;
    private LocalDate updatedAt;
    private String priority;

    public Task(){
        this.createdAt = LocalDate.now();
        this.id = UUID.randomUUID();
    }

    // Task constructor when no dueDate is provided
    public Task(String name, String priority){
        this.id = UUID.randomUUID();
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
        this.name = name;
        this.priority = priority;
    }
    public Task(String name, String priority, LocalDate dueDate){
        this.id = UUID.randomUUID();
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
        this.name = name;
        this.priority = priority;
        this.dueDate = dueDate;
    }

    private void setUpdatedAt(){
        this.updatedAt = LocalDate.now();
    }

    public void setDueDate(LocalDate dueDate){
        this.dueDate = dueDate;
    }
    public void setDone(){
        doneDate = LocalDate.now();
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public void setID(UUID id){
        this.id = id;
    }

    public void setName(String name){
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public String getPriority() {
        return priority;
    }

    public LocalDate getDoneDate() {
        return doneDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setDoneDate(LocalDate doneDate) {
        this.doneDate = doneDate;
    }

    public void updateTask(Task newTask){
        System.out.println("Hello");
        setPriority(newTask.getPriority());
        setName(newTask.getName());
        setDueDate(newTask.getDueDate());
        setUpdatedAt();
        setDoneDate(newTask.getDoneDate());
        log.debug(this);
    }
}
