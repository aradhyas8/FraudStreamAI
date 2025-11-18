package com.realtimefraud.fraud.controller;

import com.realtimefraud.fraud.model.TransactionEvent;
import com.realtimefraud.fraud.repository.TransactionEventRepository;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionEventRepository repository;

    public TransactionController(TransactionEventRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public TransactionEvent create(@RequestParam String cardLast4,
            @RequestParam BigDecimal amount,
            @RequestParam String merchant) {
        TransactionEvent event = new TransactionEvent();
        event.setCardLast4(cardLast4);
        event.setAmount(amount);
        event.setMerchant(merchant);
        return repository.save(event);
    }

    @GetMapping
    public List<TransactionEvent> findAll() {
        return repository.findAll();
    }
}
