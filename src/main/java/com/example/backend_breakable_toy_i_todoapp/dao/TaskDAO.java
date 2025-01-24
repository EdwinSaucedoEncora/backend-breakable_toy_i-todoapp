package com.example.backend_breakable_toy_i_todoapp.dao;

import com.example.backend_breakable_toy_i_todoapp.model.AverageDetails;
import com.example.backend_breakable_toy_i_todoapp.model.Task;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class TaskDAO implements TaskDAOInterface{
    private final LinkedHashMap<UUID, Task> tasks;
    public TaskDAO() {
        tasks = new LinkedHashMap<>();
        loadTasksFromFile();
    }

    private void loadTasksFromFile() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("tasks.txt"))))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String name = parts[0].trim();
                    String priority = parts[1].trim();
                    Task task = new Task(name, priority);
                    tasks.put(task.getId(), task);
                }
            }
        } catch (IOException | NullPointerException e) {
            throw new RuntimeException("Failed to load tasks from file: " + "tasks.txt", e);
        }
    }

    public UUID addTask(Task newTask) {
        if (newTask == null || newTask.getName() == null || newTask.getPriority() == null) {
            throw new IllegalArgumentException("Task and its required fields must not be null");
        }
        tasks.put(newTask.getId(), newTask);
        return newTask.getId();
    }

    public LinkedHashMap<UUID, Task> getAll() {
        return tasks;
    }

    public Task getTask(UUID id) {
        if (!tasks.containsKey(id)) {
            throw new NoSuchElementException("Task with ID " + id + " not found");
        }
        return tasks.get(id);
    }

    public void deleteTask(UUID id) {
        if (!tasks.containsKey(id)) {
            throw new NoSuchElementException("Task with ID " + id + " not found");
        }
        tasks.remove(id);
    }

    public void updateTask(UUID id, Task updatedTask) {
        if (!tasks.containsKey(id)) {
            throw new NoSuchElementException("Task with ID " + id + " not found");
        }
        tasks.replace(id, updatedTask);
    }

    public boolean hasTask(UUID id){
        return tasks.containsKey(id);
    }

    public void setDoneDate(UUID id){
        Task targetTask = tasks.get(id);
        targetTask.setDoneDate();
    }
    public void unsetDoneDate(UUID id){
        Task targetTask = tasks.get(id);
        targetTask.unsetDoneDate();
    }

    public AverageDetails getAverageDetails(){
        AtomicLong highCount = new AtomicLong();
        AtomicLong highAverage = new AtomicLong();
        AtomicLong mediumCount = new AtomicLong();
        AtomicLong mediumAverage = new AtomicLong();;
        AtomicLong lowCount = new AtomicLong();;
        AtomicLong lowAverage = new AtomicLong();;
        List<Task> taskList = tasks.values().stream().filter(task -> task.getCreatedAt() != null && task.getDoneDate() != null).toList();
        Comparator<Task> comparator = (t1, t2) -> t1.getPriority().compareTo(t2.getPriority());
        taskList.stream().sorted(comparator).forEach(task -> {
            if(task.getPriority().equals("high")){
                highCount.getAndIncrement();
                highAverage.addAndGet(task.getDiffDays());
            }
            if(task.getPriority().equals("medium")){
                mediumCount.getAndIncrement();
                mediumAverage.addAndGet(task.getDiffDays());
            }
            if(task.getPriority().equals("low")){
                lowCount.getAndIncrement();
                lowAverage.addAndGet(task.getDiffDays());
            }
        });

        double hAverage = 0, mAverage = 0, lAverage = 0, totalAverage = 0;
        if(highCount.getPlain() > 0){
            hAverage = (double) highAverage.getPlain() /highCount.getPlain();
        }
        if(mediumCount.getPlain() > 0){
            mAverage = (double) mediumAverage.getPlain() /mediumCount.getPlain();
        }
        if(lowCount.getPlain() > 0){
            lAverage = (double) lowAverage.getPlain() /lowCount.getPlain();
        }

        double totalAverageDivider = (highCount.getPlain() + mediumCount.getPlain() + lowCount.getPlain());
        totalAverageDivider = totalAverageDivider > 0 ? totalAverageDivider : 1;
        totalAverage = (double) ((highAverage.getPlain() + mediumAverage.getPlain() + lowAverage.getPlain()) / totalAverageDivider);

        return new AverageDetails(hAverage, mAverage, lAverage, totalAverage);
    }
}