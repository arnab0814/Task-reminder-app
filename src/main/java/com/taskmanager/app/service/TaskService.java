package com.taskmanager.app.service;

import com.taskmanager.app.entity.TaskEntity;
import com.taskmanager.app.repository.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;


    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public TaskEntity saveTask(TaskEntity task){
        return taskRepository.save(task);
    }

    public List<TaskEntity> getAllTasks(){
        return taskRepository.findAll();
    }

    public TaskEntity findById(Long id){
        return taskRepository.findById(id).orElseThrow(()->new RuntimeException("Task not found"));
    }
    public void deleteTask(long id){
        taskRepository.deleteById(id);
    }

    public Page<TaskEntity> getTasksPage(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        return taskRepository.findAll(pageable);
    }

    public TaskEntity marksDone(Long id){
        TaskEntity task = taskRepository.findById(id).orElseThrow(()->new IllegalArgumentException("Invalid Task ID: "+id));
        task.setStatus(TaskEntity.Status.DONE);
        return taskRepository.save(task);
    }
}
