package com.taskmanager.app.repository;

import com.taskmanager.app.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long>, JpaSpecificationExecutor<TaskEntity> {

    List<TaskEntity> findByDueDateBeforeAndStatusNot(
            LocalDate date,
            TaskEntity.Status status
    );

    List<TaskEntity> findByDueDate(LocalDate date);

    List<TaskEntity> findByDueDateAfter(LocalDate date);



}
