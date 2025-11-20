package com.cymark.estatemanagementsystem.service;

import com.cymark.estatemanagementsystem.model.entity.Transaction;

public interface TransactionService {
    Transaction saveTransaction(Transaction transaction);
}
