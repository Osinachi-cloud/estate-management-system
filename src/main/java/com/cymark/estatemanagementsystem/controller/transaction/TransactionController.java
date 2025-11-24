package com.cymark.estatemanagementsystem.controller.transaction;

import com.cymark.estatemanagementsystem.model.dto.EstateTransactionStatistics;
import com.cymark.estatemanagementsystem.model.dto.UserTransactionStatistics;
import com.cymark.estatemanagementsystem.model.entity.Transaction;
import com.cymark.estatemanagementsystem.model.enums.TransactionStatus;
import com.cymark.estatemanagementsystem.model.request.TransactionFilterRequest;
import com.cymark.estatemanagementsystem.model.response.BaseResponse;
import com.cymark.estatemanagementsystem.model.response.PaginatedResponse;
import com.cymark.estatemanagementsystem.service.TransactionService;
import com.cymark.estatemanagementsystem.util.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.cymark.estatemanagementsystem.util.Constants.BASE_URL;

@RestController
@RequestMapping(BASE_URL)
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/get-transactions")
    public ResponseEntity<BaseResponse<PaginatedResponse<List<Transaction>>>> getTransactions(
            @RequestParam(required = false) String reference,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String estateId,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            // Convert status string to enum if provided
            TransactionStatus statusEnum = null;
            if (status != null && !status.isEmpty()) {
                try {
                    statusEnum = TransactionStatus.valueOf(status.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest()
                            .body(BaseResponse.error(HttpStatus.BAD_REQUEST, "Invalid status value: " + status));
                }
            }

            // Convert date strings to LocalDate
            java.time.LocalDate fromLocalDate = null;
            java.time.LocalDate toLocalDate = null;

            if (fromDate != null && !fromDate.isEmpty()) {
                fromLocalDate = java.time.LocalDate.parse(fromDate);
            }
            if (toDate != null && !toDate.isEmpty()) {
                toLocalDate = java.time.LocalDate.parse(toDate);
            }

            Page<Transaction> transactions = transactionService.getTransactionsWithFilters(
                    reference, statusEnum, productName, userId, estateId, fromLocalDate, toLocalDate, page, size);

            PaginatedResponse<List<Transaction>> pagination = new PaginatedResponse<List<Transaction>>(
                    page,
                    size,
                    (int)transactions.getTotalElements(),
//                    transactions.getTotalPages()
                    transactions.getContent()
            );

            return ResponseEntity.ok(BaseResponse.success(pagination, "Successfully retrieved transactions"));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(BaseResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching transactions: " + e.getMessage()));
        }
    }

    // Alternative approach using a single DTO
    @GetMapping("/get-transactions-v2")
    public ResponseEntity<BaseResponse<?>> getTransactionsV2(TransactionFilterRequest filterRequest) {
        try {
            Page<Transaction> transactions = transactionService.getTransactionsWithFilters(
                    filterRequest.getReference(),
                    filterRequest.getStatus(),
                    filterRequest.getProductName(),
                    filterRequest.getUserId(),
                    filterRequest.getEstateId(),
                    filterRequest.getFromDate(),
                    filterRequest.getToDate(),
                    filterRequest.getPage(),
                    filterRequest.getSize()
            );

            PaginatedResponse pagination = new PaginatedResponse(
                    filterRequest.getPage(),
                    filterRequest.getSize(),
                    (int)transactions.getTotalElements(),
//                    transactions.getTotalPages()
                    transactions.getContent()
            );

            return ResponseEntity.ok(BaseResponse.success(pagination, "Success fetching transactions"));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(BaseResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching transactions: " + e.getMessage()));
        }
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<BaseResponse<Transaction>> getTransactionById(@PathVariable String transactionId) {
        try {
            Optional<Transaction> transaction = transactionService.getTransactionById(transactionId);

            if (transaction.isPresent()) {
                return ResponseEntity.ok(BaseResponse.success(transaction.get()));
            } else {
                return ResponseEntity.status(404)
                        .body(BaseResponse.error(HttpStatus.NOT_FOUND, "Transaction not found with ID: " + transactionId));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(BaseResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching transaction: " + e.getMessage()));
        }
    }

    @GetMapping("/get-user-transaction-stats")
    public ResponseEntity<BaseResponse<UserTransactionStatistics>> getUserTransactionStats(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate
    ){

        String effectiveFromDate = fromDate != null ? fromDate : DateUtils.getDefaultFromDateAsString();
        String effectiveToDate = toDate != null ? toDate : DateUtils.getDefaultToDateAsString();

        UserTransactionStatistics userTransactionStatistics = transactionService.getUserTransactionStats(email,effectiveFromDate, effectiveToDate);
        return ResponseEntity.ok(BaseResponse.success(userTransactionStatistics, "Users retrieved successfully"));
    }

    @GetMapping("/get-estate-transaction-stats")
    public ResponseEntity<BaseResponse<EstateTransactionStatistics>> getEstateTransactionStats(
            @RequestParam(required = false) String estateId,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate
    ){
        String effectiveFromDate = fromDate != null ? fromDate : DateUtils.getDefaultFromDateAsString();
        String effectiveToDate = toDate != null ? toDate : DateUtils.getDefaultToDateAsString();

        EstateTransactionStatistics estateTransactionStatistics = transactionService.getEstateTransactionStats(estateId,
                effectiveFromDate, effectiveToDate);
        return ResponseEntity.ok(BaseResponse.success(estateTransactionStatistics, "Estate transaction stats retrieved successfully"));
    }
}