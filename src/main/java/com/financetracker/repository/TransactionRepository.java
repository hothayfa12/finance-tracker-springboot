
package com.financetracker.repository;

import com.financetracker.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    List<Transaction> findByUserIdOrderByDateDesc(Integer userId);

    Optional<Transaction> findByIdAndUserId(Integer id, Integer userId);

    List<Transaction> findByUserIdAndType(Integer userId, String type);

    boolean existsByCategoryId(Integer categoryId);
}
