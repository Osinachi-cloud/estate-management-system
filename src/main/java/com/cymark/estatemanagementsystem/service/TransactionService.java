package com.cymark.estatemanagementsystem.service;

import com.cymark.estatemanagementsystem.model.dto.EstateTransactionStatistics;
import com.cymark.estatemanagementsystem.model.dto.UserTransactionStatistics;
import com.cymark.estatemanagementsystem.model.entity.Transaction;
import com.cymark.estatemanagementsystem.model.enums.TransactionStatus;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.Optional;

public interface TransactionService {
    Page<Transaction> getTransactionsWithFilters(
            String reference,
            TransactionStatus status,
            String productName,
            String userId,
            String estateId,
            LocalDate fromDate,
            LocalDate toDate,
            int page,
            int size);

    Optional<Transaction> getTransactionById(String transactionId);

    Transaction saveTransaction(Transaction transaction);

    UserTransactionStatistics getUserTransactionStats(String email, String fromDate, String toDate);

    EstateTransactionStatistics getEstateTransactionStats(String estateId, String fromDate, String toDate);
}
