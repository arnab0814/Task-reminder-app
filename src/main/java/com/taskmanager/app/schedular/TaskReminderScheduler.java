package com.taskmanager.app.schedular;


import com.taskmanager.app.entity.TaskEntity;
import com.taskmanager.app.entity.UserEntity;
import com.taskmanager.app.repository.UserRepository;
import com.taskmanager.app.service.EmailService;
import com.taskmanager.app.service.TaskService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class TaskReminderScheduler {

    private final TaskService taskService;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public TaskReminderScheduler(
            TaskService taskService,
            UserRepository userRepository,
            EmailService emailService
    ) {
        this.taskService = taskService;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    // Runs every day at 9 AM
    @Scheduled(cron = "0 0 9 * * *")
    public void sendDailyTaskReminders() {

        List<TaskEntity> todayTasks =
                taskService.getAllTodayTasks();

        Map<UserEntity, List<TaskEntity>> tasksByUser =
                todayTasks.stream()
                        .collect(Collectors.groupingBy(TaskEntity::getUser));

        for (Map.Entry<UserEntity, List<TaskEntity>> entry : tasksByUser.entrySet()) {

            UserEntity user = entry.getKey();
            List<TaskEntity> tasks = entry.getValue();

            if (tasks.isEmpty()) continue;

            emailService.sendTaskReminderEmail(
                    user.getEmail(),
                    user.getName(),
                    tasks
            );
        }
    }
}
