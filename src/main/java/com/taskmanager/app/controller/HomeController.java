package com.taskmanager.app.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(HttpSession httpSession){
        if (httpSession.getAttribute("Logged_IN_USER") != null){
            return "redirect:task/list";
        }
        return "home";
    }
}
