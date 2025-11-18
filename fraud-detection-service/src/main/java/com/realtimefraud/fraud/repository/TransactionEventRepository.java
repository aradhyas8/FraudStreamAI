package com.realtimefraud.fraud.repository;

import com.realtimefraud.fraud.model.TransactionEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionEventRepository extends JpaRepository<TransactionEvent, Long> {
}
