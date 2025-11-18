package com.realtimefraud.fraud.kafka;

import com.realtimefraud.fraud.model.Transaction;
import com.realtimefraud.fraud.service.TransactionConsumerService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionConsumer {

    private final TransactionConsumerService consumerService;

    public TransactionConsumer(TransactionConsumerService consumerService) {
        this.consumerService = consumerService;
    }

    @KafkaListener(topics = "transactions", groupId = "fraud-detection-group", containerFactory = "transactionKafkaListenerContainerFactory")
    public void onMessage(Transaction transaction) {
        consumerService.handle(transaction);
    }
}
