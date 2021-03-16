package com.github.ivnmrtk.transactionsvalidationservice.repository;

import com.github.ivnmrtk.transactionsvalidationservice.dao.Transaction;
import org.springframework.data.repository.CrudRepository;

public interface TransactionsRepository extends CrudRepository<Transaction, Integer> {
}
