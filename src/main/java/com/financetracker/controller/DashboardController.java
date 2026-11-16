
package com.financetracker.controller;

import com.financetracker.model.User;
import com.financetracker.service.UserService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final UserService userService;

    public DashboardController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(
            Authentication authentication,
            Model model) {

        User user = userService.getUserByEmail(
                authentication.getName());

        model.addAttribute("firstName", user.getFirstName());
        model.addAttribute("fullName",
                user.getFirstName() + " " + user.getLastName());

        return "dashboard";
    }
}
