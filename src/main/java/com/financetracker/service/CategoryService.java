
package com.financetracker.service;

import com.financetracker.exception.ResourceNotFoundException;
import com.financetracker.model.Category;
import com.financetracker.model.User;
import com.financetracker.repository.CategoryRepository;
import com.financetracker.repository.TransactionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final UserService userService;

    public CategoryService(
            CategoryRepository categoryRepository,
            TransactionRepository transactionRepository,
            UserService userService) {

        this.categoryRepository = categoryRepository;
        this.transactionRepository = transactionRepository;
        this.userService = userService;
    }

    @Transactional
    public Category createCategory(Integer userId, String name) {

        String normalizedName = validateName(name);

        User user = userService.getUserById(userId);

        if (categoryRepository.existsByUserIdAndNameIgnoreCase(
                userId, normalizedName)) {
            throw new IllegalArgumentException(
                    "Category already exists");
        }

        Category category = new Category();
        category.setUser(user);
        category.setName(normalizedName);

        return categoryRepository.save(category);
    }

    @Transactional(readOnly = true)
    public List<Category> getUserCategories(Integer userId) {

        userService.getUserById(userId);

        return categoryRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Category getCategoryById(Integer userId, Integer categoryId) {

        return categoryRepository
                .findByIdAndUserId(categoryId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found"));
    }

    @Transactional
    public Category updateCategory(
            Integer userId,
            Integer categoryId,
            String newName) {

        Category category = getCategoryById(userId, categoryId);
        String normalizedName = validateName(newName);

        if (!category.getName().equalsIgnoreCase(normalizedName)
                && categoryRepository.existsByUserIdAndNameIgnoreCase(
                userId, normalizedName)) {
            throw new IllegalArgumentException(
                    "Category name already exists");
        }

        category.setName(normalizedName);

        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Integer userId, Integer categoryId) {

        Category category = getCategoryById(userId, categoryId);

        if (transactionRepository.existsByCategoryId(categoryId)) {
            throw new IllegalStateException(
                    "Cannot delete a category with transactions");
        }

        categoryRepository.delete(category);
    }

    private String validateName(String name) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Category name is required");
        }

        String trimmedName = name.trim();

        if (trimmedName.length() > 100) {
            throw new IllegalArgumentException(
                    "Category name must not exceed 100 characters");
        }

        return trimmedName;
    }
}
