# RealTimeFraudDetect - Step-by-Step Development Plan

## How to Use This Plan

**Daily Workflow:**
1. Read the day's tasks (morning)
2. Complete tasks in order
3. Test each component before moving on
4. Update your learning journal (what you learned, challenges faced)
5. Git commit with meaningful message
6. Check off completed tasks

**When Stuck:**
- Google the specific error message
- Check official documentation
- Try Stack Overflow
- Simplify the problem (reduce to minimal example)
- Take a break and come back

---

# Week 0: Foundations Bootcamp

## Day 1: Spring Boot REST API (3 hours)

### Morning Session (1.5 hours)
**Task 1.1: Watch Tutorial (1 hour)**
- Watch: "Spring Boot Tutorial for Beginners" (freeCodeCamp, first hour)
- Focus on: @RestController, @Service, @Autowired annotations
- Take notes on dependency injection concept

**Task 1.2: Create Project (30 mins)**
```bash
# Go to https://start.spring.io/
# Configure:
# - Project: Maven
# - Language: Java
# - Spring Boot: 3.2.0
# - Dependencies: Spring Web, Lombok
# - Package name: com.practice.userapi
# Download and open in IntelliJ
```

### Afternoon Session (1.5 hours)
**Task 1.3: Build User POJO (15 mins)**
```java
// src/main/java/com/practice/userapi/model/User.java
package com.practice.userapi.model;

import lombok.Data;

@Data
public class User {
    private String id;
    private String name;
    private String email;
}
```

**Task 1.4: Build UserService (20 mins)**
```java
// src/main/java/com/practice/userapi/service/UserService.java
package com.practice.userapi.service;

import com.practice.userapi.model.User;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    private List<User> users = new ArrayList<>();
    
    public User createUser(User user) {
        user.setId(UUID.randomUUID().toString());
        users.add(user);
        return user;
    }
    
    public User getUserById(String id) {
        return users.stream()
            .filter(u -> u.getId().equals(id))
            .findFirst()
            .orElse(null);
    }
    
    public List<User> getAllUsers() {
        return users;
    }
}
```

**Task 1.5: Build REST Controller (25 mins)**
```java
// src/main/java/com/practice/userapi/controller/UserController.java
package com.practice.userapi.controller;

import com.practice.userapi.model.User;
import com.practice.userapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }
    
    @GetMapping("/{id}")
    public User getUser(@PathVariable String id) {
        return userService.getUserById(id);
    }
    
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
}
```

**Task 1.6: Test with Postman (30 mins)**
1. Start application: `mvn spring-boot:run`
2. Test POST `http://localhost:8080/api/users`
   ```json
   {
     "name": "John Doe",
     "email": "john@example.com"
   }
   ```
3. Copy the returned ID
4. Test GET `http://localhost:8080/api/users/{id}`
5. Test GET `http://localhost:8080/api/users`
6. Create 3 more users and verify

**✅ Day 1 Checklist:**
- [ ] Spring Boot project created
- [ ] User POJO created with Lombok
- [ ] UserService with 3 methods working
- [ ] REST API with 3 endpoints
- [ ] All endpoints tested in Postman
- [ ] Understanding of @RestController, @Service, @Autowired

---

## Day 2: Spring Boot + PostgreSQL (3 hours)

### Morning Session (1.5 hours)
**Task 2.1: Add PostgreSQL Dependencies (15 mins)**
```xml
<!-- Add to pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>
```

**Task 2.2: Setup PostgreSQL with Docker (20 mins)**
```yaml
# Create docker-compose.yml in project root
version: '3.8'
services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: userdb
      POSTGRES_USER: user
      POSTGRES_PASSWORD: pass
    ports:
      - "5432:5432"
```
```bash
docker-compose up -d
```

**Task 2.3: Configure Database Connection (15 mins)**
```yaml
# src/main/resources/application.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/userdb
    username: user
    password: pass
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

**Task 2.4: Create JPA Entity (20 mins)**
```java
// Update User.java to be a JPA entity
package com.practice.userapi.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    private String name;
    private String email;
}
```

**Task 2.5: Create Repository (20 mins)**
```java
// src/main/java/com/practice/userapi/repository/UserRepository.java
package com.practice.userapi.repository;

import com.practice.userapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
}
```

### Afternoon Session (1.5 hours)
**Task 2.6: Update Service to Use Repository (30 mins)**
```java
// Update UserService.java
@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public User createUser(User user) {
        return userRepository.save(user);
    }
    
    public User getUserById(String id) {
        return userRepository.findById(id).orElse(null);
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
```

**Task 2.7: Test End-to-End (30 mins)**
1. Restart application
2. Create 5 users via Postman
3. Verify in database:
   ```bash
   docker exec -it <container-id> psql -U user -d userdb
   SELECT * FROM users;
   ```
4. Restart application and verify users persist

**Task 2.8: Install DBeaver (30 mins)**
1. Download DBeaver Community
2. Connect to PostgreSQL:
   - Host: localhost, Port: 5432
   - Database: userdb, User: user, Password: pass
3. View users table
4. Practice SQL queries

**✅ Day 2 Checklist:**
- [ ] PostgreSQL running in Docker
- [ ] User entity with JPA annotations
- [ ] UserRepository created
- [ ] Service updated to use repository
- [ ] Data persists after restart
- [ ] DBeaver connected and working

---

## Day 3: Apache Kafka (4 hours)

### Morning Session (2 hours)
**Task 3.1: Watch Kafka Tutorial (1 hour)**
- Watch: "Apache Kafka in 5 Minutes" + "Kafka Explained" (Confluent)
- Understand: topics, producers, consumers, partitions

**Task 3.2: Setup Kafka with Docker (30 mins)**
```yaml
# Create new kafka-practice/docker-compose.yml
version: '3.8'
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:latest
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
    ports:
      - "2181:2181"
  
  kafka:
    image: confluentinc/cp-kafka:latest
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
```
```bash
cd kafka-practice
docker-compose up -d
```

**Task 3.3: Create Spring Boot Kafka Project (30 mins)**
```bash
# Go to start.spring.io
# Dependencies: Spring Web, Spring for Apache Kafka
# Create two projects: kafka-producer, kafka-consumer
```

### Afternoon Session (2 hours)
**Task 3.4: Build Message POJO (15 mins)**
```java
// Both projects need this
package com.practice.kafka.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderMessage {
    private String orderId;
    private String product;
    private Double price;
}
```

**Task 3.5: Build Producer (45 mins)**
```java
// kafka-producer/config/KafkaProducerConfig.java
@Configuration
public class KafkaProducerConfig {
    @Bean
    public ProducerFactory<String, OrderMessage> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }
    
    @Bean
    public KafkaTemplate<String, OrderMessage> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}

// kafka-producer/service/OrderProducer.java
@Service
public class OrderProducer {
    
    @Autowired
    private KafkaTemplate<String, OrderMessage> kafkaTemplate;
    
    public void sendOrder(OrderMessage order) {
        kafkaTemplate.send("orders", order);
        System.out.println("Sent: " + order);
    }
}

// kafka-producer/controller/OrderController.java
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    @Autowired
    private OrderProducer producer;
    
    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody OrderMessage order) {
        order.setOrderId(UUID.randomUUID().toString());
        producer.sendOrder(order);
        return ResponseEntity.ok("Order sent to Kafka");
    }
}
```

**Task 3.6: Build Consumer (45 mins)**
```java
// kafka-consumer/config/KafkaConsumerConfig.java
@Configuration
public class KafkaConsumerConfig {
    @Bean
    public ConsumerFactory<String, OrderMessage> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "order-consumer-group");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return new DefaultKafkaConsumerFactory<>(config);
    }
    
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderMessage> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OrderMessage> factory = 
            new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}

// kafka-consumer/service/OrderConsumer.java
@Service
public class OrderConsumer {
    
    @KafkaListener(topics = "orders", groupId = "order-consumer-group")
    public void consumeOrder(OrderMessage order) {
        System.out.println("Received: " + order);
    }
}
```

**Task 3.7: Test Kafka Flow (15 mins)**
1. Start both applications (producer on 8080, consumer on 8081)
2. POST to producer:
   ```json
   {
     "product": "Laptop",
     "price": 999.99
   }
   ```
3. Check consumer console - should print received message
4. Send 10 orders and verify all received

**✅ Day 3 Checklist:**
- [ ] Kafka + Zookeeper running in Docker
- [ ] Producer application sending messages
- [ ] Consumer application receiving messages
- [ ] Understanding of topics, serialization
- [ ] 10 successful message deliveries

---

## Day 4: Redis Caching (2 hours)

### Task 4.1: Add Redis to Day 2 Project (30 mins)
```xml
<!-- Add to pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
```

```yaml
# Update docker-compose.yml
services:
  # ... existing postgres service ...
  
  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
```

```bash
docker-compose up -d
```

### Task 4.2: Enable Caching (30 mins)
```java
// Main application class
@SpringBootApplication
@EnableCaching
public class UserApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApiApplication.class, args);
    }
}

// Update UserService
@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Cacheable(value = "users", key = "#id")
    public User getUserById(String id) {
        System.out.println("Fetching from database..."); // Debug log
        return userRepository.findById(id).orElse(null);
    }
    
    @CachePut(value = "users", key = "#user.id")
    public User createUser(User user) {
        return userRepository.save(user);
    }
    
    @CacheEvict(value = "users", allEntries = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
```

### Task 4.3: Test Caching (1 hour)
1. Restart application
2. Create a user, note the ID
3. GET user by ID - should see "Fetching from database..." in logs
4. GET same user again - should NOT see log (cached)
5. Verify in Redis:
   ```bash
   docker exec -it <redis-container> redis-cli
   KEYS *
   GET users::<user-id>
   ```
6. Measure performance:
   - First request: ~50ms (database)
   - Cached request: ~2ms (Redis)

**✅ Day 4 Checklist:**
- [ ] Redis running in Docker
- [ ] @Cacheable working on getUserById
- [ ] Can see cached data in Redis
- [ ] Performance improvement measured
- [ ] Understanding of cache-aside pattern

---

# Week 1: Project Skeleton

## Monday: Project Setup (3 hours)

### Task 1.1: Create GitHub Repository (30 mins)
```bash
# Create new repository on GitHub: realtime-fraud-detect
git clone https://github.com/YOUR_USERNAME/realtime-fraud-detect.git
cd realtime-fraud-detect

# Initialize with README
echo "# RealTimeFraudDetect" > README.md
git add README.md
git commit -m "Initial commit"
git push origin main
```

### Task 1.2: Create Multi-Module Maven Project (1 hour)
```bash
# Create parent pom.xml
```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.frauddetect</groupId>
    <artifactId>realtime-fraud-detect</artifactId>
    <version>1.0.0</version>
    <packaging>pom</packaging>
    
    <modules>
        <module>fraud-detection-service</module>
        <module>transaction-producer</module>
    </modules>
    
    <properties>
        <java.version>17</java.version>
        <spring-boot.version>3.2.0</spring-boot.version>
    </properties>
    
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>${spring-boot.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
</project>
```

```bash
# Create child modules using Spring Initializr
# Module 1: fraud-detection-service
# Module 2: transaction-producer

# Adjust their pom.xml to reference parent
```

### Task 1.3: Create Docker Compose (1 hour)
```yaml
# docker-compose.yml in root
version: '3.8'

services:
  zookeeper:
    image: confluentinc/cp-zookeeper:latest
    hostname: zookeeper
    container_name: fraud-zookeeper
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
  
  kafka:
    image: confluentinc/cp-kafka:latest
    hostname: kafka
    container_name: fraud-kafka
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
  
  postgres:
    image: postgres:15
    container_name: fraud-postgres
    environment:
      POSTGRES_DB: fraud_detection
      POSTGRES_USER: fraud_user
      POSTGRES_PASSWORD: fraud_pass
    ports:
      - "5432:5432"
    volumes:
      - postgres-data:/var/lib/postgresql/data
  
  redis:
    image: redis:7-alpine
    container_name: fraud-redis
    ports:
      - "6379:6379"

volumes:
  postgres-data:
```

```bash
docker-compose up -d
docker ps  # Verify all services running
```

### Task 1.4: Initialize README (30 mins)
```markdown
# RealTimeFraudDetect

Real-time fraud detection system using ML, Kafka, and Spring Boot.

## Architecture
- Kafka: Event streaming
- PostgreSQL: Transaction storage
- Redis: Feature caching
- XGBoost: ML model
- Spring Boot: Backend
- React: Dashboard

## Getting Started
\`\`\`bash
# Start infrastructure
docker-compose up -d

# Build and run
mvn clean install
cd fraud-detection-service && mvn spring-boot:run
\`\`\`

## Progress
- [x] Week 0: Foundations
- [x] Week 1 Day 1: Project setup
- [ ] Week 1 Day 2-3: Kafka pipeline
```

**✅ Monday Checklist:**
- [ ] GitHub repo created
- [ ] Multi-module Maven structure
- [ ] Docker Compose with 4 services running
- [ ] README initialized
- [ ] Git commit: "Week 1 Day 1: Project setup"

---

## Tuesday: Kafka Pipeline Part 1 (2.5 hours)

### Task 2.1: Create Transaction Model (30 mins)
```java
// fraud-detection-service/model/Transaction.java
package com.frauddetect.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    private String transactionId;
    private String customerId;
    private String merchantId;
    private Double amount;
    private LocalDateTime timestamp;
    private String location;
    private String merchantCategory;
    private Boolean isInternational;
}
```

### Task 2.2: Build Transaction Producer (1.5 hours)
```java
// transaction-producer/TransactionProducer.java
@Service
public class TransactionProducer {
    
    @Autowired
    private KafkaTemplate<String, Transaction> kafkaTemplate;
    
    @Scheduled(fixedRate = 5000) // Every 5 seconds
    public void sendTransaction() {
        Transaction txn = generateRandomTransaction();
        kafkaTemplate.send("transactions", txn.getTransactionId(), txn);
        System.out.println("Sent transaction: " + txn.getTransactionId());
    }
    
    private Transaction generateRandomTransaction() {
        Transaction txn = new Transaction();
        txn.setTransactionId(UUID.randomUUID().toString());
        txn.setCustomerId("CUST_" + (new Random().nextInt(1000)));
        txn.setMerchantId("MERCH_" + (new Random().nextInt(100)));
        txn.setAmount(50 + (new Random().nextDouble() * 950)); // $50-$1000
        txn.setTimestamp(LocalDateTime.now());
        txn.setLocation("New York");
        txn.setMerchantCategory("retail");
        txn.setIsInternational(false);
        return txn;
    }
}
```

### Task 2.3: Test Producer (30 mins)
```bash
cd transaction-producer
mvn spring-boot:run

# Should see: "Sent transaction: <uuid>" every 5 seconds

# Verify in Kafka
docker exec -it fraud-kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic transactions \
  --from-beginning
```

**✅ Tuesday Checklist:**
- [ ] Transaction POJO created
- [ ] Producer sends 1 transaction every 5 seconds
- [ ] Can see messages in Kafka console consumer
- [ ] Git commit: "Week 1: Kafka producer"

---

## Wednesday: Kafka Pipeline Part 2 (2.5 hours)

### Task 3.1: Create Transaction Entity (30 mins)
```java
// fraud-detection-service/entity/TransactionEntity.java
@Entity
@Table(name = "transactions")
@Data
public class TransactionEntity {
    @Id
    private String transactionId;
    private String customerId;
    private String merchantId;
    private Double amount;
    private LocalDateTime timestamp;
    private String location;
    private String merchantCategory;
    private Boolean isInternational;
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
```

### Task 3.2: Create Repository (15 mins)
```java
// fraud-detection-service/repository/TransactionRepository.java
@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, String> {
    List<TransactionEntity> findByCustomerId(String customerId);
}
```

### Task 3.3: Build Kafka Consumer (1.5 hours)
```java
// fraud-detection-service/kafka/TransactionConsumer.java
@Service
public class TransactionConsumer {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @KafkaListener(topics = "transactions", groupId = "fraud-detection-group")
    public void consumeTransaction(Transaction transaction) {
        System.out.println("Received: " + transaction.getTransactionId());
        
        // Convert to entity and save
        TransactionEntity entity = convertToEntity(transaction);
        transactionRepository.save(entity);
        
        System.out.println("Saved to database: " + transaction.getTransactionId());
    }
    
    private TransactionEntity convertToEntity(Transaction txn) {
        TransactionEntity entity = new TransactionEntity();
        entity.setTransactionId(txn.getTransactionId());
        entity.setCustomerId(txn.getCustomerId());
        entity.setMerchantId(txn.getMerchantId());
        entity.setAmount(txn.getAmount());
        entity.setTimestamp(txn.getTimestamp());
        entity.setLocation(txn.getLocation());
        entity.setMerchantCategory(txn.getMerchantCategory());
        entity.setIsInternational(txn.getIsInternational());
        return entity;
    }
}
```

### Task 3.4: Test End-to-End (1 hour)
```bash
# Terminal 1: Start producer
cd transaction-producer && mvn spring-boot:run

# Terminal 2: Start consumer
cd fraud-detection-service && mvn spring-boot:run

# Should see in consumer logs:
# "Received: <uuid>"
# "Saved to database: <uuid>"

# Verify in database
docker exec -it fraud-postgres psql -U fraud_user -d fraud_detection
SELECT COUNT(*) FROM transactions;  # Should increase every 5 seconds
SELECT * FROM transactions LIMIT 5;
```

**✅ Wednesday Checklist:**
- [ ] TransactionEntity with JPA annotations
- [ ] Consumer receiving from Kafka
- [ ] Transactions saving to PostgreSQL
- [ ] End-to-end flow working (producer → Kafka → consumer → DB)
- [ ] Git commit: "Week 1: Kafka consumer + database"

---

*[Continue with remaining days following same detailed format...]*

---

## Key Patterns for All Tasks

### Testing Pattern
```java
// Always test in this order:
1. Unit test (test single method)
2. Integration test (test service + repository)
3. End-to-end test (test full flow)
```

### Debugging Pattern
```java
// When something breaks:
1. Check logs (System.out or logger)
2. Check database (DBeaver)
3. Check Kafka (console consumer)
4. Google exact error message
5. Simplify to minimal example
```

### Git Commit Pattern
```bash
# Commit after completing each major task
git add .
git commit -m "Week X Day Y: <what you built>"
git push origin main
```

**End of Development Plan**

Total estimated hours: ~100 hours over 8 weeks
Average: 12.5 hours/week (2 hours/day on weekdays)
