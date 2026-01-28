package com.taskmanager.app.controller;

import com.taskmanager.app.entity.TaskEntity;
import com.taskmanager.app.entity.UserEntity;
import com.taskmanager.app.service.TaskService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskRestController {

    private final TaskService taskService;

    public TaskRestController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<TaskEntity>> getAllTasks(){
        List<TaskEntity> tasks = taskService.getAllTasks();
            return ResponseEntity.ok(tasks);
        }

    @GetMapping("/{id}")
    public ResponseEntity<TaskEntity> findById(@PathVariable Long id){
        try {
            TaskEntity task = taskService.findById(id);
            return ResponseEntity.ok(task);
        }catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/overdue")
    public List<TaskEntity> overdue(HttpSession session){
        UserEntity user = (UserEntity) session.getAttribute("LOGGED_IN_USER");
        return taskService.getOverdueTasks(user, LocalDate.now());
    }

    @GetMapping("/today")
    public List<TaskEntity> today(HttpSession session){
        UserEntity user = (UserEntity) session.getAttribute("LOGGED_IN_USER");
        return taskService.getTodayTasks(user, LocalDate.now());
    }

    @GetMapping("/upcoming")
    public List<TaskEntity> upcoming(HttpSession session){
        UserEntity user = (UserEntity) session.getAttribute("LOGGED_IN_USER");
        return taskService.getUpcomingTasks(user, LocalDate.now());
    }


    @PostMapping
    public ResponseEntity<TaskEntity> createTask(
            @RequestBody TaskEntity task,
            HttpSession session
    ) {
        UserEntity user = (UserEntity) session.getAttribute("LOGGED_IN_USER");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        task.setUser(user);
        TaskEntity saved = taskService.saveTask(task);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }



    @PutMapping("/{id}")
    public ResponseEntity<TaskEntity> updateTask(@PathVariable Long id, @RequestBody TaskEntity UpdatedTask){
        try {
            TaskEntity existing = taskService.findById(id);

            existing.setName(UpdatedTask.getName());
            existing.setDescription(UpdatedTask.getDescription());
            existing.setPriority(UpdatedTask.getPriority());
            existing.setStatus(UpdatedTask.getStatus());
            existing.setDueDate(UpdatedTask.getDueDate());

            TaskEntity saved = taskService.saveTask(existing);
            return ResponseEntity.ok(saved);

        }catch (IllegalArgumentException ex){
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void>  deleteTask(@PathVariable Long id){
        try {
            taskService.deleteTask(id);
        }catch (Exception ex){
            return ResponseEntity.notFound().build();
        }
        return null;
    }



    @GetMapping("/calendar")
    public List<Map<String, Object>> calendarEvents() {

        return taskService.getAllTasks().stream()
                .filter(t -> t.getDueDate() != null)
                .map(t -> {
                    Map<String, Object> event = new HashMap<>();
                    event.put("id",t.getId());
                    event.put("title", t.getName());
                    event.put("start", t.getDueDate().toString());
                    switch (t.getStatus()) {
                        case DONE -> {
                            event.put("backgroundColor", "#16a34a");
                            event.put("borderColor", "#16a34a");
                        }
                        case IN_PROGRESS -> {
                            event.put("backgroundColor", "#f59e0b");
                            event.put("borderColor", "#f59e0b");
                        }
                        default -> {
                            event.put("backgroundColor", "#3b82f6");
                            event.put("borderColor", "#3b82f6");
                        }
                    }
                    return event;
                })
                .toList();
    }

}
