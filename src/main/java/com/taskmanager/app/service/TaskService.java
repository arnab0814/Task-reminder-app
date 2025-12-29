package com.taskmanager.app.service;

import com.taskmanager.app.entity.TaskEntity;
import com.taskmanager.app.repository.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

//    public Optional<TaskEntity> getTaskById(Long id){
//        return taskRepository.findById(id);
//    }
    public Optional<TaskEntity> getTaskById(Long id) {
        return taskRepository.findById(id);
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
        task.setCompletedAt(LocalDateTime.now());
        return taskRepository.save(task);
    }

    public Page<TaskEntity> getFilterTasks(Integer page,Integer size, String sortField, String sortDir, TaskEntity.Status status, String priority, String title){
        int p = page == null || page <0 ? 0 : page;
        int s = size == null || size <=0 ? 5 : size;

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir == null ? "ASC " : sortDir.toUpperCase()),
                sortField == null ? "id" : sortField);

        Pageable pageable = PageRequest.of(p,s,sort);
        Specification<TaskEntity> spec = Specification.where(Specs.hasStatus(status))
                .and(Specs.hasPriority(priority))
                .and(Specs.titleContains(title));
        return taskRepository.findAll(spec,pageable);
    }

    public static class Specs {

        public static Specification<TaskEntity> hasStatus(TaskEntity.Status status) {
            return (root, query, cb) ->
                    status == null ? null : cb.equal(root.get("status"), status);
        }

        public static Specification<TaskEntity> hasPriority(String priority) {
            return (root, query, cb) -> {
                if (priority == null || priority.trim().isEmpty()) return null;
                return cb.equal(cb.upper(root.get("priority")), priority.trim().toUpperCase());
            };
        }

        public static Specification<TaskEntity> titleContains(String title) {
            return (root, query, cb) -> {
                if (title == null || title.trim().isEmpty()) return null;
                return cb.like(cb.lower(root.get("name")), "%" + title.trim().toLowerCase() + "%");
            };
        }
    }
    public List<TaskEntity> getOverdueTasks(LocalDate today) {
        return taskRepository
                .findByDueDateBeforeAndStatusNot(today, TaskEntity.Status.DONE);
    }

    public List<TaskEntity> getTodayTasks(LocalDate today) {
        return taskRepository.findByDueDate(today);
    }

    public List<TaskEntity> getUpcomingTasks(LocalDate today) {
        return taskRepository.findByDueDateAfter(today);
    }

    public List<TaskEntity> getTasksByDate(LocalDate date) {
        return taskRepository.findByDueDate(date);
    }


}
