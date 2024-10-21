package com.example.backend_breakable_toy_i_todoapp.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.UUID;

// !Developer notes
// !Ponerlo en el dao como una entidad (entity) dao/entity
// !Notacion para evitar hacer getters y setters <- lombok
// !Para operaciones de fechas utilizar Date

public class Task {
    private static final Logger logger = LoggerFactory.getLogger(Task.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    private UUID id;
    private String name;
    private LocalDate dueDate;
    private LocalDate doneDate;
    private final LocalDate createdAt;
    private LocalDate updatedAt;
    private String priority;

    protected void configureMapper(){
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public Task(){
        configureMapper();
        this.createdAt = LocalDate.now();
        this.id = UUID.randomUUID();
    }

    public Task(Task newTask){
        configureMapper();
        setPriority(newTask.getPriority());
        setName(newTask.getName());
        setDueDate(newTask.getDueDate());
        setUpdatedAt();
        setDoneDate(newTask.getDoneDate());
        this.createdAt = newTask.createdAt;
        this.id = newTask.id;
        logger.info(newTask.toString());
        logger.error("<-------->");

    }

    // Task constructor when no dueDate is provided
    public Task(String name, String priority){
        configureMapper();
        this.id = UUID.randomUUID();
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
        this.name = name;
        this.priority = priority;
    }
    public Task(String name, String priority, LocalDate dueDate){
        configureMapper();
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

    // Contructor => nueva instancia

    @Override
    public String toString() {
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
}
