package com.github.ivnmrtk.transactionsvalidationservice.repository;

import com.github.ivnmrtk.transactionsvalidationservice.dao.Transaction;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface TransactionsRepository extends CrudRepository<Transaction, Integer> {
    Collection<Transaction> findAll();
}
