package com.taskmanager.app.controller;

import com.taskmanager.app.entity.TaskEntity;
import com.taskmanager.app.repository.TaskRepository;
import com.taskmanager.app.service.TaskService;
import org.springframework.data.domain.Page;
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
        taskService.saveTask(task);
        redirectAttributes.addFlashAttribute("SucessMessage","Task Saved SuessFully!");
        redirectAttributes.addFlashAttribute("task",new TaskEntity());
        return "redirect:/tasks/list";
    }

    @GetMapping("list")
    public String listTasks(@RequestParam(name = "page",defaultValue = "0") int page,
            @RequestParam(name = "size",defaultValue = "10") int size,
            Model model){
        Page<TaskEntity> taskPage = taskService.getTasksPage(page, size);
        List<TaskEntity> tasks = taskService.getAllTasks();
        model.addAttribute("tasks",tasks);
        model.addAttribute("taskPage",taskPage);
        model.addAttribute("currentPage",page);
        model.addAttribute("pageSize",size);
        model.addAttribute("totalPages",taskPage.getTotalPages());
        return "task";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model){
        TaskEntity task = taskService.findById(id);
        model.addAttribute("task",task);
        return "edit_task";

    }
    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable("id") Long id,RedirectAttributes redirectAttributes){
       taskService.deleteTask(id);
       redirectAttributes.addFlashAttribute("sucessMessage","Task Deleted Sucess Fully!");
       redirectAttributes.addFlashAttribute("task",new TaskEntity());
       return "redirect:/tasks/list";

    }
    @GetMapping("/done/{id}")
    public String markDone(@PathVariable("id") Long id,RedirectAttributes redirectAttributes){
        try {
            taskService.marksDone(id);
            redirectAttributes.addFlashAttribute("Sucess Message","Task Marked Done.");
        }catch (IllegalArgumentException ex){
            redirectAttributes.addFlashAttribute("errorMessage ","Task Not Found");
        }
        return "redirect:/tasks/list";
    }
}
