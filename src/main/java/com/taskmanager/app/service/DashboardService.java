package com.taskmanager.app.service;

import com.taskmanager.app.entity.TaskEntity;
import com.taskmanager.app.entity.UserEntity;
import com.taskmanager.app.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.LocalDate;

@Service
public class DashboardService {

    private final TaskService taskService;
    private final TaskRepository taskRepository;

    public DashboardService(TaskService taskService, TaskRepository taskRepository) {
        this.taskService = taskService;
        this.taskRepository = taskRepository;
    }
    public long countHighPriorityTasks(UserEntity user) {
        return taskRepository.countByUserAndPriorityIgnoreCase(user, "HIGH");
    }

    public long countOverdueTasks(UserEntity user, LocalDate today) {
        return taskRepository.countByUserAndDueDateBeforeAndStatusNot(
                user, today, TaskEntity.Status.DONE
        );
    }


    public void populateDashboard(Model model, UserEntity user) {

        long total = taskService.countByUser(user);
        long done = taskService.countByUserAndStatus(user, TaskEntity.Status.DONE);
        long pending = taskService.countByUserAndStatus(user, TaskEntity.Status.PENDING);
        long upcoming = taskService.countUpcomingTasks(user, LocalDate.now());

        int completionPercent =
                total == 0 ? 0 : (int) ((done * 100) / total);

        // COUNTS
        model.addAttribute("totalTasks", total);
        model.addAttribute("pendingCount", pending);
        model.addAttribute("doneCount", done);
        model.addAttribute("upcomingCount", upcoming);
        model.addAttribute("completionPercent", completionPercent);

        // LISTS
        model.addAttribute("todayTasks",
                taskService.getTodayTasks(user, LocalDate.now()));

        model.addAttribute("overdueTasks",
                taskService.getOverdueTasks(user, LocalDate.now()));

        model.addAttribute("highPriorityTasks",
                taskService.getHighPriorityTasks(user));

        model.addAttribute("pendingTasks",
                taskService.getFilteredTasksForUser(
                        user, 0, 5, "dueDate", "ASC",
                        TaskEntity.Status.PENDING, null, null
                ).getContent());

        model.addAttribute("upcomingTasks",
                taskService.getUpcomingTasks(user, LocalDate.now()));

        model.addAttribute("doneTasks",
                taskService.getFilteredTasksForUser(
                        user, 0, 5, "completedAt", "DESC",
                        TaskEntity.Status.DONE, null, null
                ).getContent());

        // COUNTS
        model.addAttribute(
                "highPriorityCount",
                taskService.getHighPriorityTasks(user)
        );

        model.addAttribute(
                "overdueCount",
                taskService.getOverdueTasks(user, LocalDate.now())
        );


    }
}
