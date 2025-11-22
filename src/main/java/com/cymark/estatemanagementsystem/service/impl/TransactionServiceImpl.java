package com.cymark.estatemanagementsystem.service.impl;

import com.cymark.estatemanagementsystem.model.entity.Transaction;
import com.cymark.estatemanagementsystem.model.enums.TransactionStatus;
import com.cymark.estatemanagementsystem.repository.TransactionRepository;
import com.cymark.estatemanagementsystem.service.TransactionService;
import com.cymark.estatemanagementsystem.specification.TransactionSpecifications;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;

//    @Override
    public Page<Transaction> getTransactionsWithFilters1(
            String reference,
            TransactionStatus status,
            String productName,
            String userId,
            LocalDate fromDate,
            LocalDate toDate,
            int page,
            int size) {

        // Convert LocalDate to LocalDateTime for proper date range filtering
        LocalDateTime fromDateTime = null;
        LocalDateTime toDateTime = null;

        if (fromDate != null) {
            fromDateTime = fromDate.atStartOfDay(); // Start of the day
        }
        if (toDate != null) {
            toDateTime = toDate.atTime(LocalTime.MAX); // End of the day
        }

        // Create pageable with sorting by createdAt descending
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        return transactionRepository.findTransactionsWithFilters(
                reference,
                status,
                productName,
                userId,
                fromDateTime,
                toDateTime,
                pageable
        );
    }

    @Override
    public Optional<Transaction> getTransactionById(String transactionId) {
        return transactionRepository.findById(transactionId);
    }

//    public Page<Transaction> getTransactionsByUserId(String userId, int page, int size) {
//        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
//        return transactionRepository.findByUserId(userId, pageable);
//    }
//
//    public Page<Transaction> getTransactionsByStatus(TransactionStatus status, int page, int size) {
//        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
//        return transactionRepository.findByStatus(status, pageable);
//    }

    @Override
    public Transaction saveTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public Page<Transaction> getTransactionsWithFilters(
            String reference,
            TransactionStatus status,
            String productName,
            String userId,
            LocalDate fromDate,
            LocalDate toDate,
            int page,
            int size) {

        // Convert dates
        LocalDateTime fromDateTime = fromDate != null ? fromDate.atStartOfDay() : null;
        LocalDateTime toDateTime = toDate != null ? toDate.atTime(LocalTime.MAX) : null;

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Specification<Transaction> spec = TransactionSpecifications.withFilters(
                reference, status, productName, userId, fromDateTime, toDateTime);

        return transactionRepository.findAll(spec, pageable);
    }

    // Add this method to your TransactionRepository if you want to use it:
    // Page<Transaction> findByUserId(String userId, Pageable pageable);
    // Page<Transaction> findByStatus(TransactionStatus status, Pageable pageable);
}


