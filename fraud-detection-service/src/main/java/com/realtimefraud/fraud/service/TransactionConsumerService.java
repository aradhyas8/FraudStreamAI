package com.realtimefraud.fraud.service;

import com.realtimefraud.fraud.model.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TransactionConsumerService {

    public void handle(Transaction tx) {
        log.info("Received tx: id={}, customer={}, merchant={}, amount={}",
                tx.getTransactionId(),
                tx.getCustomerId(),
                tx.getMerchantId(),
                tx.getAmount());
    }
}
