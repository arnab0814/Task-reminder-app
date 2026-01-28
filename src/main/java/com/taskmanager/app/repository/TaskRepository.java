package com.taskmanager.app.repository;

import com.taskmanager.app.entity.TaskEntity;
import com.taskmanager.app.entity.UserEntity;
import org.apache.catalina.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository
        extends JpaRepository<TaskEntity, Long> {

    // 📅 Calendar queries (USER-SCOPED)
    List<TaskEntity> findByUserAndDueDate(
            UserEntity user,
            LocalDate date
    );

    List<TaskEntity> findByUserAndDueDateAfter(
            UserEntity user,
            LocalDate date
    );

    List<TaskEntity> findByUserAndDueDateBeforeAndStatusNot(
            UserEntity user,
            LocalDate date,
            TaskEntity.Status status
    );

    // 📊 Dashboard
    long countByUser(UserEntity user);

    long countByUserAndStatus(
            UserEntity user,
            TaskEntity.Status status
    );
    long countByUserAndDueDateAfterAndStatusNot(
            UserEntity user,
            LocalDate date,
            TaskEntity.Status status
    );
    Page<TaskEntity> findByUserAndDueDateAfterAndStatusNot(
            UserEntity user,
            LocalDate date,
            TaskEntity.Status status,
            Pageable pageable
    );



    Page<TaskEntity> findAll(Specification<TaskEntity> spec, Pageable pageable);

    List<TaskEntity> findByUserAndPriority(UserEntity user, String priority);

    long countByUserAndPriorityIgnoreCase(UserEntity user, String priority);

    long countByUserAndDueDateBeforeAndStatusNot(
            UserEntity user,
            LocalDate date,
            TaskEntity.Status status
    );

    List<TaskEntity> findByUserAndDueDateAndStatusNot(
            UserEntity user,
            LocalDate date,
            TaskEntity.Status status
    );

    List<TaskEntity> findByUserAndDueDateAfterAndStatusNot(
            UserEntity user,
            LocalDate date,
            TaskEntity.Status status
    );

    List<TaskEntity> findByDueDateAndStatusNot(
            LocalDate date,
            TaskEntity.Status status
    );

    List<TaskEntity> findByUser(UserEntity user);




}

