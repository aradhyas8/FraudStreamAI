# RealTimeFraudDetect - Complete Folder Structure

## Root Directory Structure

```
realtime-fraud-detect/
├── README.md                          # Project overview, setup instructions
├── ARCHITECTURE.md                    # System architecture documentation
├── docker-compose.yml                 # Multi-container Docker setup
├── docker-compose.prod.yml           # Production configuration
├── .gitignore                         # Git ignore rules
├── pom.xml                            # Maven parent POM (multi-module)
│
├── fraud-detection-service/           # Main Spring Boot application
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/frauddetect/
│   │   │   │   ├── FraudDetectionApplication.java
│   │   │   │   │
│   │   │   │   ├── config/              # Configuration classes
│   │   │   │   │   ├── KafkaConfig.java
│   │   │   │   │   ├── RedisConfig.java
│   │   │   │   │   ├── DatabaseConfig.java
│   │   │   │   │   └── SecurityConfig.java
│   │   │   │   │
│   │   │   │   ├── model/               # Data models (POJOs)
│   │   │   │   │   ├── Transaction.java
│   │   │   │   │   ├── TransactionFeatures.java
│   │   │   │   │   ├── FraudPrediction.java
│   │   │   │   │   ├── Decision.java
│   │   │   │   │   ├── AnalystReview.java
│   │   │   │   │   ├── Merchant.java
│   │   │   │   │   └── Customer.java
│   │   │   │   │
│   │   │   │   ├── entity/              # JPA entities
│   │   │   │   │   ├── TransactionEntity.java
│   │   │   │   │   ├── FraudScoreEntity.java
│   │   │   │   │   ├── DecisionEntity.java
│   │   │   │   │   ├── AnalystReviewEntity.java
│   │   │   │   │   └── MerchantEntity.java
│   │   │   │   │
│   │   │   │   ├── repository/          # Data access layer
│   │   │   │   │   ├── TransactionRepository.java
│   │   │   │   │   ├── FraudScoreRepository.java
│   │   │   │   │   ├── DecisionRepository.java
│   │   │   │   │   ├── AnalystReviewRepository.java
│   │   │   │   │   └── MerchantRepository.java
│   │   │   │   │
│   │   │   │   ├── service/             # Business logic
│   │   │   │   │   ├── TransactionConsumerService.java
│   │   │   │   │   ├── FeatureExtractionService.java
│   │   │   │   │   ├── FraudScoringService.java
│   │   │   │   │   ├── DecisionEngineService.java
│   │   │   │   │   ├── AnalystWorkflowService.java
│   │   │   │   │   ├── CustomerHistoryService.java
│   │   │   │   │   ├── MerchantRiskService.java
│   │   │   │   │   └── ModelRetrainingService.java
│   │   │   │   │
│   │   │   │   ├── controller/          # REST API endpoints
│   │   │   │   │   ├── TransactionController.java
│   │   │   │   │   ├── AnalystController.java
│   │   │   │   │   ├── DashboardController.java
│   │   │   │   │   └── HealthCheckController.java
│   │   │   │   │
│   │   │   │   ├── kafka/               # Kafka components
│   │   │   │   │   ├── TransactionConsumer.java
│   │   │   │   │   ├── DecisionProducer.java
│   │   │   │   │   ├── TransactionDeserializer.java
│   │   │   │   │   └── DecisionSerializer.java
│   │   │   │   │
│   │   │   │   ├── ml/                  # Machine learning components
│   │   │   │   │   ├── ModelLoader.java
│   │   │   │   │   ├── FeatureTransformer.java
│   │   │   │   │   ├── PredictionService.java
│   │   │   │   │   └── ModelEvaluator.java
│   │   │   │   │
│   │   │   │   ├── feature/             # Feature engineering
│   │   │   │   │   ├── AmountFeatureExtractor.java
│   │   │   │   │   ├── VelocityFeatureExtractor.java
│   │   │   │   │   ├── GeographicFeatureExtractor.java
│   │   │   │   │   ├── MerchantFeatureExtractor.java
│   │   │   │   │   └── TimeFeatureExtractor.java
│   │   │   │   │
│   │   │   │   ├── cache/               # Redis caching
│   │   │   │   │   ├── CustomerCacheService.java
│   │   │   │   │   └── MerchantCacheService.java
│   │   │   │   │
│   │   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   │   │   ├── TransactionRequest.java
│   │   │   │   │   ├── TransactionResponse.java
│   │   │   │   │   ├── FlaggedTransactionDTO.java
│   │   │   │   │   ├── AnalystReviewRequest.java
│   │   │   │   │   └── DashboardStatsDTO.java
│   │   │   │   │
│   │   │   │   ├── exception/           # Custom exceptions
│   │   │   │   │   ├── TransactionNotFoundException.java
│   │   │   │   │   ├── ModelLoadException.java
│   │   │   │   │   └── GlobalExceptionHandler.java
│   │   │   │   │
│   │   │   │   └── util/                # Utility classes
│   │   │   │       ├── DateUtils.java
│   │   │   │       ├── GeoUtils.java
│   │   │   │       └── ValidationUtils.java
│   │   │   │
│   │   │   └── resources/
│   │   │       ├── application.yml                # Main config
│   │   │       ├── application-dev.yml           # Dev environment
│   │   │       ├── application-prod.yml          # Production
│   │   │       ├── db/migration/                 # Flyway migrations
│   │   │       │   ├── V1__create_transactions_table.sql
│   │   │       │   ├── V2__create_fraud_scores_table.sql
│   │   │       │   ├── V3__create_decisions_table.sql
│   │   │       │   ├── V4__create_analyst_reviews_table.sql
│   │   │       │   └── V5__create_merchants_table.sql
│   │   │       └── models/
│   │   │           └── fraud_model.json          # Trained ML model
│   │   │
│   │   └── test/
│   │       └── java/com/frauddetect/
│   │           ├── service/
│   │           │   ├── FeatureExtractionServiceTest.java
│   │           │   ├── FraudScoringServiceTest.java
│   │           │   └── DecisionEngineServiceTest.java
│   │           ├── integration/
│   │           │   ├── KafkaIntegrationTest.java
│   │           │   └── EndToEndIntegrationTest.java
│   │           └── util/
│   │               └── TestDataBuilder.java
│   │
│   ├── pom.xml                          # Maven dependencies
│   └── Dockerfile                       # Container image
│
├── transaction-producer/                # Transaction simulator
│   ├── src/
│   │   └── main/
│   │       ├── java/com/frauddetect/producer/
│   │       │   ├── ProducerApplication.java
│   │       │   ├── TransactionGenerator.java
│   │       │   ├── TransactionProducer.java
│   │       │   └── config/
│   │       │       └── KafkaProducerConfig.java
│   │       └── resources/
│   │           ├── application.yml
│   │           └── sample-transactions.json
│   ├── pom.xml
│   └── Dockerfile
│
├── ml-training/                         # Python ML training scripts
│   ├── requirements.txt                # Python dependencies
│   ├── data/                           # Training data
│   │   ├── raw/
│   │   │   └── transactions.csv
│   │   └── processed/
│   │       ├── train.csv
│   │       └── test.csv
│   ├── notebooks/                      # Jupyter notebooks (exploration)
│   │   ├── 01_data_exploration.ipynb
│   │   ├── 02_feature_engineering.ipynb
│   │   └── 03_model_training.ipynb
│   ├── scripts/
│   │   ├── generate_training_data.py   # Synthetic data generation
│   │   ├── train_fraud_model.py        # Model training
│   │   ├── evaluate_model.py           # Model evaluation
│   │   └── export_model.py             # Export to Java-compatible format
│   ├── models/                         # Saved models
│   │   ├── fraud_model.json
│   │   ├── fraud_model_v2.json
│   │   └── model_metrics.json
│   └── utils/
│       ├── feature_engineering.py
│       └── data_generator.py
│
├── fraud-dashboard/                     # React frontend
│   ├── public/
│   │   ├── index.html
│   │   └── favicon.ico
│   ├── src/
│   │   ├── components/
│   │   │   ├── TransactionCard.jsx
│   │   │   ├── TransactionList.jsx
│   │   │   ├── ReviewModal.jsx
│   │   │   ├── DashboardStats.jsx
│   │   │   ├── TransactionDetails.jsx
│   │   │   ├── FeatureView.jsx
│   │   │   └── FilterBar.jsx
│   │   ├── pages/
│   │   │   ├── Dashboard.jsx
│   │   │   ├── TransactionDetailPage.jsx
│   │   │   └── AnalystQueue.jsx
│   │   ├── services/
│   │   │   └── api.js                  # API client (Axios)
│   │   ├── utils/
│   │   │   ├── formatters.js
│   │   │   └── constants.js
│   │   ├── App.js
│   │   ├── App.css
│   │   └── index.js
│   ├── package.json
│   ├── Dockerfile
│   └── nginx.conf                      # Nginx reverse proxy
│
├── load-testing/                        # Performance testing
│   ├── jmeter/
│   │   ├── fraud-load-test.jmx
│   │   └── results/
│   │       ├── load-test-report.html
│   │       └── metrics.csv
│   └── scripts/
│       ├── generate-test-data.sh
│       └── run-load-test.sh
│
├── infrastructure/                      # Infrastructure as Code
│   ├── terraform/                      # AWS infrastructure (optional)
│   │   ├── main.tf
│   │   ├── variables.tf
│   │   └── outputs.tf
│   └── kubernetes/                     # K8s manifests (optional)
│       ├── deployment.yaml
│       ├── service.yaml
│       └── ingress.yaml
│
├── docs/                               # Documentation
│   ├── ARCHITECTURE.md
│   ├── API.md                         # API documentation
│   ├── DATABASE_SCHEMA.md
│   ├── ML_MODEL.md                    # Model documentation
│   ├── DEPLOYMENT.md
│   └── images/
│       ├── architecture-diagram.png
│       ├── data-flow.png
│       └── dashboard-screenshot.png
│
└── scripts/                            # Utility scripts
    ├── setup-dev-env.sh               # Set up local development
    ├── seed-database.sh               # Seed initial data
    ├── start-services.sh              # Start all services
    ├── stop-services.sh               # Stop all services
    └── backup-database.sh             # Backup PostgreSQL

```

---

## Key Files Explained

### Root Level Files

**docker-compose.yml**
```yaml
version: '3.8'
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:latest
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
  
  kafka:
    image: confluentinc/cp-kafka:latest
    depends_on:
      - zookeeper
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092
  
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: fraud_detection
      POSTGRES_USER: fraud_user
      POSTGRES_PASSWORD: fraud_pass
    volumes:
      - postgres-data:/var/lib/postgresql/data
  
  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
  
  fraud-service:
    build: ./fraud-detection-service
    depends_on:
      - kafka
      - postgres
      - redis
    environment:
      SPRING_PROFILES_ACTIVE: dev
  
  fraud-dashboard:
    build: ./fraud-dashboard
    ports:
      - "3000:80"

volumes:
  postgres-data:
```

---

### fraud-detection-service Key Files

**application.yml**
```yaml
spring:
  application:
    name: fraud-detection-service
  
  datasource:
    url: jdbc:postgresql://postgres:5432/fraud_detection
    username: fraud_user
    password: fraud_pass
  
  kafka:
    bootstrap-servers: kafka:9092
    consumer:
      group-id: fraud-detection-group
      auto-offset-reset: earliest
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
  
  redis:
    host: redis
    port: 6379

fraud:
  model:
    path: classpath:models/fraud_model.json
  decision:
    block-threshold: 0.9
    flag-threshold: 0.5
```

**Transaction.java (Model)**
```java
package com.frauddetect.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Transaction {
    private String transactionId;
    private String customerId;
    private String merchantId;
    private Double amount;
    private LocalDateTime timestamp;
    private String location;
    private String merchantCategory;
    private String currency;
    private boolean isInternational;
}
```

**FeatureExtractionService.java**
```java
package com.frauddetect.service;

import com.frauddetect.model.Transaction;
import com.frauddetect.model.TransactionFeatures;
import org.springframework.stereotype.Service;

@Service
public class FeatureExtractionService {
    
    public TransactionFeatures extractFeatures(Transaction transaction) {
        TransactionFeatures features = new TransactionFeatures();
        
        // Extract 10 features
        features.setAmountNormalized(normalizeAmount(transaction.getAmount()));
        features.setTimeOfDay(extractTimeOfDay(transaction.getTimestamp()));
        features.setMerchantRiskScore(getMerchantRisk(transaction.getMerchantId()));
        // ... 7 more features
        
        return features;
    }
    
    private double normalizeAmount(double amount) {
        // Min-max normalization
        return Math.min(amount / 10000.0, 1.0);
    }
}
```

---

### ml-training Key Files

**generate_training_data.py**
```python
import pandas as pd
import numpy as np
from faker import Faker
import random

fake = Faker()

def generate_transaction(is_fraud=False):
    """Generate a single transaction"""
    transaction = {
        'transaction_id': fake.uuid4(),
        'customer_id': f'CUST_{random.randint(1000, 9999)}',
        'merchant_id': f'MERCH_{random.randint(100, 999)}',
        'amount': generate_amount(is_fraud),
        'timestamp': fake.date_time_this_year(),
        'location': fake.city(),
        'merchant_category': random.choice(['grocery', 'gas', 'electronics', 'restaurant']),
        'is_fraud': int(is_fraud)
    }
    return transaction

def generate_amount(is_fraud):
    """Generate transaction amount based on fraud pattern"""
    if is_fraud:
        return random.uniform(3000, 10000)  # High amounts for fraud
    else:
        return random.uniform(5, 500)  # Normal spending

# Generate 100K transactions
transactions = []
for i in range(99000):
    transactions.append(generate_transaction(is_fraud=False))
for i in range(1000):
    transactions.append(generate_transaction(is_fraud=True))

df = pd.DataFrame(transactions)
df.to_csv('data/raw/transactions.csv', index=False)
print(f"Generated {len(df)} transactions")
```

**train_fraud_model.py**
```python
import pandas as pd
import xgboost as xgb
from sklearn.model_selection import train_test_split
from sklearn.metrics import classification_report

# Load data
df = pd.read_csv('data/processed/train.csv')
X = df.drop('is_fraud', axis=1)
y = df['is_fraud']

# Split
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

# Train model
model = xgb.XGBClassifier(
    scale_pos_weight=99,  # Handle class imbalance
    max_depth=6,
    learning_rate=0.1,
    n_estimators=100
)

model.fit(X_train, y_train)

# Evaluate
predictions = model.predict(X_test)
print(classification_report(y_test, predictions))

# Save model
model.save_model('models/fraud_model.json')
print("Model saved to models/fraud_model.json")
```

---

### fraud-dashboard Key Files

**api.js**
```javascript
import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

export const getFlaggedTransactions = async (status = 'PENDING') => {
  const response = await axios.get(`${API_BASE_URL}/flagged-transactions`, {
    params: { status }
  });
  return response.data;
};

export const submitReview = async (transactionId, decision) => {
  const response = await axios.post(
    `${API_BASE_URL}/transactions/${transactionId}/review`,
    decision
  );
  return response.data;
};
```

**Dashboard.jsx**
```javascript
import React, { useState, useEffect } from 'react';
import { getFlaggedTransactions } from '../services/api';
import TransactionList from '../components/TransactionList';
import DashboardStats from '../components/DashboardStats';

function Dashboard() {
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadTransactions();
  }, []);

  const loadTransactions = async () => {
    try {
      const data = await getFlaggedTransactions();
      setTransactions(data);
    } catch (error) {
      console.error('Error loading transactions:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="dashboard">
      <h1>Fraud Detection Dashboard</h1>
      <DashboardStats transactions={transactions} />
      <TransactionList transactions={transactions} onReview={loadTransactions} />
    </div>
  );
}

export default Dashboard;
```

---

## Database Schema (SQL Migration Files)

**V1__create_transactions_table.sql**
```sql
CREATE TABLE transactions (
    transaction_id VARCHAR(50) PRIMARY KEY,
    customer_id VARCHAR(50) NOT NULL,
    merchant_id VARCHAR(50) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    location VARCHAR(100),
    merchant_category VARCHAR(50),
    is_international BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_customer_id ON transactions(customer_id);
CREATE INDEX idx_timestamp ON transactions(timestamp DESC);
```

**V2__create_fraud_scores_table.sql**
```sql
CREATE TABLE fraud_scores (
    id BIGSERIAL PRIMARY KEY,
    transaction_id VARCHAR(50) REFERENCES transactions(transaction_id),
    fraud_probability DECIMAL(5, 4) NOT NULL,
    model_version VARCHAR(20),
    features JSONB,
    scored_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_fraud_scores_txn ON fraud_scores(transaction_id);
```

**V3__create_decisions_table.sql**
```sql
CREATE TYPE decision_type AS ENUM ('APPROVED', 'FLAGGED', 'BLOCKED');

CREATE TABLE decisions (
    id BIGSERIAL PRIMARY KEY,
    transaction_id VARCHAR(50) REFERENCES transactions(transaction_id),
    decision decision_type NOT NULL,
    reason TEXT,
    decided_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## File Size Estimates

```
fraud-detection-service/     ~50 Java files, ~5000 lines of code
transaction-producer/        ~5 Java files, ~300 lines of code
ml-training/                 ~8 Python files, ~1000 lines of code
fraud-dashboard/             ~15 React files, ~2000 lines of code
Total project size:          ~15 MB (excluding dependencies)
```

---

## Navigation Tips

**When working on features:**
1. Start with model/ (define data structures)
2. Create repository/ (data access)
3. Build service/ (business logic)
4. Add controller/ (REST endpoints)
5. Write tests in test/

**When debugging:**
1. Check logs in fraud-detection-service/logs/
2. Verify Kafka messages in Kafka UI (if enabled)
3. Query database directly with DBeaver
4. Check Redis cache with redis-cli

**When documenting:**
1. Update README.md for high-level changes
2. Add inline comments for complex logic
3. Update API.md when adding endpoints
4. Document ML changes in ML_MODEL.md
