package com.taskmanager.app.dashboard;

import com.taskmanager.app.entity.TaskEntity;
import com.taskmanager.app.entity.UserEntity;
import com.taskmanager.app.service.DashboardService;
import com.taskmanager.app.service.TaskService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final TaskService taskService;

    public DashboardController(DashboardService dashboardService, TaskService taskService) {
        this.dashboardService = dashboardService;
        this.taskService = taskService;
    }

    @GetMapping
    public String dashboard(HttpSession session, Model model) {

        UserEntity user = (UserEntity) session.getAttribute("LOGGED_IN_USER");
        if (user == null) {
            return "redirect:/auth/login";
        }

        long total = taskService.countByUser(user);
        long done = taskService.countByUserAndStatus(user, TaskEntity.Status.DONE);
        long pending = taskService.countByUserAndStatus(user, TaskEntity.Status.PENDING);
        long upcoming = taskService.countUpcomingTasks(user, LocalDate.now());

        int completionPercent = (total == 0) ? 0 : (int) ((done * 100) / total);
        int donePercent = (total == 0) ? 0 : (int)((done * 100.0) / total);
        model.addAttribute("donePercent", donePercent);


        model.addAttribute("donePercent", donePercent);

        // 🔥 EXACT attribute names
        model.addAttribute("totalTasks", total);
        model.addAttribute("pendingCount", pending);
        model.addAttribute("doneCount", done);
        model.addAttribute("upcomingCount", upcoming);
        model.addAttribute("completionPercent", completionPercent);

        return "dashboard";
    }


}



