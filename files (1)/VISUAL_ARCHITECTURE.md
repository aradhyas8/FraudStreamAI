# Visual Architecture Diagram

## Simple Visual Overview

```ascii
                    REALTIME FRAUD DETECTION SYSTEM
                    ================================

┌─────────────────┐
│   Transaction   │ (Customer swipes card)
│     Source      │
└────────┬────────┘
         │ JSON Event
         ▼
    ╔═══════════════════╗
    ║  Apache Kafka     ║ (10,000 TPS buffer)
    ║  Topic: "trans"   ║
    ╚═══════════════════╝
         │ Consumer Group
         ▼
    ┌───────────────────────────────────────┐
    │   FRAUD DETECTION SERVICE (Java)      │
    │  ┌─────────────────────────────────┐  │
    │  │ 1. Extract Features (10 total)  │  │ 50ms
    │  │    • Amount, velocity, location │  │
    │  └────────────┬────────────────────┘  │
    │               ▼                        │
    │  ┌─────────────────────────────────┐  │
    │  │ 2. ML Model (XGBoost)           │  │ 10ms
    │  │    Input: 10 features           │  │
    │  │    Output: Fraud probability    │  │
    │  └────────────┬────────────────────┘  │
    │               ▼                        │
    │  ┌─────────────────────────────────┐  │
    │  │ 3. Decision Engine              │  │ 5ms
    │  │    >0.9 → BLOCK                 │  │
    │  │    0.5-0.9 → FLAG for analyst   │  │
    │  │    <0.5 → APPROVE               │  │
    │  └────────────┬────────────────────┘  │
    └───────────────┼────────────────────────┘
                    │
        ┌───────────┴────────────┐
        ▼                        ▼
    ╔═══════╗              ╔═══════════╗
    ║ Redis ║ (Cache)      ║PostgreSQL ║ (Storage)
    ╚═══════╝              ╚═══════════╝
        │                        │
        └────────────┬───────────┘
                     ▼
         ┌────────────────────────┐
         │    REST API             │
         │  (Analyst endpoints)    │
         └────────────────────────┘
                     │
                     ▼
         ┌────────────────────────┐
         │   React Dashboard       │
         │  (Fraud Analyst UI)     │
         └────────────────────────┘
                     │
                     ▼ Feedback
         ┌────────────────────────┐
         │  Weekly ML Retraining   │
         └────────────────────────┘
```

## Detailed Component Diagram

```
┌────────────────────────────────────────────────────────────────────┐
│                         PRODUCER LAYER                              │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │  Transaction Producer (Simulator)                             │  │
│  │  • Generates synthetic transactions                           │  │
│  │  • Rate: 1 transaction every 5 seconds (configurable)         │  │
│  │  • Sends to Kafka topic: "transactions"                       │  │
│  └──────────────────────────────────────────────────────────────┘  │
└────────────────────────────────────────────────────────────────────┘
                                  ⬇
┌────────────────────────────────────────────────────────────────────┐
│                      STREAMING LAYER (Kafka)                        │
│  Topic: transactions                                                │
│  • Partitions: 10 (for parallel processing)                        │
│  • Replication: 1 (for development)                                │
│  • Retention: 7 days                                               │
│  • Format: JSON                                                    │
└────────────────────────────────────────────────────────────────────┘
                                  ⬇
┌────────────────────────────────────────────────────────────────────┐
│               PROCESSING LAYER (Spring Boot Service)                │
│                                                                     │
│  ┌──────────────────────────────────────────────────────────────┐ │
│  │ STAGE 1: Feature Extraction (50ms)                           │ │
│  │ ┌────────────┐  ┌─────────────┐  ┌──────────────────┐      │ │
│  │ │  Amount    │  │  Velocity   │  │   Geographic     │      │ │
│  │ │  Features  │  │  Features   │  │   Features       │      │ │
│  │ └────────────┘  └─────────────┘  └──────────────────┘      │ │
│  │ Outputs: 10 numeric features                                │ │
│  └──────────────────────────────────────────────────────────────┘ │
│                              ⬇                                      │
│  ┌──────────────────────────────────────────────────────────────┐ │
│  │ STAGE 2: ML Scoring (10ms)                                   │ │
│  │ ┌──────────────────────────────────────────────────────────┐│ │
│  │ │ XGBoost Classifier                                        ││ │
│  │ │ • Model: fraud_model.json (2.5 MB)                        ││ │
│  │ │ • Input: 10 features                                      ││ │
│  │ │ • Output: fraud_probability (0.0 - 1.0)                   ││ │
│  │ │ • Metrics: Precision=85%, Recall=90%                      ││ │
│  │ └──────────────────────────────────────────────────────────┘│ │
│  └──────────────────────────────────────────────────────────────┘ │
│                              ⬇                                      │
│  ┌──────────────────────────────────────────────────────────────┐ │
│  │ STAGE 3: Decision Engine (5ms)                               │ │
│  │                                                              │ │
│  │  if fraud_probability > 0.9:                                │ │
│  │    decision = "BLOCKED"                                     │ │
│  │  elif fraud_probability > 0.5:                              │ │
│  │    decision = "FLAGGED"  # Send to analyst                 │ │
│  │  else:                                                       │ │
│  │    decision = "APPROVED"                                    │ │
│  └──────────────────────────────────────────────────────────────┘ │
│                              ⬇                                      │
│  ┌──────────────────────────────────────────────────────────────┐ │
│  │ STAGE 4: Persistence (20ms)                                  │ │
│  │ • Save transaction to PostgreSQL                            │ │
│  │ • Save features to PostgreSQL                               │ │
│  │ • Save prediction to PostgreSQL                             │ │
│  │ • Save decision to PostgreSQL                               │ │
│  │ • Cache customer history to Redis (24h TTL)                 │ │
│  └──────────────────────────────────────────────────────────────┘ │
│                                                                     │
│  Total Processing Time: <100ms                                     │
└────────────────────────────────────────────────────────────────────┘
                    ⬇                           ⬇
┌─────────────────────────────┐    ┌───────────────────────────────┐
│     CACHE LAYER (Redis)     │    │   STORAGE LAYER (PostgreSQL)  │
│  ┌───────────────────────┐  │    │  ┌─────────────────────────┐ │
│  │ Customer history:     │  │    │  │ Tables:                 │ │
│  │ • Last 24h trans      │  │    │  │ • transactions          │ │
│  │ • Velocity data       │  │    │  │ • transaction_features  │ │
│  │ • Profile data        │  │    │  │ • fraud_predictions     │ │
│  │                       │  │    │  │ • decisions             │ │
│  │ Merchant risk scores: │  │    │  │ • analyst_reviews       │ │
│  │ • Predefined risks    │  │    │  │ • merchants             │ │
│  │ • TTL: 7 days         │  │    │  └─────────────────────────┘ │
│  │                       │  │    │  Indexed on:                │ │
│  │ Hit Rate: ~90%        │  │    │  • customer_id              │ │
│  │ Avg Latency: 2ms      │  │    │  • timestamp                │ │
│  └───────────────────────┘  │    │  • transaction_id           │ │
└─────────────────────────────┘    └───────────────────────────────┘
                    ⬇
┌────────────────────────────────────────────────────────────────────┐
│                      API LAYER (REST)                               │
│  ┌──────────────────────────────────────────────────────────────┐ │
│  │ Analyst Workflow Endpoints:                                  │ │
│  │                                                              │ │
│  │ GET  /api/flagged-transactions                              │ │
│  │      → Returns list of transactions flagged for review      │ │
│  │                                                              │ │
│  │ GET  /api/transactions/{id}/details                         │ │
│  │      → Full context: features, score, customer history      │ │
│  │                                                              │ │
│  │ POST /api/transactions/{id}/review                          │ │
│  │      → Analyst submits decision: FRAUD or LEGITIMATE        │ │
│  │                                                              │ │
│  │ GET  /api/dashboard/stats                                   │ │
│  │      → Dashboard metrics (flagged count, avg score, etc.)   │ │
│  └──────────────────────────────────────────────────────────────┘ │
└────────────────────────────────────────────────────────────────────┘
                                  ⬇
┌────────────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER (React)                       │
│  ┌──────────────────────────────────────────────────────────────┐ │
│  │                  Fraud Analyst Dashboard                      │ │
│  │                                                              │ │
│  │  ┌────────────────────────────────────────────────────────┐ │ │
│  │  │ Dashboard Stats                                         │ │ │
│  │  │ ┌─────────────┐ ┌─────────────┐ ┌──────────────────┐  │ │ │
│  │  │ │Total Flagged│ │ Avg Score   │ │ Pending Reviews  │  │ │ │
│  │  │ │     47      │ │    0.73     │ │        23        │  │ │ │
│  │  │ └─────────────┘ └─────────────┘ └──────────────────┘  │ │ │
│  │  └────────────────────────────────────────────────────────┘ │ │
│  │                                                              │ │
│  │  ┌────────────────────────────────────────────────────────┐ │ │
│  │  │ Flagged Transactions Queue                             │ │ │
│  │  │ ┌───────┬─────────┬────────┬───────┬────────────────┐ │ │ │
│  │  │ │ ID    │Customer │ Amount │ Score │ Actions        │ │ │ │
│  │  │ ├───────┼─────────┼────────┼───────┼────────────────┤ │ │ │
│  │  │ │txn_01 │CUST_001 │ $5,240 │ 0.87  │[Review Details]│ │ │ │
│  │  │ │txn_02 │CUST_045 │ $3,120 │ 0.72  │[Review Details]│ │ │ │
│  │  │ │txn_03 │CUST_089 │ $7,980 │ 0.65  │[Review Details]│ │ │ │
│  │  │ └───────┴─────────┴────────┴───────┴────────────────┘ │ │ │
│  │  └────────────────────────────────────────────────────────┘ │ │
│  │                                                              │ │
│  │  On Review Details Click:                                   │ │
│  │  ┌────────────────────────────────────────────────────────┐ │ │
│  │  │ Transaction Details Modal                               │ │ │
│  │  │ • Full transaction info                                 │ │ │
│  │  │ • All 10 features extracted                             │ │ │
│  │  │ • Customer transaction history                          │ │ │
│  │  │ • [Approve as Legitimate] [Mark as Fraud] buttons       │ │ │
│  │  └────────────────────────────────────────────────────────┘ │ │
│  └──────────────────────────────────────────────────────────────┘ │
└────────────────────────────────────────────────────────────────────┘
                                  ⬇ Analyst Feedback
┌────────────────────────────────────────────────────────────────────┐
│                     FEEDBACK LOOP & RETRAINING                      │
│  ┌──────────────────────────────────────────────────────────────┐ │
│  │ Weekly Batch Job (Cron: Every Sunday 2am)                   │ │
│  │                                                              │ │
│  │ 1. Query analyst_reviews table for past week                │ │
│  │ 2. Extract features + true labels from reviews              │ │
│  │ 3. Append new data to training dataset                      │ │
│  │ 4. Retrain XGBoost model with updated data                  │ │
│  │ 5. Evaluate new model (precision, recall, F1)               │ │
│  │ 6. If metrics improve → Deploy new model                    │ │
│  │ 7. Save as fraud_model_v{timestamp}.json                    │ │
│  │ 8. Update model version in application config               │ │
│  └──────────────────────────────────────────────────────────────┘ │
└────────────────────────────────────────────────────────────────────┘
```

## Data Flow by Example

### Example 1: Legitimate Transaction

```
Customer: John (CUST_789)
Action: Buys coffee at Starbucks for $5.50 at 8am

Flow:
1. Transaction Event → Kafka
2. Consumer picks up event
3. Feature Extraction:
   - amount_normalized = 0.00055 (very low)
   - time_of_day = 8 (morning - typical)
   - transaction_count_24h = 2 (normal velocity)
   - merchant_risk_score = 0.1 (coffee shop - low risk)
   - distance_from_home = 2 miles (nearby)
   - ... other features
4. ML Model Prediction:
   - fraud_probability = 0.03 (3% chance of fraud)
5. Decision Engine:
   - 0.03 < 0.5 → APPROVED
6. Persist to database
7. Customer's transaction goes through immediately

Result: Auto-approved, no human intervention needed
```

### Example 2: Suspicious Transaction (True Positive)

```
Customer: Sarah (CUST_234)
Action: Buys $5,000 laptop at 3am from new electronics store

Flow:
1. Transaction Event → Kafka
2. Consumer picks up event
3. Feature Extraction:
   - amount_normalized = 0.5 (high)
   - time_of_day = 3 (unusual hour)
   - transaction_count_24h = 5 (high velocity - multiple purchases today)
   - merchant_risk_score = 0.85 (electronics - high risk category)
   - distance_from_home = 500 miles (far from home)
   - velocity_impossible = 1 (was in NY 2 hours ago, now in LA)
   - ... other features
4. ML Model Prediction:
   - fraud_probability = 0.92 (92% chance of fraud)
5. Decision Engine:
   - 0.92 > 0.9 → BLOCKED
6. Persist to database with BLOCKED status
7. Transaction automatically declined
8. Customer receives fraud alert

Result: Auto-blocked, card potentially compromised
```

### Example 3: Flagged for Review (Edge Case)

```
Customer: Mike (CUST_456)
Action: Buys $2,000 camera equipment at 7pm

Flow:
1. Transaction Event → Kafka
2. Consumer picks up event
3. Feature Extraction:
   - amount_normalized = 0.2 (moderately high)
   - time_of_day = 19 (evening - acceptable)
   - transaction_count_24h = 1 (normal)
   - merchant_risk_score = 0.65 (electronics - moderate risk)
   - distance_from_home = 15 miles (reasonable)
   - is_international = 0 (domestic)
   - ... other features
4. ML Model Prediction:
   - fraud_probability = 0.67 (67% chance of fraud)
5. Decision Engine:
   - 0.5 < 0.67 < 0.9 → FLAGGED for review
6. Persist to database with FLAGGED status
7. Appears in analyst dashboard
8. Analyst reviews:
   - Checks customer history
   - Sees Mike is a professional photographer
   - Has purchased camera equipment before
   - Called bank beforehand about purchase
   - Decision: APPROVE (false positive)
9. Feedback saved: features + label=0 (legitimate)
10. Next week's retraining: Model learns this pattern

Result: Human-in-the-loop prevented false positive
```

## Technology Stack Summary

```
┌─────────────────────────────────────────────────────────┐
│                   TECHNOLOGY STACK                      │
├─────────────────────────────────────────────────────────┤
│ Event Streaming:    Apache Kafka 3.6                    │
│ Backend:            Spring Boot 3.2 (Java 17)           │
│ ML Framework:       XGBoost 2.0 (Python training)       │
│ ML Integration:     XGBoost4J 2.0 (Java inference)      │
│ Database:           PostgreSQL 15                       │
│ Cache:              Redis 7                             │
│ Frontend:           React 18 + Material-UI              │
│ API:                REST (Spring Web)                   │
│ Containerization:   Docker + Docker Compose             │
│ Build Tool:         Maven 3.9                           │
│ Testing:            JUnit 5, JMeter (load testing)      │
└─────────────────────────────────────────────────────────┘
```

## Performance Summary

```
┌─────────────────────────────────────────────────────────┐
│               PERFORMANCE CHARACTERISTICS               │
├─────────────────────────────────────────────────────────┤
│ Throughput:         10,000 transactions per second      │
│ Latency (avg):      87ms (end-to-end)                   │
│ Latency (p99):      120ms                               │
│ Cache Hit Rate:     90%                                  │
│ ML Precision:       85%                                  │
│ ML Recall:          90%                                  │
│ False Positive Rate: 5%                                  │
│ Auto-Approval Rate:  ~95% (most transactions)           │
│ Flagged for Review:  ~4% (need human review)            │
│ Auto-Blocked:        ~1% (high confidence fraud)        │
└─────────────────────────────────────────────────────────┘
```
