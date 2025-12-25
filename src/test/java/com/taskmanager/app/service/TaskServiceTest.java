package com.taskmanager.app.service;

import com.taskmanager.app.entity.TaskEntity;
import com.taskmanager.app.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private TaskEntity createTask(Long id, TaskEntity.Status status) {
        TaskEntity task = new TaskEntity();
        task.setId(id);
        task.setName("Test Task");
        task.setDescription("Test Desc");
        task.setPriority("HIGH");
        task.setStatus(status);
        task.setCreatedAt(LocalDateTime.now());
        return task;
    }

    @Test
    void getAllTasks_ShouldReturnTaskList(){
        List<TaskEntity> mockTasks = List.of(createTask(1L,TaskEntity.Status.PENDING),
                createTask(2L,TaskEntity.Status.DONE));
        when(taskRepository.findAll()).thenReturn(mockTasks);
        List<TaskEntity> result = taskService.getAllTasks();

        assertEquals(2,result.size());
        verify(taskRepository,times(1)).findAll();
    }

    @Test
    void saveTask_shouldSaveAndReturnTask() {
        TaskEntity task = createTask(null, TaskEntity.Status.PENDING);

        when(taskRepository.save(any(TaskEntity.class)))
                .thenAnswer(invocation -> {
                    TaskEntity saved = invocation.getArgument(0);
                    saved.setId(1L);
                    return saved;
                });

        TaskEntity savedTask = taskService.saveTask(task);

        assertNotNull(savedTask.getId());
        verify(taskRepository).save(task);
    }

    @Test
    void markTaskAsDone_shouldUpdateStatusAndCompletedAt() {
        TaskEntity task = createTask(1L, TaskEntity.Status.PENDING);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(task);

        taskService.marksDone(1L);

        assertEquals(TaskEntity.Status.DONE, task.getStatus());
        assertNotNull(task.getCompletedAt());

        verify(taskRepository).findById(1L);
        verify(taskRepository).save(task);
    }

    @Test
    void deleteTask_shouldCallRepositoryDelete() {
        doNothing().when(taskRepository).deleteById(1L);

        taskService.deleteTask(1L);

        verify(taskRepository, times(1)).deleteById(1L);
    }
    @Test
    void markTaskAsDone_shouldThrowIfTaskNotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> taskService.marksDone(99L));
    }





}
