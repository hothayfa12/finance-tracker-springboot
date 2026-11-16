
package com.financetracker.service;

import com.financetracker.exception.ResourceNotFoundException;
import com.financetracker.model.Category;
import com.financetracker.model.Transaction;
import com.financetracker.model.User;
import com.financetracker.repository.TransactionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Service
public class TransactionService {

    private static final String INCOME = "INCOME";
    private static final String EXPENSE = "EXPENSE";

    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final CategoryService categoryService;

    public TransactionService(
            TransactionRepository transactionRepository,
            UserService userService,
            CategoryService categoryService) {

        this.transactionRepository = transactionRepository;
        this.userService = userService;
        this.categoryService = categoryService;
    }

    // 1. Create Transaction
    @Transactional
    public Transaction createTransaction(
            Integer userId,
            Integer categoryId,
            BigDecimal amount,
            String type,
            LocalDate date) {

        validateTransaction(amount, type, date);

        User user = userService.getUserById(userId);

        Category category = categoryService.getCategoryById(
                userId, categoryId);

        Transaction transaction = new Transaction();

        transaction.setUser(user);
        transaction.setCategory(category);
        transaction.setAmount(amount);
        transaction.setType(normalizeType(type));
        transaction.setDate(date);

        return transactionRepository.save(transaction);
    }

    // 2. Get All User Transactions
    @Transactional(readOnly = true)
    public List<Transaction> getUserTransactions(Integer userId) {

        userService.getUserById(userId);

        return transactionRepository
                .findByUserIdOrderByDateDesc(userId);
    }

    // 3. Get Single Transaction
    @Transactional(readOnly = true)
    public Transaction getTransactionById(
            Integer userId, Integer transactionId) {

        return transactionRepository
                .findByIdAndUserId(transactionId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Transaction not found"));
    }

    // 4. Update Transaction
    @Transactional
    public Transaction updateTransaction(
            Integer userId,
            Integer transactionId,
            Integer categoryId,
            BigDecimal amount,
            String type,
            LocalDate date) {

        validateTransaction(amount, type, date);

        Transaction transaction = getTransactionById(
                userId, transactionId);

        Category category = categoryService.getCategoryById(
                userId, categoryId);

        transaction.setCategory(category);
        transaction.setAmount(amount);
        transaction.setType(normalizeType(type));
        transaction.setDate(date);

        return transactionRepository.save(transaction);
    }

    // 5. Delete Transaction
    @Transactional
    public void deleteTransaction(
            Integer userId, Integer transactionId) {

        Transaction transaction = getTransactionById(
                userId, transactionId);

        transactionRepository.delete(transaction);
    }

    // 6. Calculate Financial Summary
    @Transactional(readOnly = true)
    public FinancialSummary getFinancialSummary(Integer userId) {

        List<Transaction> transactions =
                getUserTransactions(userId);

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpenses = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {

            if (INCOME.equals(transaction.getType())) {

                totalIncome = totalIncome.add(
                        transaction.getAmount());

            } else if (EXPENSE.equals(transaction.getType())) {

                totalExpenses = totalExpenses.add(
                        transaction.getAmount());
            }
        }

        BigDecimal balance =
                totalIncome.subtract(totalExpenses);

        return new FinancialSummary(
                totalIncome,
                totalExpenses,
                balance);
    }

    // 7. Validate Transaction Data
    private void validateTransaction(
            BigDecimal amount,
            String type,
            LocalDate date) {

        if (amount == null ||
                amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Amount must be greater than zero");
        }

        if (amount.scale() > 2 ||
                amount.precision() - amount.scale() > 13) {

            throw new IllegalArgumentException(
                    "Amount exceeds supported precision");
        }

        if (type == null ||
                (!INCOME.equals(normalizeType(type)) &&
                        !EXPENSE.equals(normalizeType(type)))) {

            throw new IllegalArgumentException(
                    "Type must be INCOME or EXPENSE");
        }

        if (date == null) {

            throw new IllegalArgumentException(
                    "Transaction date is required");
        }
    }

    // 8. Normalize Transaction Type
    private String normalizeType(String type) {

        return type.trim().toUpperCase(Locale.ROOT);
    }

    // 9. Financial Summary Result
    public record FinancialSummary(
            BigDecimal totalIncome,
            BigDecimal totalExpenses,
            BigDecimal balance) {
    }
}
