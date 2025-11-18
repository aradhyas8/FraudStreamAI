package com.realtimefraud.fraud.service;

import com.realtimefraud.fraud.entity.TransactionEntity;
import com.realtimefraud.fraud.repository.TransactionRepository;
import com.realtimefraud.fraud.model.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
@Slf4j
@Service
public class TransactionConsumerService {

    private final TransactionRepository transactionRepository;

public TransactionConsumerService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public void handle(Transaction tx) {
        log.info("Received tx: id={}, customer={}, merchant={}, amount={}",
                tx.getTransactionId(),
                tx.getCustomerId(),
                tx.getMerchantId(),
                tx.getAmount());

        TransactionEntity entity = mapToEntity(tx);
        transactionRepository.save(entity);
    }

    private TransactionEntity mapToEntity(Transaction tx) {
        TransactionEntity entity = new TransactionEntity();
        entity.setTransactionId(tx.getTransactionId());
        entity.setCustomerId(tx.getCustomerId());
        entity.setMerchantId(tx.getMerchantId());
        entity.setAmount(new BigDecimal(tx.getAmount()));
        entity.setTimestamp(tx.getTimestamp());
        entity.setLocation(tx.getLocation());
        entity.setMerchantCategory(tx.getMerchantCategory());
        entity.setIsInternational(tx.getIsInternational());
        return entity;
    }
}
