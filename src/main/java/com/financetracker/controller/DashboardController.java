
package com.financetracker.controller;

import com.financetracker.model.User;
import com.financetracker.service.TransactionService;
import com.financetracker.service.UserService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final UserService userService;
    private final TransactionService transactionService;

    public DashboardController(
            UserService userService,
            TransactionService transactionService) {

        this.userService = userService;
        this.transactionService = transactionService;
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

        TransactionService.FinancialSummary summary =
                transactionService.getFinancialSummary(user.getId());

        model.addAttribute("firstName", user.getFirstName());
        model.addAttribute("summary", summary);

        return "dashboard";
    }
}
