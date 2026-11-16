
package com.financetracker.controller;

import com.financetracker.dto.TransactionRequest;
import com.financetracker.model.Transaction;
import com.financetracker.model.User;
import com.financetracker.service.CategoryService;
import com.financetracker.service.TransactionService;
import com.financetracker.service.UserService;

import jakarta.validation.Valid;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final CategoryService categoryService;
    private final UserService userService;

    public TransactionController(
            TransactionService transactionService,
            CategoryService categoryService,
            UserService userService) {

        this.transactionService = transactionService;
        this.categoryService = categoryService;
        this.userService = userService;
    }

    private Integer currentUserId(Authentication authentication) {
        User user = userService.getUserByEmail(
                authentication.getName());

        return user.getId();
    }

    private void addCategories(Model model, Integer userId) {
        model.addAttribute("categories",
                categoryService.getUserCategories(userId));
    }

    // 1. List Transactions
    @GetMapping
    public String listTransactions(
            Authentication authentication,
            Model model) {

        Integer userId = currentUserId(authentication);

        model.addAttribute("transactions",
                transactionService.getUserTransactions(userId));

        addCategories(model, userId);

        model.addAttribute("transactionRequest",
                new TransactionRequest());

        return "transactions";
    }

    // 2. Create Transaction
    @PostMapping
    public String createTransaction(
            @Valid @ModelAttribute("transactionRequest")
            TransactionRequest request,
            BindingResult result,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        Integer userId = currentUserId(authentication);

        if (!result.hasErrors()) {
            try {
                transactionService.createTransaction(
                        userId,
                        request.getCategoryId(),
                        request.getAmount(),
                        request.getType(),
                        request.getDate());

            } catch (IllegalArgumentException ex) {
                result.reject("transaction.error",
                        ex.getMessage());

            } catch (com.financetracker.exception
                             .ResourceNotFoundException ex) {
                result.reject("transaction.error",
                        "Selected category is not available");
            }
        }

        if (result.hasErrors()) {
            model.addAttribute("transactions",
                    transactionService.getUserTransactions(userId));

            addCategories(model, userId);
            return "transactions";
        }

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Transaction created successfully");

        return "redirect:/transactions";
    }

    // 3. Display Edit Form
    @GetMapping("/{id}/edit")
    public String showEditForm(
            @PathVariable Integer id,
            Authentication authentication,
            Model model) {

        Integer userId = currentUserId(authentication);

        Transaction transaction =
                transactionService.getTransactionById(userId, id);

        TransactionRequest request = new TransactionRequest();

        request.setCategoryId(transaction.getCategory().getId());
        request.setAmount(transaction.getAmount());
        request.setType(transaction.getType());
        request.setDate(transaction.getDate());

        model.addAttribute("transactionId", id);
        model.addAttribute("transactionRequest", request);

        addCategories(model, userId);

        return "transaction-edit";
    }

    // 4. Update Transaction
    @PostMapping("/{id}/edit")
    public String updateTransaction(
            @PathVariable Integer id,
            @Valid @ModelAttribute("transactionRequest")
            TransactionRequest request,
            BindingResult result,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        Integer userId = currentUserId(authentication);

        if (!result.hasErrors()) {
            try {
                transactionService.updateTransaction(
                        userId,
                        id,
                        request.getCategoryId(),
                        request.getAmount(),
                        request.getType(),
                        request.getDate());

            } catch (IllegalArgumentException ex) {
                result.reject("transaction.error",
                        ex.getMessage());

            } catch (com.financetracker.exception
                             .ResourceNotFoundException ex) {
                result.reject("transaction.error",
                        "Transaction or category not found");
            }
        }

        if (result.hasErrors()) {
            model.addAttribute("transactionId", id);
            addCategories(model, userId);

            return "transaction-edit";
        }

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Transaction updated successfully");

        return "redirect:/transactions";
    }

    // 5. Delete Transaction
    @PostMapping("/{id}/delete")
    public String deleteTransaction(
            @PathVariable Integer id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        Integer userId = currentUserId(authentication);

        transactionService.deleteTransaction(userId, id);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Transaction deleted successfully");

        return "redirect:/transactions";
    }
}
