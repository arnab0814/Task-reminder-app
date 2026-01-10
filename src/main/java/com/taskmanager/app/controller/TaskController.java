package com.taskmanager.app.controller;

import com.taskmanager.app.entity.TaskEntity;
import com.taskmanager.app.service.TaskService;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    private boolean notLoggedIn(HttpSession session) {
        return session.getAttribute("LOGGED_IN_USER") == null;
    }

    private boolean isLoggedIn(HttpSession session) {
        return session != null && session.getAttribute("LOGGED_IN_USER") != null;
    }




    @GetMapping("/new")
    public String showCreateForm(HttpSession session,Model model){
        if (session.getAttribute("LOGGED_IN_USER") == null) {
            return "redirect:/auth/register";
        }

        TaskEntity task = new TaskEntity();
        model.addAttribute("task",new TaskEntity());
        if (!model.containsAttribute("task")){
            model.addAttribute("task",new TaskEntity());
        }
        return "add_task";
    }



    @PostMapping("/save")
    public String saveTask(@ModelAttribute("task") TaskEntity task, RedirectAttributes redirectAttributes,HttpSession session){
        if (session.getAttribute("LOGGED_IN_USER") == null) {
            return "redirect:/auth/register";
        }

        taskService.saveTask(task);
        redirectAttributes.addFlashAttribute("SucessMessage","Task Saved SuessFully!");
        redirectAttributes.addFlashAttribute("task",new TaskEntity());
        return "redirect:/tasks/list";
    }

    @GetMapping("/list")
    public String listTasks(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "sortField", defaultValue = "id") String sortField,
            @RequestParam(name = "sortDir", defaultValue = "ASC") String sortDir,
            @RequestParam(name = "status", required = false) TaskEntity.Status status,
            @RequestParam(name = "priority", required = false) String priority,
            @RequestParam(name = "title", required = false) String title,
            HttpSession session,
            Model model) {

        if (!isLoggedIn(session)) {
            return "redirect:/auth/register";
        }

        Page<TaskEntity> taskPage = taskService.getFilterTasks(page, size, sortField, sortDir, status, priority, title);



        model.addAttribute("taskPage", taskPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", taskPage.getTotalPages());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);

        model.addAttribute("filterStatus", status);
        model.addAttribute("filterPriority", priority);
        model.addAttribute("filterTitle", title);


        return "task";
    }



    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model,HttpSession session){
        if (session.getAttribute("LOGGED_IN_USER") == null) {
            return "redirect:/auth/register";
        }

        TaskEntity task = taskService.findById(id);
        if (task.getStatus() == TaskEntity.Status.DONE) {
            return "redirect:/tasks/list";
        }
        model.addAttribute("task",task);
        return "edit_task";

    }
    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable("id") Long id,RedirectAttributes redirectAttributes,HttpSession session){
        if (session.getAttribute("LOGGED_IN_USER") == null) {
            return "redirect:/auth/register";
        }
        taskService.deleteTask(id);
       redirectAttributes.addFlashAttribute("sucessMessage","Task Deleted Sucess Fully!");
       redirectAttributes.addFlashAttribute("task",new TaskEntity());
       return "redirect:/tasks/list";

    }
    @GetMapping("/done/{id}")
    public String markDone(@PathVariable("id") Long id,RedirectAttributes redirectAttributes,HttpSession session){
        if (session.getAttribute("LOGGED_IN_USER") == null) {
            return "redirect:/auth/register";
        }

        try {
            taskService.marksDone(id);
            redirectAttributes.addFlashAttribute("Sucess Message","Task Marked Done.");
        }catch (IllegalArgumentException ex){
            redirectAttributes.addFlashAttribute("errorMessage ","Task Not Found");
        }
        return "redirect:/tasks/list";
    }

    @GetMapping("/view/{id}")
    public String viewTask(@PathVariable Long id, Model model,HttpSession session){
        if (session.getAttribute("LOGGED_IN_USER") == null) {
            return "redirect:/auth/register";
        }

        TaskEntity task = taskService.getTaskById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        model.addAttribute("task",task);
        return "task_view";
    }

    @GetMapping("/{id}")
    public Optional<TaskEntity> getTask(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }




}
