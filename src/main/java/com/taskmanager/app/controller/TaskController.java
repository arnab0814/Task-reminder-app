package com.taskmanager.app.controller;

import com.taskmanager.app.entity.TaskEntity;
import com.taskmanager.app.repository.TaskRepository;
import com.taskmanager.app.service.TaskService;
import org.springframework.scheduling.config.Task;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }


    @GetMapping("/new")
    public String showCreateForm(Model model){
        TaskEntity task = new TaskEntity();
        model.addAttribute("task",new TaskEntity());
        if (!model.containsAttribute("task")){
            model.addAttribute("task",new TaskEntity());
        }
        return "add_task";
    }



    @PostMapping("/save")
    public String saveTask(@ModelAttribute("task") TaskEntity task, RedirectAttributes redirectAttributes){
        taskService.SaveTask(task);
        redirectAttributes.addFlashAttribute("SucessMessage","Task Saved SuessFully!");
        redirectAttributes.addFlashAttribute("task",new TaskEntity());
        return "redirect:/tasks/list";
    }

    @GetMapping("list")
    public String listTasks(Model model){
        List<TaskEntity> tasks = taskService.getTaskById();
        model.addAttribute("tasks",tasks);
        return "task";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model){
        TaskEntity task = taskService.findById(id);
        model.addAttribute("task",task);
        return "edit_task";

    }
}
