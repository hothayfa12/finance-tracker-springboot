
package com.financetracker.controller;

import com.financetracker.dto.RegisterRequest;
import com.financetracker.service.UserService;

import jakarta.validation.Valid;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login(Authentication authentication) {

        if (isAuthenticated(authentication)) {
            return "redirect:/dashboard";
        }

        return "login";
    }

    @GetMapping("/register")
    public String showRegistration(
            Model model,
            Authentication authentication) {

        if (isAuthenticated(authentication)) {
            return "redirect:/dashboard";
        }

        model.addAttribute("registerRequest",
                new RegisterRequest());

        return "register";
    }

    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("registerRequest")
            RegisterRequest request,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (request.getPassword() != null &&
                !request.getPassword().equals(
                        request.getConfirmPassword())) {

            result.rejectValue(
                    "confirmPassword",
                    "password.mismatch",
                    "Passwords do not match");
        }

        if (result.hasErrors()) {
            clearPasswords(request);
            return "register";
        }

        try {
            userService.registerUser(
                    request.getFirstName(),
                    request.getLastName(),
                    request.getEmail(),
                    request.getPassword());

        } catch (IllegalArgumentException ex) {

            result.reject("registration.error", ex.getMessage());
            clearPasswords(request);
            return "register";

        } catch (DataIntegrityViolationException ex) {

            result.reject("registration.error",
                    "Registration could not be completed. "
                            + "The email may already be registered.");
            clearPasswords(request);
            return "register";
        }

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Account created successfully. Please sign in.");

        return "redirect:/login";
    }

    private boolean isAuthenticated(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }

    private void clearPasswords(RegisterRequest request) {
        request.setPassword(null);
        request.setConfirmPassword(null);
    }
}
