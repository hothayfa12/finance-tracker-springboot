
package com.financetracker.dto;

import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionRequest {

    @NotNull(message = "Category is required")
    private Integer categoryId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01",
            message = "Amount must be greater than zero")
    @Digits(integer = 13, fraction = 2,
            message = "Amount must have at most 2 decimal places")
    private BigDecimal amount;

    @NotBlank(message = "Transaction type is required")
    @Pattern(regexp = "INCOME|EXPENSE",
            message = "Select a valid transaction type")
    private String type;

    @NotNull(message = "Date is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;

    public TransactionRequest() {
        this.date = LocalDate.now();
        this.type = "EXPENSE";
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
