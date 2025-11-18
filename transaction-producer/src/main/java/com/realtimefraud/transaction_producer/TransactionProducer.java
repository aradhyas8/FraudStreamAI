package com.realtimefraud.producer;

import com.realtimefraud.producer.model.Transaction;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class TransactionProducer {

    private final KafkaTemplate<String, Transaction> kafkaTemplate;
    private final TransactionGenerator generator;

    public TransactionProducer(KafkaTemplate<String, Transaction> kafkaTemplate, TransactionGenerator generator) {
        this.kafkaTemplate = kafkaTemplate;
        this.generator = generator;
    }

    @Scheduled(fixedRate = 5000) // every 5s
    public void sendRandomTransaction() {
        Transaction tx = generator.generate();
        kafkaTemplate.send("transactions", tx.getTransactionId(), tx);
        System.out.println("Sent transaction: " + tx.getTransactionId());
    }
}
