package com.taskmanager.app.controller;

import com.taskmanager.app.entity.UserEntity;
import com.taskmanager.app.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }
    private boolean isLoggedIn(HttpSession session) {
        return session != null && session.getAttribute("LOGGED_IN_USER") != null;
    }




    @GetMapping("/")
    public String root(HttpSession session) {
        if (session.getAttribute("LOGGED_IN_USER") == null) {
            return "redirect:/auth/register";
        }
        return "redirect:/tasks/list";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(UserEntity user, Model model) {
        try {
            userService.register(user);
            model.addAttribute("email", user.getEmail());
            return "redirect:/auth/verify?email=" + user.getEmail();
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/verify")
    public String verifyPage(@RequestParam String email,Model model){
        model.addAttribute("email",email);
        return "verify_otp";
    }



    @PostMapping("/verify_otp")
    public String verifyOtp(
            @RequestParam String email,
            @RequestParam String otp,
            HttpSession session,
            Model model
    ) {
        try {
            UserEntity user = userService.verifyOtp(email, otp);
            session.setAttribute("LOGGED_IN_USER", user);
            return "redirect:/tasks/list";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", e.getMessage());
            model.addAttribute("email", email);
            return "verify_otp";
        }
    }



    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model
    ) {
        try {
            UserEntity user = userService.login(email, password);
            session.setAttribute("LOGGED_IN_USER", user);
            return "redirect:/tasks/list";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", e.getMessage());
            return "login";
        }
    }



    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/auth/login";
    }
}
