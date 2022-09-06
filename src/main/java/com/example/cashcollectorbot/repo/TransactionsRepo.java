package com.example.cashcollectorbot.repo;

import com.example.cashcollectorbot.model.Transaction;
import org.springframework.data.repository.CrudRepository;

public interface TransactionsRepo extends CrudRepository<Transaction, Long> {
}
