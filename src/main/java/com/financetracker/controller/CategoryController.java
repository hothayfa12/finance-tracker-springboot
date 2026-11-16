
package com.financetracker.controller;

import com.financetracker.dto.CategoryRequest;
import com.financetracker.model.Category;
import com.financetracker.model.User;
import com.financetracker.service.CategoryService;
import com.financetracker.service.UserService;

import jakarta.validation.Valid;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final UserService userService;

    public CategoryController(
            CategoryService categoryService,
            UserService userService) {

        this.categoryService = categoryService;
        this.userService = userService;
    }

    private Integer currentUserId(Authentication authentication) {
        User user = userService.getUserByEmail(
                authentication.getName());

        return user.getId();
    }

    // Display Categories
    @GetMapping
    public String listCategories(
            Authentication authentication,
            Model model) {

        Integer userId = currentUserId(authentication);

        model.addAttribute("categories",
                categoryService.getUserCategories(userId));

        if (!model.containsAttribute("categoryRequest")) {
            model.addAttribute("categoryRequest",
                    new CategoryRequest());
        }

        return "categories";
    }

    // Create Category
    @PostMapping
    public String createCategory(
            @Valid @ModelAttribute("categoryRequest")
            CategoryRequest request,
            BindingResult result,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        Integer userId = currentUserId(authentication);

        if (!result.hasErrors()) {
            try {
                categoryService.createCategory(
                        userId, request.getName());

            } catch (IllegalArgumentException |
                     DataIntegrityViolationException ex) {

                result.reject("category.error",
                        "Category already exists or is invalid");
            }
        }

        if (result.hasErrors()) {
            model.addAttribute("categories",
                    categoryService.getUserCategories(userId));

            return "categories";
        }

        redirectAttributes.addFlashAttribute(
                "successMessage", "Category created successfully");

        return "redirect:/categories";
    }

    // Display Edit Form
    @GetMapping("/{id}/edit")
    public String showEditForm(
            @PathVariable Integer id,
            Authentication authentication,
            Model model) {

        Integer userId = currentUserId(authentication);

        Category category =
                categoryService.getCategoryById(userId, id);

        model.addAttribute("categoryId", id);
        model.addAttribute("categoryRequest",
                new CategoryRequest(category.getName()));

        return "category-edit";
    }

    // Update Category
    @PostMapping("/{id}/edit")
    public String updateCategory(
            @PathVariable Integer id,
            @Valid @ModelAttribute("categoryRequest")
            CategoryRequest request,
            BindingResult result,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        Integer userId = currentUserId(authentication);

        if (!result.hasErrors()) {
            try {
                categoryService.updateCategory(
                        userId, id, request.getName());

            } catch (IllegalArgumentException |
                     DataIntegrityViolationException ex) {

                result.reject("category.error",
                        "Category name already exists or is invalid");
            }
        }

        if (result.hasErrors()) {
            model.addAttribute("categoryId", id);
            return "category-edit";
        }

        redirectAttributes.addFlashAttribute(
                "successMessage", "Category updated successfully");

        return "redirect:/categories";
    }

    // Delete Category
    @PostMapping("/{id}/delete")
    public String deleteCategory(
            @PathVariable Integer id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        Integer userId = currentUserId(authentication);

        try {
            categoryService.deleteCategory(userId, id);

            redirectAttributes.addFlashAttribute(
                    "successMessage", "Category deleted successfully");

        } catch (IllegalStateException |
                 DataIntegrityViolationException ex) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Cannot delete a category that has transactions");
        }

        return "redirect:/categories";
    }
}
