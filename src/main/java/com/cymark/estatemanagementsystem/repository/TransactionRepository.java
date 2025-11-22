package com.cymark.estatemanagementsystem.repository;

import com.cymark.estatemanagementsystem.model.entity.Transaction;
import com.cymark.estatemanagementsystem.model.enums.TransactionStatus;
import com.cymark.estatemanagementsystem.specification.TransactionSpecifications;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String>, JpaSpecificationExecutor<Transaction> {

    // Basic query methods
    List<Transaction> findByStatus(TransactionStatus status);
    List<Transaction> findByUserId(String userId);
    List<Transaction> findByProductNameContainingIgnoreCase(String productName);
    List<Transaction> findByReferenceContainingIgnoreCase(String reference);

    // Date range queries
    List<Transaction> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Transaction> findByCreatedAtAfter(LocalDateTime startDate);
    List<Transaction> findByCreatedAtBefore(LocalDateTime endDate);

    // Complex queries with pagination
    @Query("SELECT t FROM Transaction t WHERE " +
            "(:reference IS NULL OR LOWER(t.reference) LIKE LOWER(CONCAT('%', :reference, '%'))) AND " +
            "(:status IS NULL OR t.status = :status) AND " +
            "(:productName IS NULL OR LOWER(t.productName) LIKE LOWER(CONCAT('%', :productName, '%'))) AND " +
            "(:userId IS NULL OR t.userId = :userId) AND " +
            "(:fromDate IS NULL OR t.createdAt >= :fromDate) AND " +
            "(:toDate IS NULL OR t.createdAt <= :toDate)")
    Page<Transaction> findTransactionsWithFilters(
            @Param("reference") String reference,
            @Param("status") TransactionStatus status,
            @Param("productName") String productName,
            @Param("userId") String userId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable);

    default BigDecimal sumTransactionAmountBetween(TransactionStatus status, LocalDate fromDate, LocalDate toDate) {
        Specification<Transaction> spec = Specification.where(TransactionSpecifications.withStatus(status))
                .and(TransactionSpecifications.withCreatedAtNotNull())
                .and(TransactionSpecifications.withCreatedAtBetween(fromDate, toDate));

        List<Transaction> transactions = findAll(spec);
        return transactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    default Long countTransactionByStatusBetween(TransactionStatus status, LocalDate fromDate, LocalDate toDate) {
        Specification<Transaction> spec = Specification.where(TransactionSpecifications.withStatus(status))
                .and(TransactionSpecifications.withCreatedAtNotNull())
                .and(TransactionSpecifications.withCreatedAtBetween(fromDate, toDate));

        return count(spec);
    }
}
