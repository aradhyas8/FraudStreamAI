package com.realtimefraud.producer;

import com.realtimefraud.producer.model.Transaction;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Component
public class TransactionGenerator {

    private final Random random = new Random();

    public Transaction generate() {
        Transaction tx = new Transaction();
        tx.setTransactionId(UUID.randomUUID().toString());
        tx.setCustomerId("CUST_" + random.nextInt(10_000));
        tx.setMerchantId("MERCH_" + random.nextInt(500));
        tx.setAmount(50 + random.nextDouble() * 950); // $50 - $1000
        tx.setTimestamp(LocalDateTime.now());
        tx.setLocation("Toronto");
        tx.setMerchantCategory("retail");
        tx.setIsInternational(random.nextDouble() < 0.1); // 10% intl
        return tx;
    }
}
