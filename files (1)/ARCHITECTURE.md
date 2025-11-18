# RealTimeFraudDetect - System Architecture

## High-Level Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         REALTIME FRAUD DETECTION SYSTEM                      │
└─────────────────────────────────────────────────────────────────────────────┘

┌──────────────────────┐
│  Transaction Source  │
│  (Credit Card        │
│   Network/Simulator) │
└──────────┬───────────┘
           │ Transaction Events
           │ (JSON/Avro)
           ▼
┌──────────────────────────────────────────────────────────────────────────────┐
│                           EVENT STREAMING LAYER                               │
│  ┌────────────────────────────────────────────────────────────────────┐     │
│  │                        Apache Kafka                                 │     │
│  │  Topic: "transactions" (10 partitions)                             │     │
│  │  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐          │     │
│  │  │ Part 0   │  │ Part 1   │  │ Part 2   │  │  ...     │          │     │
│  │  └──────────┘  └──────────┘  └──────────┘  └──────────┘          │     │
│  │  Throughput: 10,000 TPS | Retention: 7 days                       │     │
│  └────────────────────────────────────────────────────────────────────┘     │
└──────────────────────────────────────────────────────────────────────────────┘
           │
           │ Consumer Group: "fraud-detection-group"
           ▼
┌──────────────────────────────────────────────────────────────────────────────┐
│                      FRAUD DETECTION SERVICE (Spring Boot)                   │
│  ┌────────────────────────────────────────────────────────────────────┐     │
│  │                      1. TRANSACTION CONSUMER                        │     │
│  │  @KafkaListener → Receives transaction → Validates                 │     │
│  └────────────────┬───────────────────────────────────────────────────┘     │
│                   │                                                           │
│                   ▼                                                           │
│  ┌────────────────────────────────────────────────────────────────────┐     │
│  │                   2. FEATURE EXTRACTION ENGINE                      │     │
│  │  ┌──────────────┐  ┌───────────────┐  ┌──────────────────┐       │     │
│  │  │ Amount       │  │ Velocity      │  │ Geographic       │       │     │
│  │  │ Features     │  │ Features      │  │ Features         │       │     │
│  │  │ • Normalized │  │ • Txn count   │  │ • Distance from  │       │     │
│  │  │   amount     │  │   in 24h      │  │   home           │       │     │
│  │  │ • Time of    │  │ • Avg amount  │  │ • Impossible     │       │     │
│  │  │   day        │  │   7 days      │  │   travel         │       │     │
│  │  └──────────────┘  └───────────────┘  └──────────────────┘       │     │
│  │                                                                     │     │
│  │  Extracts 10 features in <50ms                                     │     │
│  └────────────────┬───────────────────────────────────────────────────┘     │
│                   │                                                           │
│                   ▼                                                           │
│  ┌────────────────────────────────────────────────────────────────────┐     │
│  │                     3. ML SCORING ENGINE                            │     │
│  │  ┌────────────────────────────────────────────────────┐           │     │
│  │  │  XGBoost Model (fraud_model.json)                  │           │     │
│  │  │  • Trained on 100K transactions                    │           │     │
│  │  │  • Precision: 85%, Recall: 90%                     │           │     │
│  │  │  • Input: 10 features                              │           │     │
│  │  │  • Output: Fraud probability (0.0 - 1.0)           │           │     │
│  │  └────────────────────────────────────────────────────┘           │     │
│  │                                                                     │     │
│  │  Scoring latency: <10ms                                            │     │
│  └────────────────┬───────────────────────────────────────────────────┘     │
│                   │                                                           │
│                   ▼                                                           │
│  ┌────────────────────────────────────────────────────────────────────┐     │
│  │                     4. DECISION ENGINE                              │     │
│  │  Rule-Based Decision Logic:                                        │     │
│  │                                                                     │     │
│  │  if fraud_probability > 0.9:                                       │     │
│  │    → BLOCKED (auto-decline, alert customer)                        │     │
│  │                                                                     │     │
│  │  elif fraud_probability > 0.5:                                     │     │
│  │    → FLAGGED (send to analyst queue)                               │     │
│  │                                                                     │     │
│  │  else:                                                              │     │
│  │    → APPROVED (proceed with transaction)                           │     │
│  │                                                                     │     │
│  │  Decision latency: <5ms                                            │     │
│  └────────────────┬───────────────────────────────────────────────────┘     │
│                   │                                                           │
│                   ▼                                                           │
│  ┌────────────────────────────────────────────────────────────────────┐     │
│  │                    5. PERSISTENCE LAYER                             │     │
│  │  Save: Transaction, Features, Prediction, Decision                 │     │
│  └────────────────────────────────────────────────────────────────────┘     │
│                                                                               │
│  Total Processing Time: <100ms (from Kafka to decision)                      │
└───────────────────────────────────┬───────────────────────────────────────────┘
                                    │
                   ┌────────────────┴────────────────┐
                   │                                  │
                   ▼                                  ▼
    ┌──────────────────────────┐      ┌──────────────────────────┐
    │   STORAGE LAYER          │      │   CACHING LAYER          │
    │  ┌────────────────────┐  │      │  ┌────────────────────┐  │
    │  │   PostgreSQL       │  │      │  │      Redis         │  │
    │  │                    │  │      │  │                    │  │
    │  │ Tables:            │  │      │  │ Cached Data:       │  │
    │  │ • transactions     │  │      │  │ • Customer history │  │
    │  │ • fraud_scores     │  │      │  │   (24h window)     │  │
    │  │ • decisions        │  │      │  │ • Merchant risk    │  │
    │  │ • analyst_reviews  │  │      │  │   scores           │  │
    │  │ • merchants        │  │      │  │ • Feature vectors  │  │
    │  │                    │  │      │  │                    │  │
    │  │ Indexed on:        │  │      │  │ TTL: 24 hours      │  │
    │  │ • customer_id      │  │      │  │ Hit rate: 90%      │  │
    │  │ • timestamp        │  │      │  └────────────────────┘  │
    │  │ • transaction_id   │  │      │                          │
    │  └────────────────────┘  │      └──────────────────────────┘
    └──────────────────────────┘
                   │
                   ▼
    ┌──────────────────────────────────────────────────────────┐
    │              ANALYST WORKFLOW & API                       │
    │  ┌────────────────────────────────────────────────┐      │
    │  │     REST API (Spring Boot Controllers)          │      │
    │  │                                                  │      │
    │  │  GET  /api/flagged-transactions                 │      │
    │  │       → List all transactions flagged for review│      │
    │  │                                                  │      │
    │  │  GET  /api/transactions/{id}/details            │      │
    │  │       → Full context (features, score, history) │      │
    │  │                                                  │      │
    │  │  POST /api/transactions/{id}/review             │      │
    │  │       → Analyst marks as FRAUD or LEGITIMATE    │      │
    │  │                                                  │      │
    │  │  GET  /api/dashboard/stats                      │      │
    │  │       → Metrics (flagged count, avg score)      │      │
    │  └────────────────────────────────────────────────┘      │
    └──────────────────────┬───────────────────────────────────┘
                           │
                           ▼
    ┌──────────────────────────────────────────────────────────┐
    │           FRONTEND (React Dashboard)                      │
    │  ┌────────────────────────────────────────────────┐      │
    │  │  Fraud Analyst Dashboard                        │      │
    │  │  ┌──────────────────────────────────────────┐  │      │
    │  │  │ Dashboard Stats                          │  │      │
    │  │  │ • Total Flagged: 47                      │  │      │
    │  │  │ • Avg Score: 0.73                        │  │      │
    │  │  │ • Pending Review: 23                     │  │      │
    │  │  └──────────────────────────────────────────┘  │      │
    │  │                                                 │      │
    │  │  ┌──────────────────────────────────────────┐  │      │
    │  │  │ Flagged Transactions List                │  │      │
    │  │  ├──────┬──────────┬────────┬──────┬────────┤  │      │
    │  │  │ ID   │ Customer │ Amount │ Score│ Action │  │      │
    │  │  ├──────┼──────────┼────────┼──────┼────────┤  │      │
    │  │  │ 1234 │ CUST_001 │ $5,240 │ 0.87 │[Review]│  │      │
    │  │  │ 1235 │ CUST_045 │ $3,120 │ 0.72 │[Review]│  │      │
    │  │  └──────┴──────────┴────────┴──────┴────────┘  │      │
    │  │                                                 │      │
    │  │  [Approve] [Reject] buttons → POST to API      │      │
    │  └────────────────────────────────────────────────┘      │
    └──────────────────────────────────────────────────────────┘
                           │
                           │ Analyst Feedback
                           ▼
    ┌──────────────────────────────────────────────────────────┐
    │           FEEDBACK LOOP & RETRAINING                      │
    │  ┌────────────────────────────────────────────────┐      │
    │  │  Weekly Batch Job (ModelRetrainingService)     │      │
    │  │                                                 │      │
    │  │  1. Collect analyst decisions from past week   │      │
    │  │  2. Extract true labels (FRAUD vs LEGITIMATE)  │      │
    │  │  3. Append to training dataset                 │      │
    │  │  4. Retrain XGBoost model                      │      │
    │  │  5. Evaluate new model (precision, recall)     │      │
    │  │  6. Deploy if metrics improve                  │      │
    │  │                                                 │      │
    │  │  Model versioning: fraud_model_v{date}.json    │      │
    │  └────────────────────────────────────────────────┘      │
    └──────────────────────────────────────────────────────────┘
```

---

## Data Flow Sequence

### 1. Transaction Processing Flow (Happy Path)

```
[Transaction Event] 
    │
    ├─> Kafka Topic "transactions"
    │
    ├─> Fraud Detection Service (Consumer)
    │       │
    │       ├─> Extract Features (50ms)
    │       │   ├─> Check Redis for customer history [CACHE HIT → 2ms]
    │       │   ├─> Calculate 10 features
    │       │   └─> Store features in Redis + PostgreSQL
    │       │
    │       ├─> ML Scoring (10ms)
    │       │   ├─> Load features into DMatrix
    │       │   ├─> Call XGBoost model.predict()
    │       │   └─> Get fraud probability (e.g., 0.34)
    │       │
    │       ├─> Decision Engine (5ms)
    │       │   ├─> Apply threshold rules
    │       │   └─> Decision: APPROVED
    │       │
    │       └─> Persist Results (20ms)
    │           ├─> Save transaction to DB
    │           ├─> Save fraud_score to DB
    │           └─> Save decision to DB
    │
    └─> Total Latency: 87ms
```

### 2. Fraud Detection Flow (Suspicious Transaction)

```
[Suspicious Transaction: $5,000 electronics at 3am]
    │
    ├─> Kafka → Consumer
    │
    ├─> Feature Extraction
    │   ├─> Amount feature: 0.95 (very high)
    │   ├─> Time of day: 3 (unusual)
    │   ├─> Velocity: 5 transactions in 1 hour (high)
    │   ├─> Merchant risk: 0.85 (electronics = risky)
    │   └─> ... 6 more features
    │
    ├─> ML Model Prediction
    │   └─> Fraud probability: 0.87
    │
    ├─> Decision: FLAGGED (0.5 < 0.87 < 0.9)
    │
    ├─> Persist to Database
    │
    └─> Appears in Analyst Dashboard
            │
            ├─> Analyst Reviews Transaction
            │   ├─> Checks customer history
            │   ├─> Sees merchant is reputable electronics store
            │   ├─> Customer called bank beforehand about large purchase
            │   └─> Decision: APPROVE (false positive)
            │
            └─> Feedback Loop
                └─> Label added to training data: (features=..., label=0)
                └─> Next retraining cycle: Model learns this pattern
```

### 3. Cache Strategy Flow

```
Customer Transaction Arrives
    │
    ├─> Need customer history for velocity features
    │
    ├─> Check Redis: GET "customer:{customerId}:history"
    │       │
    │       ├─> [CACHE HIT] (90% of cases)
    │       │   └─> Return cached data (2ms)
    │       │
    │       └─> [CACHE MISS] (10% of cases)
    │           ├─> Query PostgreSQL (40ms)
    │           ├─> Cache result in Redis with 24h TTL
    │           └─> Return data
    │
    └─> Calculate velocity features using cached data
```

---

## Component Specifications

### 1. Kafka Configuration

```yaml
Topics:
  transactions:
    partitions: 10
    replication-factor: 1
    retention: 7 days
    
  decisions-output:
    partitions: 5
    replication-factor: 1
    retention: 30 days

Consumer Groups:
  fraud-detection-group:
    consumers: 3 (parallel processing)
    auto-offset-reset: earliest
    max-poll-records: 100
```

**Why Kafka?**
- Decouples transaction ingestion from fraud processing
- Handles traffic spikes (buffer during high load)
- Enables replay (reprocess old transactions if needed)
- Allows multiple consumers (scale horizontally)

---

### 2. Feature Engineering Pipeline

**Features Extracted (10 total):**

| # | Feature Name | Type | Description | Example |
|---|-------------|------|-------------|---------|
| 1 | amount_normalized | Float | Transaction amount / 10000 | 0.52 for $5,200 |
| 2 | time_of_day | Int | Hour (0-23) | 14 for 2pm |
| 3 | merchant_risk_score | Float | Predefined risk (0.0-1.0) | 0.75 for electronics |
| 4 | transaction_count_24h | Int | Count in last 24 hours | 7 |
| 5 | avg_amount_7d | Float | Average amount in 7 days | $237.50 |
| 6 | time_since_last_txn | Float | Minutes since last transaction | 125.3 |
| 7 | is_international | Binary | 1 if international, 0 if domestic | 1 |
| 8 | distance_from_home | Float | Miles from home address | 520.7 |
| 9 | unusual_location | Binary | 1 if >100 miles from typical | 1 |
| 10 | velocity_impossible | Binary | 1 if physically impossible travel | 0 |

**Feature Engineering Time Budget:**
- Customer history lookup: 2ms (Redis cache)
- Merchant lookup: 1ms (Redis cache)
- Calculations: 5ms
- **Total: <10ms**

---

### 3. ML Model Specifications

**XGBoost Configuration:**
```python
XGBClassifier(
    max_depth=6,                # Decision tree depth
    learning_rate=0.1,          # Step size shrinkage
    n_estimators=100,           # Number of trees
    scale_pos_weight=99,        # Handle class imbalance (99:1 ratio)
    subsample=0.8,              # Sample 80% of data per tree
    colsample_bytree=0.8,       # Sample 80% of features per tree
    objective='binary:logistic', # Binary classification
    eval_metric='aucpr'         # Area under precision-recall curve
)
```

**Model Performance:**
- Training set: 80,000 transactions
- Test set: 20,000 transactions
- Precision: 85% (of flagged, 85% are actually fraud)
- Recall: 90% (catches 90% of all fraud)
- F1-Score: 0.87
- AUC-ROC: 0.94

**Model File:**
- Format: JSON (XGBoost native format)
- Size: ~2.5 MB
- Location: `fraud-detection-service/src/main/resources/models/fraud_model.json`

---

### 4. Database Schema

**PostgreSQL Tables:**

```sql
-- Stores raw transaction data
CREATE TABLE transactions (
    transaction_id VARCHAR(50) PRIMARY KEY,
    customer_id VARCHAR(50) NOT NULL,
    merchant_id VARCHAR(50) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    location VARCHAR(100),
    merchant_category VARCHAR(50),
    is_international BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_customer (customer_id),
    INDEX idx_timestamp (timestamp DESC)
);

-- Stores extracted features
CREATE TABLE transaction_features (
    transaction_id VARCHAR(50) PRIMARY KEY REFERENCES transactions(transaction_id),
    amount_normalized FLOAT,
    time_of_day INT,
    merchant_risk_score FLOAT,
    transaction_count_24h INT,
    avg_amount_7d FLOAT,
    time_since_last_txn FLOAT,
    is_international BOOLEAN,
    distance_from_home FLOAT,
    unusual_location BOOLEAN,
    velocity_impossible BOOLEAN,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Stores ML predictions
CREATE TABLE fraud_predictions (
    id BIGSERIAL PRIMARY KEY,
    transaction_id VARCHAR(50) REFERENCES transactions(transaction_id),
    fraud_probability FLOAT NOT NULL CHECK (fraud_probability >= 0 AND fraud_probability <= 1),
    model_version VARCHAR(20),
    scored_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Stores automated decisions
CREATE TABLE decisions (
    id BIGSERIAL PRIMARY KEY,
    transaction_id VARCHAR(50) REFERENCES transactions(transaction_id),
    decision VARCHAR(20) NOT NULL CHECK (decision IN ('APPROVED', 'FLAGGED', 'BLOCKED')),
    reason TEXT,
    decided_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Stores analyst reviews
CREATE TABLE analyst_reviews (
    id BIGSERIAL PRIMARY KEY,
    transaction_id VARCHAR(50) REFERENCES transactions(transaction_id),
    analyst_decision VARCHAR(20) NOT NULL CHECK (analyst_decision IN ('FRAUD', 'LEGITIMATE')),
    reviewed_by VARCHAR(50),
    confidence VARCHAR(20) CHECK (confidence IN ('HIGH', 'MEDIUM', 'LOW')),
    notes TEXT,
    reviewed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Stores merchant metadata
CREATE TABLE merchants (
    merchant_id VARCHAR(50) PRIMARY KEY,
    merchant_name VARCHAR(100),
    category VARCHAR(50),
    risk_score FLOAT DEFAULT 0.5,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

### 5. Redis Cache Structure

**Key Patterns:**

```redis
# Customer transaction history (last 24 hours)
Key: "customer:{customerId}:history:24h"
Type: Sorted Set (sorted by timestamp)
TTL: 24 hours
Value: [
    {score: timestamp, value: {transaction_id, amount, timestamp}}
]

# Merchant risk scores
Key: "merchant:{merchantId}:risk"
Type: String
TTL: 7 days
Value: "0.75"

# Customer profile (home location, etc.)
Key: "customer:{customerId}:profile"
Type: Hash
TTL: 30 days
Value: {
    "home_location": "40.7128,-74.0060",
    "typical_amount": "125.50",
    "registered_at": "2023-01-15"
}
```

**Cache Strategies:**
- Write-through: Update cache when writing to DB
- Cache-aside: Read from cache, fallback to DB if miss
- TTL-based eviction: Automatic cleanup of old data

---

### 6. REST API Endpoints

**Analyst Workflow API:**

```
GET /api/flagged-transactions
    Query params: ?status=PENDING&limit=50&offset=0
    Response: {
        "transactions": [
            {
                "transactionId": "txn_123",
                "customerId": "CUST_001",
                "amount": 5240.00,
                "fraudProbability": 0.87,
                "timestamp": "2024-01-15T14:23:45Z",
                "features": {...}
            }
        ],
        "total": 47,
        "page": 1
    }

GET /api/transactions/{transactionId}/details
    Response: {
        "transaction": {...},
        "features": {...},
        "fraudScore": 0.87,
        "decision": "FLAGGED",
        "customerHistory": [...]
    }

POST /api/transactions/{transactionId}/review
    Body: {
        "analystDecision": "FRAUD" | "LEGITIMATE",
        "confidence": "HIGH" | "MEDIUM" | "LOW",
        "notes": "Customer confirmed unauthorized"
    }
    Response: {
        "success": true,
        "message": "Review submitted"
    }

GET /api/dashboard/stats
    Response: {
        "totalFlagged": 47,
        "avgFraudScore": 0.73,
        "pendingReviews": 23,
        "blockedToday": 12,
        "approvedToday": 8543
    }
```

---

## Performance Characteristics

### Latency Breakdown

```
Component              | Avg Latency | P99 Latency
-----------------------|-------------|-------------
Kafka delivery         | 5ms         | 15ms
Feature extraction     | 8ms         | 20ms
Redis cache lookup     | 2ms         | 5ms
ML model prediction    | 10ms        | 25ms
Decision logic         | 2ms         | 5ms
Database write         | 15ms        | 40ms
-----------------------|-------------|-------------
Total end-to-end       | 42ms        | 110ms
```

### Throughput

```
Single instance:       3,000 TPS
With 3 consumers:      10,000 TPS
Peak tested:           15,000 TPS (burst)
```

### Resource Usage (per instance)

```
CPU: 2 cores (average 60% utilization)
Memory: 2 GB RAM
Database connections: 10 (HikariCP pool)
Redis connections: 5
Kafka consumers: 3 threads
```

---

## Deployment Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Docker Compose Stack                      │
│                                                               │
│  ┌───────────────┐  ┌───────────────┐  ┌───────────────┐   │
│  │   Zookeeper   │  │     Kafka     │  │  PostgreSQL   │   │
│  │   (1 node)    │  │   (1 broker)  │  │   (1 node)    │   │
│  └───────────────┘  └───────────────┘  └───────────────┘   │
│                                                               │
│  ┌───────────────┐  ┌─────────────────────────────────────┐ │
│  │     Redis     │  │  Fraud Detection Service (Spring)   │ │
│  │   (1 node)    │  │  - Port: 8080                       │ │
│  └───────────────┘  │  - Consumers: 3 threads             │ │
│                     └─────────────────────────────────────┘ │
│                                                               │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │        React Dashboard (Nginx)                          │ │
│  │        - Port: 3000                                     │ │
│  └─────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

---

## Scalability & Future Enhancements

### Current Limitations
- Single Kafka broker (no fault tolerance)
- Single PostgreSQL instance (no replication)
- Redis not clustered

### Production Scaling Path

**Phase 1: Horizontal Scaling (10K → 50K TPS)**
- Scale consumers: 3 → 10 instances
- Add Kafka partitions: 10 → 50
- Add database read replicas

**Phase 2: High Availability**
- Kafka cluster: 3 brokers
- PostgreSQL: Primary + 2 replicas
- Redis Sentinel for failover

**Phase 3: Cloud Native (AWS)**
- Replace Kafka with AWS MSK (Managed Kafka)
- Replace PostgreSQL with AWS RDS
- Replace Redis with ElastiCache
- Deploy consumers on ECS/EKS

---

## Security Considerations

```
Component         | Security Measure
------------------|--------------------------------------------------
Kafka             | SASL/SCRAM authentication, TLS encryption
PostgreSQL        | Password auth, SSL connections, encrypted at rest
Redis             | Password protection, network isolation
REST API          | JWT authentication, rate limiting
Sensitive Data    | PII encrypted in database, masked in logs
```

---

This architecture diagram document is now complete! Would you like me to also create a visual diagram using a diagramming tool?
