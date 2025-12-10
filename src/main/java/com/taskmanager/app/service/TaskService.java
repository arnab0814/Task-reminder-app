package com.taskmanager.app.service;

import com.taskmanager.app.entity.TaskEntity;
import com.taskmanager.app.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;


    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public TaskEntity SaveTask(TaskEntity task){
        return taskRepository.save(task);
    }
    public List<TaskEntity> getTaskById(){
        return taskRepository.findAll();
    }

    public TaskEntity findById(Long id){
        return taskRepository.findById(id).orElseThrow(()->new RuntimeException("Task not found"));
    }
    public void delete(long id){
        taskRepository.deleteById(id);
    }
}
