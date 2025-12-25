package com.taskmanager.app.controller;

import com.taskmanager.app.entity.TaskEntity;
import com.taskmanager.app.service.TaskService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.net.URI;

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

    @PostMapping
    public ResponseEntity<TaskEntity> createTask(@RequestBody TaskEntity task){
        TaskEntity saved = taskService.saveTask(task);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/api/tasks"+saved.getId()));

        return new ResponseEntity<>(saved, headers, HttpStatus.CREATED); // 201
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

}
