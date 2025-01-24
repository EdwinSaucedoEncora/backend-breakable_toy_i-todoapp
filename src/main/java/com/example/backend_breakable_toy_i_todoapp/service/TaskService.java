package com.example.backend_breakable_toy_i_todoapp.service;

import com.example.backend_breakable_toy_i_todoapp.config.*;
import com.example.backend_breakable_toy_i_todoapp.dao.TaskDAO;
import com.example.backend_breakable_toy_i_todoapp.model.AverageDetails;
import com.example.backend_breakable_toy_i_todoapp.model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
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
            throw new TaskNotFoundException("Task with ID " + id + " does not exist.");
        }

        return task;
    }

    public ResponseEntity<String> deleteTaskById(UUID id) {
        if (taskDAO.hasTask(id)) {
            throw new TaskNotFoundException("Task not found with ID: " + id);
        }

        taskDAO.deleteTask(id);
        return new ResponseEntity<>("Task " + id.toString() + " was deleted!", HttpStatus.OK);
    }

    public ResponseEntity<String> updateTask(UUID id, Task updatedTask) {
        // Check if the priority is valid
        if (!priorities.contains(updatedTask.getPriority())) {
            throw new InvalidPriorityException("Invalid priority: " + updatedTask.getPriority());
        }

        // Check if the task exists
        if (taskDAO.hasTask(id)) {
            throw new TaskNotFoundException("Task not found with ID: " + id);
        }

        // Assuming there are required fields to be checked, uncommenting the validation
        if (updatedTask.getName() == null || updatedTask.getPriority() == null) {
            throw new MissingTaskFieldsException("Task is missing required fields (name or priority).");
        }

        // Update the task
        taskDAO.updateTask(id, updatedTask);
        return new ResponseEntity<>("Task " + id.toString() + " updated.", HttpStatus.OK);
    }

    public ResponseEntity<String> addTask(Task newTask) {
        // Check if the task has required fields
        if (newTask.hasRequiredFields()) {
            throw new MissingTaskFieldsException("Task has missing required fields.");
        }

        // Check if the priority is valid
        if (!priorities.contains(newTask.getPriority())) {
            throw new InvalidTaskException("Invalid priority: " + newTask.getPriority());
        }

        // Add the task
        taskDAO.addTask(newTask);
        return new ResponseEntity<>("Task added successfully.", HttpStatus.CREATED);
    }

    public ResponseEntity<String> setDoneDateById(UUID id) {
        // Check if the task exists
        if (taskDAO.hasTask(id)) {
            throw new TaskNotFoundException("Task not found with ID: " + id);
        }

        // Set the done date for the task
        taskDAO.setDoneDate(id);
        return new ResponseEntity<>("Task done date applied.", HttpStatus.OK);
    }

    public ResponseEntity<String> unsetDoneDateById(UUID id) {
        // Check if the task exists
        if (taskDAO.hasTask(id)) {
            throw new TaskNotFoundException("Task not found with ID: " + id);
        }

        // Unset the done date for the task
        taskDAO.unsetDoneDate(id);
        return new ResponseEntity<>("Task done date removed.", HttpStatus.OK);
    }

    public ResponseEntity<AverageDetails> getAverageDetails() {
        try {
            // Retrieve tasks from the DAO
            List<Task> taskList = taskDAO.getTasksForAverageDetails();

            // Check if taskList is empty or null
            if (taskList == null || taskList.isEmpty()) {
                throw new TaskDataException("No tasks found with valid createdAt and doneDate.");
            }

            // Calculate average details
            AverageDetails averageDetails = calculateAverageDetails(taskList);

            return new ResponseEntity<>(averageDetails, HttpStatus.OK);
        } catch (TaskDataException | InvalidTaskDataException | CalculationException ex) {
            // Re-throw the exception for global handling
            throw ex;
        } catch (Exception ex) {
            // Catch all other unexpected exceptions
            throw new RuntimeException("An unexpected error occurred: " + ex.getMessage(), ex);
        }
    }

    private AverageDetails calculateAverageDetails(List<Task> taskList) {
        try {
            // Same calculation logic as before
            AtomicLong highCount = new AtomicLong();
            AtomicLong highAverage = new AtomicLong();
            AtomicLong mediumCount = new AtomicLong();
            AtomicLong mediumAverage = new AtomicLong();
            AtomicLong lowCount = new AtomicLong();
            AtomicLong lowAverage = new AtomicLong();

            Comparator<Task> comparator = Comparator.comparing(Task::getPriority);
            taskList.stream()
                    .sorted(comparator)
                    .forEach(task -> {
                        if ("high".equals(task.getPriority())) {
                            highCount.getAndIncrement();
                            highAverage.addAndGet(task.getDiffDays());
                        } else if ("medium".equals(task.getPriority())) {
                            mediumCount.getAndIncrement();
                            mediumAverage.addAndGet(task.getDiffDays());
                        } else if ("low".equals(task.getPriority())) {
                            lowCount.getAndIncrement();
                            lowAverage.addAndGet(task.getDiffDays());
                        } else {
                            // Invalid priority in task data
                            throw new InvalidTaskDataException("Task has an invalid priority: " + task.getPriority());
                        }
                    });

            double hAverage = highCount.get() > 0 ? (double) highAverage.get() / highCount.get() : 0;
            double mAverage = mediumCount.get() > 0 ? (double) mediumAverage.get() / mediumCount.get() : 0;
            double lAverage = lowCount.get() > 0 ? (double) lowAverage.get() / lowCount.get() : 0;

            double totalCount = highCount.get() + mediumCount.get() + lowCount.get();
            double totalAverage = totalCount > 0
                    ? (double) (highAverage.get() + mediumAverage.get() + lowAverage.get()) / totalCount
                    : 0;

            return new AverageDetails(hAverage, mAverage, lAverage, totalAverage);
        } catch (ArithmeticException ex) {
            throw new CalculationException("Error occurred during calculation: " + ex.getMessage());
        }
    }
}
