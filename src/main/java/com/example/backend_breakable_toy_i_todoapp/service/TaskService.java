package com.example.backend_breakable_toy_i_todoapp.service;

import com.example.backend_breakable_toy_i_todoapp.dao.TaskDAO;
import com.example.backend_breakable_toy_i_todoapp.model.AllTasksResponse;
import com.example.backend_breakable_toy_i_todoapp.model.AverageDetails;
import com.example.backend_breakable_toy_i_todoapp.model.Task;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class TaskService implements  TaskServiceInterface{
    @Autowired
    private TaskDAO taskDAO;
    protected final int PAGE_SIZE = 10;
    protected final List<String> priorities = List.of("high", "medium", "low");

    public Page<Task> getAllTasks(String status, String name, String priority, Pageable pageable) {
        // Validate inputs

        if (pageable == null) {
            throw new IllegalArgumentException("Pageable must not be null.");
        }
        if (pageable.getPageNumber() < 0) {
            throw new IllegalArgumentException("Page number must be greater than or equal to 0.");
        }
        if (!List.of("all", "done", "undone").contains(status.toLowerCase())) {
            throw new IllegalArgumentException("Invalid status value. Allowed values are: all, done, undone.");
        }
        if (!List.of("all", "high", "medium", "low").contains(priority.toLowerCase())) {
            throw new IllegalArgumentException("Invalid priority value. Allowed values are: all, high, medium, low.");
        }
        if (name != null && name.length() > 255) {
            throw new IllegalArgumentException("Name filter is too long. Maximum length is 255 characters.");
        }

        try {
            // Override the page size with a fixed value
            Pageable fixedPageable = PageRequest.of(pageable.getPageNumber(), PAGE_SIZE, pageable.getSort());

            // Fetch all tasks
            List<Task> tasks = new ArrayList<>(taskDAO.getAll().values());
            if (tasks.isEmpty()) {
                throw new NoSuchElementException("No tasks found in the system.");
            }

            // Filter by status
            if (!"all".equalsIgnoreCase(status)) {
                tasks = tasks.stream().filter(task -> {
                    if ("done".equalsIgnoreCase(status)) {
                        return task.getDoneDate() != null;
                    } else if ("undone".equalsIgnoreCase(status)) {
                        return task.getDoneDate() == null;
                    }
                    return true;
                }).collect(Collectors.toList());
            }

            // Filter by priority
            if (!"all".equalsIgnoreCase(priority)) {
                tasks = tasks.stream()
                        .filter(task -> priority.equalsIgnoreCase(task.getPriority()))
                        .collect(Collectors.toList());
            }

            // Filter by name
            if (name != null && !name.isEmpty()) {
                tasks = tasks.stream()
                        .filter(task -> task.getName() != null && task.getName().toLowerCase().contains(name.toLowerCase()))
                        .collect(Collectors.toList());
            }

            // Apply sorting
            Sort sort = fixedPageable.getSort();
            if (sort.isSorted()) {
                Comparator<Task> comparator = sort.stream()
                        .map(order -> {
                            String property = order.getProperty();
                            Sort.Direction direction = order.getDirection();
                            Comparator<Task> propertyComparator;

                            // Define comparators for different fields
                            switch (property) {
                                case "name":
                                    propertyComparator = Comparator.comparing(Task::getName, String.CASE_INSENSITIVE_ORDER);
                                    break;
                                case "priority":
                                    propertyComparator = Comparator.comparing(task -> priorities.indexOf(task.getPriority()));
                                    break;
                                case "doneDate":
                                    propertyComparator = Comparator.comparing(Task::getDoneDate, Comparator.nullsLast(Comparator.naturalOrder()));
                                    break;
                                case "status":
                                    // Custom comparator for status: "done" tasks come first
                                    propertyComparator = Comparator.comparing(task -> task.getDoneDate() != null ? "done" : "undone");
                                    break;
                                default:
                                    throw new IllegalArgumentException("Invalid sort property: " + property);
                            }

                            return direction == Sort.Direction.DESC ? propertyComparator.reversed() : propertyComparator;
                        })
                        .reduce(Comparator::thenComparing)
                        .orElseThrow(() -> new IllegalStateException("No valid sort comparator found."));

                tasks.sort(comparator);
            }

            // Apply pagination
            int start = (int) fixedPageable.getOffset();
            int end = Math.min(start + fixedPageable.getPageSize(), tasks.size());
            if (start > tasks.size()) {
                return new PageImpl<>(Collections.emptyList(), fixedPageable, tasks.size());
            }

            List<Task> paginatedTasks = tasks.subList(start, end);
            return new PageImpl<>(paginatedTasks, fixedPageable, tasks.size());
        } catch (IllegalArgumentException e) {
            // Handle expected validation errors
            throw new IllegalArgumentException("Error while processing tasks: " + e.getMessage(), e);
        } catch (NoSuchElementException e) {
            // Handle empty task lists
            throw new NoSuchElementException("Error: " + e.getMessage());
        } catch (Exception e) {
            // Catch-all for unexpected errors
            throw new RuntimeException("An unexpected error occurred while fetching tasks.", e);
        }
    }

    public Task getTaskById(UUID id) {
        // Null check
        if (id == null) {
            throw new IllegalArgumentException("The provided task ID must not be null.");
        }

        // Attempt to fetch the task
        Task task = taskDAO.getTask(id);
        if (task == null) {
            throw new NoSuchElementException("Task with ID " + id + " does not exist.");
        }

        return task;
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
