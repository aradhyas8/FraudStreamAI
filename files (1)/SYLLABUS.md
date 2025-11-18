# RealTimeFraudDetect - 8 Week Syllabus

## Week 0: Foundations (Pre-Build Bootcamp)
**Total Hours: 15 hours**

### Day 1-2: Java + Spring Boot (6 hours)
**Learning Objectives:**
- Understand Spring Boot application structure
- Master REST API creation with @RestController
- Grasp dependency injection with @Autowired
- Learn service layer pattern with @Service

**Hands-On Tasks:**
1. Create Spring Boot project using Spring Initializr
2. Build REST API with 3 endpoints:
   - POST /api/users - Create user
   - GET /api/users/{id} - Get user by ID
   - GET /api/users - List all users
3. Implement UserService with business logic
4. Test all endpoints with Postman

**Learning Resources:**
- Spring Boot Tutorial for Beginners (freeCodeCamp - 3 hours)
- Official Spring Boot Getting Started Guide
- Practice: Build a simple TODO API

**Deliverable:** Working REST API with in-memory data storage

---

### Day 3: Apache Kafka (4 hours)
**Learning Objectives:**
- Understand Kafka architecture (topics, partitions, consumer groups)
- Learn producer/consumer patterns
- Understand message serialization/deserialization
- Master Docker Compose for Kafka setup

**Hands-On Tasks:**
1. Set up Kafka using Docker Compose
2. Create "test-topic" topic
3. Build Java producer that sends 100 messages
4. Build Java consumer that reads and prints messages
5. Experiment with consumer groups (2 consumers sharing load)

**Learning Resources:**
- Apache Kafka in 5 Minutes (Confluent YouTube)
- Kafka Quickstart Official Docs
- Practice: Build order processing system (producer = orders, consumer = processor)

**Deliverable:** Working Kafka producer/consumer with Docker

---

### Day 4: PostgreSQL + Spring Data JPA (3 hours)
**Learning Objectives:**
- Understand JPA entities and repositories
- Learn database schema design
- Master CRUD operations with Spring Data JPA
- Understand connection pooling (HikariCP)

**Hands-On Tasks:**
1. Add PostgreSQL to your Day 1 REST API
2. Create User entity with @Entity annotation
3. Create UserRepository extending JpaRepository
4. Replace in-memory storage with database persistence
5. Test CRUD operations

**Learning Resources:**
- Spring Data JPA Tutorial (Baeldung)
- PostgreSQL Tutorial (freeCodeCamp)
- Practice: Add relationships (User has many Orders)

**Deliverable:** REST API with PostgreSQL persistence

---

### Day 5: Redis (2 hours)
**Learning Objectives:**
- Understand Redis as in-memory cache
- Learn cache-aside pattern
- Master TTL (Time To Live) for cache expiration
- Understand Spring Cache abstraction

**Hands-On Tasks:**
1. Add Redis to your Spring Boot project
2. Implement caching for GET /api/users/{id}
3. Set 5-minute TTL for cached users
4. Measure performance improvement (before/after caching)

**Learning Resources:**
- Redis Crash Course (Traversy Media - 30 mins)
- Spring Cache Documentation
- Practice: Cache expensive calculations

**Deliverable:** REST API with Redis caching layer

---

## Week 1: Project Skeleton (End-to-End Flow)
**Total Hours: 12 hours | Goal: 1 transaction flowing through entire system**

### Monday (3 hours): Project Setup
**Tasks:**
1. Create GitHub repository: `realtime-fraud-detect`
2. Initialize Spring Boot project with dependencies:
   - Spring Web
   - Spring Kafka
   - Spring Data JPA
   - PostgreSQL Driver
   - Redis
   - Lombok
3. Set up multi-module Maven project:
   - fraud-detection-service (main app)
   - transaction-producer (simulator)
4. Create Docker Compose with Kafka, Zookeeper, PostgreSQL, Redis
5. Write README.md with project overview

**Deliverable:** Empty project structure with Docker Compose running

---

### Tuesday-Wednesday (5 hours): Kafka Pipeline
**Tasks:**
1. Create Transaction POJO:
   ```java
   public class Transaction {
       String transactionId;
       String customerId;
       String merchantId;
       Double amount;
       LocalDateTime timestamp;
       String location;
   }
   ```
2. Build transaction-producer module:
   - Send 1 hardcoded transaction to "transactions" topic every 5 seconds
   - Use JsonSerializer for Transaction
3. Build fraud-detection-service Kafka consumer:
   - Consume from "transactions" topic
   - Print transaction to console
   - Use @KafkaListener annotation

**Deliverable:** Transaction flowing from producer → Kafka → consumer

---

### Thursday (2 hours): Database Integration
**Tasks:**
1. Design PostgreSQL schema:
   - transactions table
   - fraud_scores table
2. Create Transaction entity
3. Create TransactionRepository
4. Modify Kafka consumer to save transaction to PostgreSQL
5. Verify data in database using DBeaver/pgAdmin

**Deliverable:** Transactions persisted to database

---

### Friday (2 hours): Testing + Documentation
**Tasks:**
1. Write unit tests for TransactionRepository
2. Write integration test for Kafka consumer
3. Update README with:
   - Architecture diagram (simple text diagram)
   - How to run locally
   - What you learned this week
4. Git commit with meaningful message

**Deliverable:** Tested, documented skeleton

---

## Week 2: Feature Engineering Pipeline
**Total Hours: 12 hours | Goal: Extract 10 fraud-relevant features**

### Monday (3 hours): Feature Extraction Framework
**Tasks:**
1. Create FeatureExtractor service
2. Implement 3 basic features:
   - `amountFeature`: Normalize transaction amount (0-1 scale)
   - `timeOfDayFeature`: Hour of day (0-23)
   - `isInternational`: Boolean (different country than customer home)
3. Create TransactionFeatures POJO to store features
4. Write unit tests for each feature

**Concepts to Learn:**
- Feature normalization/scaling
- Why features need to be numeric for ML

---

### Tuesday (3 hours): Customer History Features
**Tasks:**
1. Create CustomerHistoryService
2. Implement Redis caching for customer transaction history
3. Calculate velocity features:
   - `transactionCount24h`: Number of transactions in last 24 hours
   - `avgAmount7d`: Average transaction amount in last 7 days
   - `timeSinceLastTransaction`: Minutes since last transaction
4. Set 24-hour TTL for customer history cache

**Concepts to Learn:**
- Velocity checks (fraud teams call this "velocity")
- Why we cache customer data in Redis

---

### Wednesday (2 hours): Merchant Risk Features
**Tasks:**
1. Create merchants table in PostgreSQL
2. Seed database with 100 merchants (risk scores 0.0-1.0)
3. Create MerchantRepository
4. Implement merchant features:
   - `merchantRiskScore`: Predefined risk (0.0 = safe, 1.0 = risky)
   - `merchantCategory`: Map category to risk level (electronics = higher risk)
5. Cache merchant data in Redis

---

### Thursday (2 hours): Geographic Features
**Tasks:**
1. Implement geographic features:
   - `distanceFromHome`: Distance between transaction location and customer home
   - `unusualLocation`: Binary flag if location is >100 miles from typical
   - `locationVelocity`: Impossible travel (e.g., 500 miles in 10 minutes)
2. Use simple lat/long distance calculation (Haversine formula)

**Concepts to Learn:**
- Geographic anomaly detection
- Impossible travel detection

---

### Friday (2 hours): Feature Pipeline Integration
**Tasks:**
1. Integrate all features into Kafka consumer
2. Save extracted features to database (transaction_features table)
3. Create REST endpoint: GET /api/features/{transactionId}
4. Write integration tests
5. Performance test: Can we extract features in <50ms?

**Deliverable:** 10 features extracted for every transaction

---

## Week 3-4: Machine Learning Integration
**Total Hours: 15 hours | Goal: ML model predicting fraud probability**

### Week 3 Monday-Tuesday (5 hours): Data Generation
**Tasks:**
1. Create Python script: `generate_training_data.py`
2. Generate 100,000 synthetic transactions:
   - 99,000 legitimate (label = 0)
   - 1,000 fraud (label = 1)
3. Fraud patterns to embed:
   - High amount (>$5000) + risky merchant = fraud
   - Multiple transactions in 1 hour = fraud
   - International + unusual time = fraud
4. Export to CSV with features + label
5. Split: 80% train, 20% test

**Concepts to Learn:**
- Class imbalance problem (0.5% fraud is real-world ratio)
- Train/test split
- Why synthetic data is okay for portfolio projects

**Python Libraries:**
- pandas
- faker (for generating fake names, addresses)
- numpy

---

### Week 3 Wednesday-Thursday (4 hours): Model Training
**Tasks:**
1. Create Python script: `train_fraud_model.py`
2. Load training data
3. Handle class imbalance with SMOTE (Synthetic Minority Oversampling)
4. Train XGBoost classifier:
   ```python
   from xgboost import XGBClassifier
   model = XGBClassifier(scale_pos_weight=99)  # Handle imbalance
   model.fit(X_train, y_train)
   ```
5. Evaluate on test set:
   - Precision, Recall, F1-Score
   - Confusion matrix
6. Save model to `fraud_model.json`

**Concepts to Learn:**
- What XGBoost does (decision trees + boosting)
- Precision vs Recall tradeoff
- Confusion matrix (TP, FP, TN, FN)

**Target Metrics:**
- Precision: 85% (of flagged transactions, 85% are actually fraud)
- Recall: 90% (catch 90% of all fraud)

---

### Week 3 Friday (2 hours): Model Analysis
**Tasks:**
1. Generate feature importance plot
2. Analyze which features matter most (likely: amount, merchant_risk, velocity)
3. Test model on edge cases:
   - Elderly customer making first online purchase (should be flagged)
   - College student buying late-night pizza (should NOT be flagged)
4. Document model limitations in README

**Concepts to Learn:**
- Feature importance
- Model interpretability (why it matters for fraud)

---

### Week 4 Monday-Wednesday (4 hours): Java Integration
**Tasks:**
1. Add XGBoost4J dependency to pom.xml
2. Create FraudScoringService:
   ```java
   public class FraudScoringService {
       private Booster model;
       
       public double predictFraudProbability(TransactionFeatures features) {
           // Load model, create DMatrix, predict
       }
   }
   ```
3. Load trained model in Spring Boot @PostConstruct
4. Integrate into Kafka consumer:
   - Extract features → Score with ML → Save score to database
5. Create fraud_predictions table (transaction_id, fraud_probability, timestamp)

**Concepts to Learn:**
- How to load serialized ML model in Java
- DMatrix (XGBoost's data format)

**Deliverable:** Every transaction gets fraud probability (0.0-1.0)

---

## Week 5: Decision Engine + Analyst Workflow
**Total Hours: 10 hours | Goal: Automated decisions + human review**

### Monday-Tuesday (4 hours): Rule Engine
**Tasks:**
1. Create DecisionEngine service
2. Implement decision logic:
   ```java
   if (fraudProb > 0.9) return BLOCKED;
   if (fraudProb > 0.5) return FLAGGED_FOR_REVIEW;
   return APPROVED;
   ```
3. Create decisions table:
   - transaction_id, decision (BLOCKED/FLAGGED/APPROVED), timestamp
4. Integrate into Kafka consumer pipeline
5. Send decision to "decisions-output" Kafka topic

**Concepts to Learn:**
- Rule-based systems
- Why we don't auto-block everything (false positives hurt customers)

---

### Wednesday (3 hours): Analyst API
**Tasks:**
1. Create REST endpoints for analyst workflow:
   - GET /api/flagged-transactions?status=PENDING
   - GET /api/transactions/{id}/details (full context)
   - POST /api/transactions/{id}/review (analyst decision)
2. Create analyst_reviews table:
   - transaction_id, analyst_decision (FRAUD/LEGITIMATE), reviewed_by, timestamp
3. Implement feedback loop:
   - Analyst confirms fraud → Update label in training data
   - Trigger weekly model retraining (batch job)

**Deliverable:** API for analyst to review flagged transactions

---

### Thursday-Friday (3 hours): Testing + Scenarios
**Tasks:**
1. Test with realistic scenarios:
   - Legitimate: Small coffee purchase at regular time
   - Fraud: $5000 electronics purchase at 3am from new merchant
   - Edge case: Vacation travel (multiple cities in 1 week)
2. Verify decisions match expected outcomes
3. Write integration tests covering all 3 decision paths
4. Document fraud scenarios in README

---

## Week 6: Dashboard (Analyst UI)
**Total Hours: 12 hours | Goal: React dashboard for reviewing flagged transactions**

### Monday (3 hours): React Setup
**Tasks:**
1. Create React app: `npx create-react-app fraud-dashboard`
2. Install dependencies:
   - axios (HTTP client)
   - Material-UI (components)
   - react-router-dom (routing)
3. Set up project structure:
   - components/ (TransactionCard, ReviewModal)
   - services/ (api.js for backend calls)
   - pages/ (Dashboard, TransactionDetails)
4. Create proxy to backend in package.json

---

### Tuesday-Wednesday (5 hours): Dashboard UI
**Tasks:**
1. Build FlaggedTransactionsList component:
   - Fetch from GET /api/flagged-transactions
   - Display table with: transaction_id, customer, amount, merchant, fraud_score
   - Color code by risk (red = >0.9, yellow = 0.5-0.9)
2. Build TransactionDetails component:
   - Show all features extracted
   - Show ML score + decision
   - Show historical transactions for customer
3. Add filtering/sorting (by date, amount, risk score)

**Concepts to Learn:**
- React hooks (useState, useEffect)
- Fetching data from REST API
- Component composition

---

### Thursday (2 hours): Review Workflow
**Tasks:**
1. Build ReviewModal component:
   - Approve/Reject buttons
   - Comment field for analyst notes
   - Confidence level (High/Medium/Low)
2. POST to /api/transactions/{id}/review on button click
3. Update UI optimistically (remove from list immediately)
4. Show success/error toasts

---

### Friday (2 hours): Polish + Deployment
**Tasks:**
1. Add dashboard stats at top:
   - Total flagged today
   - Average fraud score
   - Review queue size
2. Dockerize React app
3. Add nginx reverse proxy (routes /api to backend, / to frontend)
4. Test full flow: Transaction → Flag → Analyst review → Feedback

**Deliverable:** Working analyst dashboard

---

## Week 7: Performance + Load Testing
**Total Hours: 8 hours | Goal: Prove 10K TPS claim**

### Monday-Tuesday (4 hours): Performance Optimization
**Tasks:**
1. Add monitoring/logging:
   - Log processing time for each stage
   - Add Micrometer metrics (Spring Boot Actuator)
2. Optimize bottlenecks:
   - Batch database writes (save 10 transactions at once)
   - Increase Kafka consumer threads (parallel processing)
   - Tune HikariCP connection pool size
3. Profile with VisualVM (find slow methods)
4. Target: <100ms end-to-end latency per transaction

**Concepts to Learn:**
- Application profiling
- Database connection pooling
- Kafka consumer concurrency

---

### Wednesday-Thursday (4 hours): Load Testing
**Tasks:**
1. Write JMeter test plan:
   - Simulate 10,000 transactions/second
   - Ramp up over 5 minutes
   - Run for 10 minutes
2. Monitor system during load test:
   - CPU/memory usage (Docker stats)
   - Database query performance
   - Kafka lag (consumer group lag)
3. Tune configuration if needed:
   - Increase Kafka partitions
   - Scale consumers to 3 instances
4. Document results in README:
   - "Successfully processed 6 million transactions in 10 minutes"
   - "Average latency: 87ms, p99 latency: 120ms"

**Deliverable:** Load test results proving scale

---

## Week 8: Production Ready + Documentation
**Total Hours: 15 hours | Goal: Portfolio-ready project**

### Monday-Tuesday (5 hours): Productionization
**Tasks:**
1. Add comprehensive logging:
   - Structured JSON logs
   - Log levels (INFO, WARN, ERROR)
   - Transaction correlation IDs (trace requests)
2. Add health checks:
   - /actuator/health endpoint
   - Check Kafka, PostgreSQL, Redis connectivity
3. Add error handling:
   - Global exception handler (@ControllerAdvice)
   - Circuit breaker for external services
   - Dead letter queue for failed transactions
4. Add Docker Compose production profile:
   - Persistent volumes for PostgreSQL
   - Resource limits (CPU/memory)

**Concepts to Learn:**
- 12-factor app principles
- Observability (logging, metrics, tracing)
- Resilience patterns (circuit breaker, retry)

---

### Wednesday (3 hours): Documentation
**Tasks:**
1. Write comprehensive README.md:
   - Project overview
   - Architecture diagram
   - Features list
   - Tech stack
   - How to run locally
   - API documentation
   - Performance metrics
2. Write CONTRIBUTING.md (if open source)
3. Add inline code comments for complex logic
4. Create API documentation with Swagger/OpenAPI

---

### Thursday (3 hours): Architecture Diagram + Demo Video
**Tasks:**
1. Create professional architecture diagram (draw.io or Lucidchart)
2. Record 5-minute demo video:
   - Show transaction flowing through system
   - Show ML scoring in action
   - Show analyst dashboard
   - Explain key technical decisions
3. Upload demo to YouTube (unlisted)
4. Add screenshots to README

---

### Friday (4 hours): Interview Prep
**Tasks:**
1. Write answers to 20 common interview questions:
   - "Walk me through your fraud detection project"
   - "How does the ML model work?"
   - "What was the hardest technical challenge?"
   - "How would you scale this to 100K TPS?"
2. Practice explaining system to non-technical person
3. Practice drawing architecture diagram on whiteboard
4. Prepare 3 "war stories" (challenges you overcame)
5. Update resume with project bullet points

---

## Post-Week 8: Optional Enhancements

### Enhancement 1: Advanced ML (if time permits)
- Add ensemble model (XGBoost + Isolation Forest)
- Implement online learning (model updates in real-time)
- Add SHAP values for explainability

### Enhancement 2: Cloud Deployment
- Deploy to AWS (ECS + RDS + ElastiCache)
- Set up CI/CD with GitHub Actions
- Add CloudWatch monitoring

### Enhancement 3: Advanced Features
- Graph-based fraud detection (network analysis)
- Real-time model retraining
- A/B testing framework for models

---

## Weekly Milestones Checklist

**Week 0:** ✅ Completed 4 tiny projects (REST API, Kafka, DB, Redis)
**Week 1:** ✅ 1 transaction flows end-to-end (Kafka → Java → DB)
**Week 2:** ✅ 10 features extracted for every transaction
**Week 3-4:** ✅ ML model predicting fraud probability
**Week 5:** ✅ Automated decision engine + analyst API
**Week 6:** ✅ Working React dashboard
**Week 7:** ✅ Load test proving 10K TPS
**Week 8:** ✅ Production-ready, documented, interview-ready

---

## Learning Outcomes by End of Week 8

**Technical Skills:**
- ✅ Spring Boot microservices
- ✅ Kafka event streaming
- ✅ PostgreSQL database design
- ✅ Redis caching strategies
- ✅ Machine learning integration
- ✅ React frontend development
- ✅ Docker containerization
- ✅ Load testing + performance tuning

**Domain Knowledge:**
- ✅ Fraud detection patterns (velocity, geography, behavior)
- ✅ Real-time event processing
- ✅ Human-in-the-loop ML systems

**Soft Skills:**
- ✅ Project planning + execution
- ✅ Technical documentation
- ✅ Problem-solving under constraints
- ✅ Iterative development

**Interview Readiness:**
- ✅ Can explain project in 2/5/10 minute versions
- ✅ Can answer "why this architecture?"
- ✅ Can discuss tradeoffs + alternatives
- ✅ Can demo working system
