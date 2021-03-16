package com.github.ivnmrtk.transactionsvalidationservice.rest;

import com.github.ivnmrtk.transactionsvalidationservice.dto.ExternalTransactionDto;
import com.github.ivnmrtk.transactionsvalidationservice.service.ManualCheckTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class CheckTransactionController {

    private final ManualCheckTransactionService manualCheckTransactionService;

    @PostMapping("/send-and-check")
    public void sendTxToKafkaAndCheck(@RequestBody ExternalTransactionDto externalTransactionDto) {
        manualCheckTransactionService.sendTransaction(externalTransactionDto);
    }
}
