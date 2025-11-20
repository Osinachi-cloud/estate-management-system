package com.cymark.estatemanagementsystem.service.impl;

import com.cymark.estatemanagementsystem.model.entity.Transaction;
import com.cymark.estatemanagementsystem.repository.TransactionRepository;
import com.cymark.estatemanagementsystem.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    @Override
    public Transaction saveTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }
}
