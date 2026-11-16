
package com.financetracker.service;

import com.financetracker.exception.ResourceNotFoundException;
import com.financetracker.model.Category;
import com.financetracker.model.Transaction;
import com.financetracker.model.User;
import com.financetracker.repository.TransactionRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserService userService;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void shouldCalculateFinancialSummaryCorrectly() {

        Integer userId = 1;

        when(userService.getUserById(userId))
                .thenReturn(new User());

        Transaction income = new Transaction();
        income.setType("INCOME");
        income.setAmount(new BigDecimal("3000.00"));

        Transaction expense1 = new Transaction();
        expense1.setType("EXPENSE");
        expense1.setAmount(new BigDecimal("450.00"));

        Transaction expense2 = new Transaction();
        expense2.setType("EXPENSE");
        expense2.setAmount(new BigDecimal("150.00"));

        when(transactionRepository
                .findByUserIdOrderByDateDesc(userId))
                .thenReturn(List.of(
                        income, expense1, expense2));

        TransactionService.FinancialSummary summary =
                transactionService.getFinancialSummary(userId);

        assertEquals(
                0,
                new BigDecimal("3000.00")
                        .compareTo(summary.totalIncome()));

        assertEquals(
                0,
                new BigDecimal("600.00")
                        .compareTo(summary.totalExpenses()));

        assertEquals(
                0,
                new BigDecimal("2400.00")
                        .compareTo(summary.balance()));
    }

    @Test
    void shouldRejectNegativeAmount() {

        assertThrows(
                IllegalArgumentException.class,
                () -> transactionService.createTransaction(
                        1,
                        1,
                        new BigDecimal("-100.00"),
                        "EXPENSE",
                        LocalDate.now()));

        verifyNoInteractions(transactionRepository);
    }

    @Test
    void shouldRejectInvalidTransactionType() {

        assertThrows(
                IllegalArgumentException.class,
                () -> transactionService.createTransaction(
                        1,
                        1,
                        new BigDecimal("100.00"),
                        "INVALID",
                        LocalDate.now()));

        verifyNoInteractions(transactionRepository);
    }

    @Test
    void shouldPreventUsingAnotherUsersCategory() {

        Integer userId = 1;
        Integer categoryId = 10;

        when(userService.getUserById(userId))
                .thenReturn(new User());

        when(categoryService.getCategoryById(
                userId, categoryId))
                .thenThrow(new ResourceNotFoundException(
                        "Category not found"));

        assertThrows(
                ResourceNotFoundException.class,
                () -> transactionService.createTransaction(
                        userId,
                        categoryId,
                        new BigDecimal("100.00"),
                        "EXPENSE",
                        LocalDate.now()));

        verify(transactionRepository, never())
                .save(any(Transaction.class));
    }

    @Test
    void shouldCreateValidTransaction() {

        Integer userId = 1;
        Integer categoryId = 2;

        User user = new User();
        Category category = new Category();

        when(userService.getUserById(userId))
                .thenReturn(user);

        when(categoryService.getCategoryById(
                userId, categoryId))
                .thenReturn(category);

        when(transactionRepository.save(
                any(Transaction.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        Transaction result =
                transactionService.createTransaction(
                        userId,
                        categoryId,
                        new BigDecimal("125.50"),
                        "expense",
                        LocalDate.of(2026, 11, 16));

        assertSame(user, result.getUser());
        assertSame(category, result.getCategory());
        assertEquals("EXPENSE", result.getType());

        assertEquals(
                0,
                new BigDecimal("125.50")
                        .compareTo(result.getAmount()));

        verify(transactionRepository)
                .save(any(Transaction.class));
    }
}
