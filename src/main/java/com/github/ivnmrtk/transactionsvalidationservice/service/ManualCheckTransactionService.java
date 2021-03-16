package com.github.ivnmrtk.transactionsvalidationservice.service;

import com.github.ivnmrtk.transactionsvalidationservice.dto.ExternalTransactionDto;

public interface ManualCheckTransactionService {
    void sendTransaction(ExternalTransactionDto externalTransactionDto);
}
