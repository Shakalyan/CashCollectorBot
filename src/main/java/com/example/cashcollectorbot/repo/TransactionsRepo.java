package com.example.cashcollectorbot.repo;

import com.example.cashcollectorbot.model.Transaction;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;

public interface TransactionsRepo extends CrudRepository<Transaction, Long> {

    public ArrayList<Transaction> findAllByUserId(Long userId);

}
