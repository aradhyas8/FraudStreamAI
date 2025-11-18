package com.realtimefraud.fraud.repository;

import com.realtimefraud.fraud.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
    // later: findByCustomerId, etc.
}
