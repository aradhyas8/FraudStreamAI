# RealTimeFraudDetect - Realistic Timeline

## Overview
**Total Duration:** 8 weeks (56 days)  
**Total Hours:** 95-105 hours  
**Average Weekly Commitment:** 12-13 hours  
**Daily Time (Weekdays):** 2-2.5 hours  
**Weekend Buffer:** 3-4 hours extra for catch-up

---

## Timeline Visualization

```
Week 0 (Pre-Build)     [========] 15 hrs → Learn foundations
                       ↓
Week 1 (Skeleton)      [========] 12 hrs → Transaction pipeline working
                       ↓
Week 2 (Features)      [========] 12 hrs → Feature extraction complete
                       ↓
Week 3-4 (ML)          [================] 15 hrs → ML model integrated
                       ↓
Week 5 (Decision)      [======] 10 hrs → Decision engine + API
                       ↓
Week 6 (Dashboard)     [========] 12 hrs → React UI working
                       ↓
Week 7 (Performance)   [=====] 8 hrs → Load tested
                       ↓
Week 8 (Production)    [==========] 15 hrs → Portfolio ready
```

---

## Detailed Timeline with Milestones

### Week 0: Foundation Learning (Days -5 to 0)
**Total: 15 hours | Status: PREPARATION**

| Day | Date | Tasks | Hours | Deliverable |
|-----|------|-------|-------|-------------|
| Mon | Day -5 | Spring Boot tutorial + REST API | 3h | Working CRUD API |
| Tue | Day -4 | Spring Boot + PostgreSQL | 3h | API with database |
| Wed | Day -3 | Kafka producer/consumer | 4h | Messages flowing |
| Thu | Day -2 | Redis caching | 2h | Caching working |
| Fri | Day -1 | Review + catch-up | 3h | All 4 mini-projects done |

**Milestone 0:** ✅ Completed 4 foundational mini-projects
- Can build REST APIs
- Can integrate databases
- Can use Kafka
- Can implement caching

**Risk Mitigation:**
- If behind schedule: Skip Redis (Week 0), learn it in Week 2
- If struggling with concepts: Extend Week 0 by 2-3 days

---

### Week 1: Project Skeleton (Days 1-7)
**Total: 12 hours | Status: BUILD PHASE**

#### Daily Breakdown
| Day | Date | Focus Area | Hours | Key Tasks |
|-----|------|-----------|-------|-----------|
| Mon | Day 1 | Setup | 3h | GitHub, Maven, Docker Compose |
| Tue | Day 2 | Producer | 2.5h | Build transaction producer |
| Wed | Day 3 | Consumer | 2.5h | Build consumer + database |
| Thu | Day 4 | Testing | 2h | End-to-end testing |
| Fri | Day 5 | Documentation | 2h | Update README, write tests |

**Weekly Goal:** 1 transaction flows end-to-end (Kafka → Java → PostgreSQL)

**Milestone 1:** ✅ Working data pipeline
- Transaction producer sends messages
- Consumer receives and saves to DB
- Docker Compose running all services
- Can demonstrate full flow

**Success Criteria:**
- [ ] Producer sends 1 transaction every 5 seconds
- [ ] Consumer processes without errors
- [ ] Database contains >50 transactions
- [ ] All services start with `docker-compose up`

**Common Blockers & Solutions:**
- **Kafka connection issues:** Check ADVERTISED_LISTENERS in docker-compose
- **Database not persisting:** Verify volume mounts
- **Serialization errors:** Check Jackson configuration

**If Behind Schedule:**
- Simplify producer (hardcode 1 transaction)
- Skip testing temporarily
- Extend Week 1 by 2 days

---

### Week 2: Feature Engineering (Days 8-14)
**Total: 12 hours | Status: BUILD PHASE**

#### Daily Breakdown
| Day | Date | Focus Area | Hours | Features Implemented |
|-----|------|-----------|-------|---------------------|
| Mon | Day 8 | Framework | 3h | Feature extractor skeleton, 3 basic features |
| Tue | Day 9 | Velocity | 3h | Customer history service, velocity checks |
| Wed | Day 10 | Merchant | 2h | Merchant risk scores, caching |
| Thu | Day 11 | Geographic | 2h | Location-based features |
| Fri | Day 12 | Integration | 2h | Full feature pipeline, testing |

**Weekly Goal:** Extract 10 features for every transaction

**Milestone 2:** ✅ Feature extraction pipeline working
- 10 features calculated per transaction
- Features saved to database
- Redis caching operational
- REST endpoint to view features

**Success Criteria:**
- [ ] All 10 features extracting correctly
- [ ] Feature extraction time <50ms
- [ ] Customer history cached in Redis
- [ ] Can query features via API

**Key Features to Implement:**
1. ✅ Amount normalized (0-1 scale)
2. ✅ Time of day (0-23)
3. ✅ Merchant risk score
4. ✅ Transaction count in 24h
5. ✅ Average amount in 7 days
6. ✅ Time since last transaction
7. ✅ Is international
8. ✅ Distance from home
9. ✅ Unusual location flag
10. ✅ Velocity check (impossible travel)

**If Behind Schedule:**
- Reduce to 5 core features
- Skip Redis caching (add in Week 3)
- Use simpler geographic calculations

---

### Week 3-4: ML Integration (Days 15-28)
**Total: 15 hours | Status: BUILD PHASE**

#### Week 3 Breakdown (Data + Training)
| Day | Date | Focus Area | Hours | Tasks |
|-----|------|-----------|-------|-------|
| Mon | Day 15 | Python setup | 2h | Install libs, create data gen script |
| Tue | Day 16 | Data generation | 3h | Generate 100K transactions |
| Wed | Day 17 | Model training | 2h | Train XGBoost model |
| Thu | Day 18 | Model evaluation | 2h | Evaluate metrics, tune |
| Fri | Day 19 | Analysis | 1h | Feature importance, document |

**Week 3 Goal:** Trained ML model with 85%+ precision

#### Week 4 Breakdown (Java Integration)
| Day | Date | Focus Area | Hours | Tasks |
|-----|------|-----------|-------|-------|
| Mon | Day 22 | XGBoost4J | 2h | Add dependency, load model |
| Tue | Day 23 | Scoring service | 2h | Build FraudScoringService |
| Wed | Day 24 | Integration | 1h | Integrate into consumer pipeline |

**Week 4 Goal:** ML model scoring transactions in real-time

**Milestone 3:** ✅ ML model integrated and working
- Model trained with 85%+ precision, 90%+ recall
- Java service loads and uses model
- Every transaction gets fraud probability
- Predictions saved to database

**Success Criteria:**
- [ ] Model evaluation metrics documented
- [ ] Model file in resources/models/
- [ ] FraudScoringService returns 0.0-1.0 probability
- [ ] fraud_predictions table populated
- [ ] Can explain how model works

**ML Metrics Targets:**
- Precision: ≥85% (of flagged transactions, 85% are fraud)
- Recall: ≥90% (catch 90% of all fraud)
- F1-Score: ≥0.87
- False Positive Rate: <5%

**If Behind Schedule:**
- Use pre-trained model (Kaggle)
- Simplify to 5 features
- Skip hyperparameter tuning
- Extend by 3 days

---

### Week 5: Decision Engine (Days 29-35)
**Total: 10 hours | Status: BUILD PHASE**

#### Daily Breakdown
| Day | Date | Focus Area | Hours | Tasks |
|-----|------|-----------|-------|-------|
| Mon | Day 29 | Rule engine | 2h | Decision logic + thresholds |
| Tue | Day 30 | Database | 2h | Decisions table, save decisions |
| Wed | Day 31 | Analyst API | 3h | REST endpoints for review |
| Thu | Day 32 | Feedback loop | 2h | Analyst review storage |
| Fri | Day 33 | Testing | 1h | Test all 3 decision paths |

**Weekly Goal:** Automated decision engine with human review loop

**Milestone 4:** ✅ Decision engine and analyst workflow
- Decisions automatically made (APPROVE/FLAG/BLOCK)
- REST API for analyst to review flagged transactions
- Analyst feedback saved to database
- Full transaction → decision flow working

**Success Criteria:**
- [ ] Transactions with score >0.9 blocked
- [ ] Transactions with 0.5-0.9 flagged
- [ ] Transactions with <0.5 approved
- [ ] Analyst can review via API
- [ ] Feedback stored for retraining

**Decision Logic:**
```
if fraud_probability > 0.9:
    → BLOCKED (automatic)
if fraud_probability > 0.5:
    → FLAGGED (analyst review)
else:
    → APPROVED (automatic)
```

**API Endpoints:**
- GET /api/flagged-transactions
- GET /api/transactions/{id}/details
- POST /api/transactions/{id}/review

**If Behind Schedule:**
- Simplify to 2 decision types (approve/flag)
- Skip feedback loop temporarily
- Extend by 2 days

---

### Week 6: Dashboard (Days 36-42)
**Total: 12 hours | Status: BUILD PHASE**

#### Daily Breakdown
| Day | Date | Focus Area | Hours | Tasks |
|-----|------|-----------|-------|-------|
| Mon | Day 36 | React setup | 3h | Create app, install deps |
| Tue | Day 37 | Transaction list | 2.5h | Build list component |
| Wed | Day 38 | Details view | 2.5h | Transaction details page |
| Thu | Day 39 | Review modal | 2h | Approve/reject functionality |
| Fri | Day 40 | Polish | 2h | Stats, filtering, Docker |

**Weekly Goal:** Working React dashboard for analysts

**Milestone 5:** ✅ Analyst dashboard operational
- React app showing flagged transactions
- Analysts can approve/reject via UI
- Transaction details displayed
- Dashboard stats visible

**Success Criteria:**
- [ ] Dashboard shows all flagged transactions
- [ ] Can click transaction to see details
- [ ] Can approve/reject with buttons
- [ ] UI updates after review
- [ ] Dashboard containerized

**UI Components:**
1. DashboardStats (total flagged, avg score)
2. TransactionList (table of flagged transactions)
3. TransactionDetails (full transaction info)
4. ReviewModal (approve/reject buttons)
5. FilterBar (filter by date, amount, risk)

**If Behind Schedule:**
- Use basic HTML instead of React
- Skip filtering/sorting
- Manual API calls instead of UI
- Extend by 2 days

---

### Week 7: Performance Testing (Days 43-49)
**Total: 8 hours | Status: OPTIMIZATION**

#### Daily Breakdown
| Day | Date | Focus Area | Hours | Tasks |
|-----|------|-----------|-------|-------|
| Mon | Day 43 | Monitoring | 2h | Add metrics, logging |
| Tue | Day 44 | Optimization | 2h | Tune configs, batch writes |
| Wed | Day 45 | Load test setup | 2h | JMeter test plan |
| Thu | Day 46 | Load test run | 2h | Run tests, analyze results |

**Weekly Goal:** Prove system handles 10K TPS

**Milestone 6:** ✅ Performance validated
- Load test completed successfully
- System processes 10K+ TPS
- Average latency <100ms
- Results documented

**Success Criteria:**
- [ ] Load test runs without errors
- [ ] Processes 6M+ transactions in 10 minutes
- [ ] Average latency: <100ms
- [ ] P99 latency: <150ms
- [ ] No memory leaks

**Load Test Scenarios:**
1. Baseline: 1K TPS for 5 minutes
2. Ramp up: 1K → 10K TPS over 5 minutes
3. Sustained: 10K TPS for 10 minutes
4. Spike: 15K TPS for 1 minute

**If Performance Issues:**
- Increase Kafka partitions (3-5)
- Scale consumers (2-3 instances)
- Batch database writes (10 at a time)
- Tune HikariCP pool size
- Add indices to database

**If Behind Schedule:**
- Target 5K TPS instead
- Run shorter test (5 minutes)
- Skip optimization, document limitations

---

### Week 8: Production Ready (Days 50-56)
**Total: 15 hours | Status: POLISH**

#### Daily Breakdown
| Day | Date | Focus Area | Hours | Tasks |
|-----|------|-----------|-------|-------|
| Mon | Day 50 | Logging | 2.5h | Structured logs, correlation IDs |
| Tue | Day 51 | Error handling | 2.5h | Global exception handler, DLQ |
| Wed | Day 52 | Documentation | 3h | README, API docs, architecture |
| Thu | Day 53 | Demo prep | 3h | Architecture diagram, demo video |
| Fri | Day 54 | Interview prep | 4h | Q&A answers, practice explaining |

**Weekly Goal:** Portfolio-ready project with documentation

**Milestone 7:** ✅ Production-ready system
- Professional documentation
- Demo video recorded
- Interview answers prepared
- Code clean and commented

**Success Criteria:**
- [ ] Comprehensive README with diagram
- [ ] 5-minute demo video
- [ ] API documentation (Swagger)
- [ ] Answered 20 interview questions
- [ ] Project deployed (optional)

**Documentation Checklist:**
- [ ] Architecture diagram (draw.io)
- [ ] README with setup instructions
- [ ] API documentation
- [ ] Database schema diagram
- [ ] Performance test results
- [ ] Code comments on complex logic

**Interview Prep Checklist:**
- [ ] 2-minute elevator pitch
- [ ] 5-minute technical walkthrough
- [ ] 10-minute deep dive prepared
- [ ] Can draw architecture on whiteboard
- [ ] 3 "war stories" prepared

**If Behind Schedule:**
- Skip demo video (do live demo)
- Basic README only
- Extend by 2-3 days for documentation

---

## Weekly Checkpoint System

### End of Each Week
**Review Checklist:**
- [ ] Weekly milestone achieved?
- [ ] All success criteria met?
- [ ] Tests passing?
- [ ] Code committed to GitHub?
- [ ] Learning journal updated?

**If Behind:**
- Identify blockers
- Adjust next week's scope
- Ask for help if stuck >4 hours

---

## Risk Management & Buffer Time

### Built-in Buffer
- **Week 0:** 3 hours buffer (can extend to Day -6)
- **Weeks 1-2:** 2 hours buffer each week
- **Weeks 3-4:** 3 hours buffer (most complex)
- **Week 5-6:** 2 hours buffer each week
- **Week 7-8:** 4 hours buffer (polish time)

**Total Buffer:** 16 hours across 8 weeks

### What If You Fall Behind?

**1-2 Days Behind:**
- Use weekend buffer hours
- Simplify current week's scope
- Continue to next week

**3-5 Days Behind:**
- Extend timeline by 1 week
- Remove non-critical features:
  - Skip Redis caching
  - Reduce features to 5
  - Basic HTML instead of React
  - Target 5K TPS instead of 10K

**>5 Days Behind:**
- Prioritize core functionality:
  - Keep: Kafka pipeline, ML scoring, basic API
  - Skip: Dashboard, load testing, full documentation
- Aim for functional prototype in 6 weeks
- Polish later for interviews

---

## Alternative Timeline (Accelerated)

**If You Have More Time (15-20 hrs/week):**

| Week | Focus | Total Hours |
|------|-------|-------------|
| Week 0 | Foundations | 10h (compressed to 3 days) |
| Week 1 | Skeleton | 8h (in 3 days) |
| Week 2 | Features + ML start | 15h |
| Week 3 | ML completion + Decision | 15h |
| Week 4 | Dashboard + Performance | 15h |
| Week 5 | Production + Deploy to AWS | 20h |

**Total:** 5 weeks, 83 hours

---

## Timeline Summary

### Critical Path (Must Complete)
1. ✅ Week 0: Learn basics
2. ✅ Week 1: Data pipeline working
3. ✅ Week 2: Feature extraction
4. ✅ Week 3-4: ML integration
5. ✅ Week 5: Decision engine + API

**Minimum Viable Project:** End of Week 5 (52 hours)

### Enhancement Path (Recommended)
6. ✅ Week 6: Dashboard
7. ✅ Week 7: Performance testing
8. ✅ Week 8: Production polish

**Portfolio-Ready Project:** End of Week 8 (95-105 hours)

---

## Progress Tracking

### Weekly Self-Assessment Questions

**End of Each Week:**
1. Did I achieve the weekly milestone?
2. What was the biggest challenge?
3. What did I learn that surprised me?
4. Am I on track for interview readiness?
5. What should I adjust for next week?

### Monthly Review (End of Week 4 & 8)

**After Week 4:**
- [ ] Can I explain the system architecture?
- [ ] Can I demo the fraud detection flow?
- [ ] Am I confident in my Java/Spring Boot skills?
- [ ] Do I understand how the ML model works?

**After Week 8:**
- [ ] Can I give a 5-minute project walkthrough?
- [ ] Can I answer "why this tech stack?"
- [ ] Do I have metrics to prove performance?
- [ ] Am I interview-ready?

---

## Final Timeline Commitment

**Realistic Estimate:** 8 weeks, 95-105 hours  
**Best Case:** 6 weeks, 80 hours (if no blockers)  
**Worst Case:** 10 weeks, 120 hours (with major blockers)

**Success Definition:**
- Working fraud detection system
- ML model integrated
- Can demo end-to-end
- Can explain technical decisions
- Interview-ready stories prepared

**Start Date:** _________  
**Target Completion:** _________ (8 weeks from start)  
**First Interview Target:** _________ (Week 9)
