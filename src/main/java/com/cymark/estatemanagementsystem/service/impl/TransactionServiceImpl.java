package com.cymark.estatemanagementsystem.service.impl;

import com.cymark.estatemanagementsystem.model.dto.EstateTransactionStatistics;
import com.cymark.estatemanagementsystem.model.dto.UserTransactionStatistics;
import com.cymark.estatemanagementsystem.model.entity.Transaction;
import com.cymark.estatemanagementsystem.model.enums.OrderStatus;
import com.cymark.estatemanagementsystem.model.enums.TransactionStatus;
import com.cymark.estatemanagementsystem.repository.OrderRepository;
import com.cymark.estatemanagementsystem.repository.TransactionRepository;
import com.cymark.estatemanagementsystem.service.TransactionService;
import com.cymark.estatemanagementsystem.specification.TransactionSpecifications;
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
import java.util.Optional;

import static com.cymark.estatemanagementsystem.model.enums.OrderStatus.FAILED;
import static com.cymark.estatemanagementsystem.model.enums.OrderStatus.PAYMENT_COMPLETED;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final OrderRepository orderRepository;

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

    public UserTransactionStatistics getUserTransactionStats(String email, String fromDate, String toDate){

        java.time.LocalDate fromLocalDate = null;
        java.time.LocalDate toLocalDate = null;

        if (fromDate != null && !fromDate.isEmpty()) {
            fromLocalDate = java.time.LocalDate.parse(fromDate);
        }
        if (toDate != null && !toDate.isEmpty()) {
            toLocalDate = java.time.LocalDate.parse(toDate);
        }
        UserTransactionStatistics userTransactionStatistics = new UserTransactionStatistics();
        userTransactionStatistics.setLastPaid(orderRepository.getLatestSubscriptionDateByStatus(email, OrderStatus.valueOf("COMPLETED")));
        userTransactionStatistics.setTotalAmountPaid(orderRepository.sumTransactionAmountByUserIdBetween(OrderStatus.valueOf("COMPLETED"), email, fromLocalDate, toLocalDate));
        return userTransactionStatistics;
    }

    public EstateTransactionStatistics getEstateTransactionStats(String fromDate, String toDate){
        EstateTransactionStatistics estateTransactionStatistics = new EstateTransactionStatistics();

        java.time.LocalDate fromLocalDate = null;
        java.time.LocalDate toLocalDate = null;

        if (fromDate != null && !fromDate.isEmpty()) {
            fromLocalDate = java.time.LocalDate.parse(fromDate);
        }
        if (toDate != null && !toDate.isEmpty()) {
            toLocalDate = java.time.LocalDate.parse(toDate);
        }

        estateTransactionStatistics.setTotalFailedTransactions(transactionRepository.countTransactionByStatusBetween(TransactionStatus.valueOf("FAILED"), fromLocalDate, toLocalDate));
        estateTransactionStatistics.setTotalSuccessfulTransactions(transactionRepository.countTransactionByStatusBetween(TransactionStatus.valueOf("COMPLETED"), fromLocalDate, toLocalDate));
        estateTransactionStatistics.setTotalSuccessfulAmountPaid(transactionRepository.sumTransactionAmountBetween(TransactionStatus.valueOf("COMPLETED"), fromLocalDate, toLocalDate));
        estateTransactionStatistics.setTotalFailedAmountPaid(transactionRepository.sumTransactionAmountBetween(TransactionStatus.valueOf("FAILED"), fromLocalDate, toLocalDate));

        return estateTransactionStatistics;
    }
}


