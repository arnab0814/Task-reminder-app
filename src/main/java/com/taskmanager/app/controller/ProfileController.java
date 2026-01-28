package com.taskmanager.app.controller;

import com.taskmanager.app.entity.UserEntity;
import com.taskmanager.app.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String profile(HttpSession session, Model model) {

        UserEntity user =
                (UserEntity) session.getAttribute("LOGGED_IN_USER");

        if (user == null) return "redirect:/auth/login";

        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping("/update")
    public String updateProfile(
            @RequestParam String name,
            @RequestParam MultipartFile image,
            HttpSession session
    ) {

        UserEntity user =
                (UserEntity) session.getAttribute("LOGGED_IN_USER");

        if (user == null) return "redirect:/auth/login";

        userService.updateProfile(user, name, image);
        session.setAttribute("LOGGED_IN_USER", user);

        return "redirect:/profile";
    }
}

