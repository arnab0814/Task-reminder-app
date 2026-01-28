package com.taskmanager.app.service;

import com.taskmanager.app.entity.TaskEntity;
import com.taskmanager.app.entity.UserEntity;
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

    public Page<TaskEntity> getFilteredTasksForUser(
            UserEntity user,
            Integer page,
            Integer size,
            String sortField,
            String sortDir,
            TaskEntity.Status status,
            String priority,
            String title
    ) {
        Pageable pageable = PageRequest.of(
                page == null || page < 0 ? 0 : page,
                size == null || size <= 0 ? 5 : size,
                Sort.by(Sort.Direction.fromString(sortDir), sortField)
        );

        Specification<TaskEntity> spec =
                (root, query, cb) -> cb.equal(root.get("user"), user);

        spec = spec.and(Specs.hasStatus(status))
                .and(Specs.hasPriority(priority))
                .and(Specs.titleContains(title));

        return taskRepository.findAll(spec, pageable);
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
    public List<TaskEntity> getOverdueTasks(
            UserEntity user,
            LocalDate today
    ) {
        return taskRepository
                .findByUserAndDueDateBeforeAndStatusNot(
                        user,
                        today,
                        TaskEntity.Status.DONE
                );
    }

    public List<TaskEntity> getTodayTasks(
            UserEntity user,
            LocalDate today
    ) {
        return taskRepository.findByUserAndDueDate(user, today);
    }

    public List<TaskEntity> getUpcomingTasks(
            UserEntity user,
            LocalDate today
    ) {
        return taskRepository.findByUserAndDueDateAfter(user, today);
    }


    public long countAll(){
        return taskRepository.count();
    }

    public long countUpcomingTasks(UserEntity user, LocalDate today) {
        return taskRepository.countByUserAndDueDateAfterAndStatusNot(
                user,
                today,
                TaskEntity.Status.DONE
        );
    }
    public Page<TaskEntity> getUpcomingTasksPage(
            UserEntity user,
            LocalDate today,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("dueDate").ascending());

        return taskRepository.findByUserAndDueDateAfterAndStatusNot(
                user,
                today,
                TaskEntity.Status.DONE,
                pageable
        );
    }



    public long countByUser(UserEntity user) {
        return taskRepository.countByUser(user);
    }

    public long countByUserAndStatus(UserEntity user, TaskEntity.Status status) {
        return taskRepository.countByUserAndStatus(user, status);
    }
    public List<TaskEntity> getHighPriorityTasks(UserEntity user) {
        return taskRepository
                .findByUserAndPriority(user, "HIGH");
    }

    public List<TaskEntity> getTodayPendingTasks(UserEntity user) {
        return taskRepository.findByUserAndDueDateAndStatusNot(
                user,
                LocalDate.now(),
                TaskEntity.Status.DONE
        );
    }

    public List<TaskEntity> getUpcomingTasksForReminder(UserEntity user) {
        return taskRepository.findByUserAndDueDateAfterAndStatusNot(
                user,
                LocalDate.now(),
                TaskEntity.Status.DONE
        );
    }

    public List<TaskEntity> getAllTodayTasks() {
        return taskRepository.findByDueDateAndStatusNot(
                LocalDate.now(),
                TaskEntity.Status.DONE
        );
    }

    public List<TaskEntity> getAllTasksForUser(UserEntity user) {
        return taskRepository.findByUser(user);
    }









}
