# 🛒 Order Service — Goggle Edu

> **배움에는 끝이 없다.** 강의부터 멘토링까지, 당신을 위한 완벽한 자격증 통합 교육 플랫폼 **Goggle Edu**의 주문 서비스입니다.

자격증 취득을 목표로 하는 학습자가 강의와 멘토링을 하나의 플랫폼에서 결제하고 수강할 수 있도록, 안정적인 주문-결제 파이프라인을 제공합니다.

---

## 📌 서비스 개요

Order Service는 Goggle Edu MSA 아키텍처에서 **SAGA Orchestrator** 역할을 수행하며, 주문 생성·조회·취소 전반을 담당합니다.

- **Feign Client** + **Resilience4j** Circuit Breaker / Retry로 강의·멘토링·유저 서비스 동기 연동 및 장애 전파 차단
- **Kafka** 기반 결제 요청·완료·취소 이벤트 비동기 처리
- **Outbox / Inbox 패턴** + **DLQ** 구조로 이벤트 유실 방지, 스케줄러 10초마다 미발행 이벤트 재시도
- 동기 호출 보상 트랜잭션 실패 시 **Slack 알림** 발송으로 운영 가시성 확보
- **Idempotent Consumer** 적용으로 중복 이벤트 처리 방지

---

## 🏗️ 전체 인프라 아키텍처

> **GCP 환경** / VPC 내 Compute Engine 기반 운영

<img width="1500" height="700" alt="인프라 아키텍처" src="https://github.com/user-attachments/assets/6f3faf30-1e02-447f-acc2-12eb696008b4" />

---

## 🔁 주문 처리 상세 흐름

### 주문 생성 플로우

```
주문 생성 요청
  │
  ├─▶ 유저 검증 (Feign 동기) ──── 실패 ──▶ 예외 반환
  │
  ├─▶ 강의 / 멘토링 예약 (Feign + Retry + CircuitBreaker)
  │         └── 실패 ──▶ 예외 반환
  │
  ├─▶ 주문 DB 저장 (PAYMENT_PENDING)
  │         └── 실패 ──▶ 예외 반환
  │
  ├─▶ Outbox 이벤트 저장 (주문 저장과 동일 트랜잭션, PENDING 상태)
  │         └── 실패 ──▶ 보상 트랜잭션 (예약 롤백)
  │                           ├── 성공 ──▶ 복구 완료
  │                           └── 실패 ──▶ 수동 개입 필요 (Slack + OrderFailedEvent)
  │
  ├─▶ 트랜잭션 커밋
  │
  ├─▶ Kafka 이벤트 발행 (order.payment-pending.v1)
  │         └── 실패 ──▶ 스케줄러 10초마다 재시도 (3회 초과 시 DLT)
  │
  └─▶ 결제 이벤트 수신 대기 (PAYMENT_PENDING 유지)

결제 이벤트 수신 (Kafka)
  ├── 승인 ──▶ 주문 상태 PAID → COMPLETED
  │           ├─▶ 완료 이벤트 발행 (강의 / 멘토링 / 알림 서비스)
  │           └─▶ 주문 완료
  └── 실패 ──▶ 주문 상태 PAYMENT_FAILED
              └─▶ 보상 트랜잭션 (예약 롤백)
```

---

## 📡 서비스 간 통신 명세

### Feign Client (동기 호출)

#### User Service

| 메서드 | 경로 | 설명 |
|---|---|---|
| GET | `/internal/v1/user/{userId}` | 구매자 정보 조회 및 활성 상태 검증 |

#### Lecture Service

| 메서드 | 경로 | 설명 |
|---|---|---|
| POST | `/internal/v1/lectures-enrollment` | 수강 등록 예약 (RESERVE 상태 생성) |
| POST | `/internal/v1/lectures-enrollment/rollback` | 예약 롤백 (주문 실패 시 보상) |
| POST | `/internal/v1/lectures-enrollment/cancel-pending` | 결제 실패 시 예약 취소 |
| POST | `/internal/v1/lectures-enrollment/cancellation` | 주문 취소 시 수강 등록 취소 |

#### Mentoring Service

| 메서드 | 경로 | 설명 |
|---|---|---|
| POST | `/internal/v1/mentoring-booking` | 멘토링 예약 (PENDING 상태 생성, 단일 주문) |
| PATCH | `/internal/v1/mentoring-booking/{bookingId}/rollback` | 예약 롤백 (주문 실패 시 보상) |
| PATCH | `/internal/v1/mentoring-booking/{bookingId}/cancel-pending` | 결제 실패 시 예약 취소 |
| PATCH | `/internal/v1/mentoring-booking/{bookingId}/cancellation` | 주문 취소 시 예약 취소 |

---

### Kafka 토픽 명세

#### Order Service 발행 토픽

| 토픽 | 설명 | 구독 서비스 |
|---|---|---|
| `order.payment-pending.v1` | 결제 요청 | Payment Service |
| `order.payment-canceled.v1` | 결제 취소 요청 | Payment Service |
| `order.completed.v1` | 주문 완료 | Notification Service |
| `order.canceled.v1` | 주문 취소 완료 | Notification Service |
| `order.failed.v1` | 주문 실패 (보상 불가) | Slack Alert |
| `order.lecture-completed.v1` | 수강 등록 완료 처리 요청 | Lecture Service |
| `order.lecture-canceled.v1` | 수강 등록 취소 처리 요청 | Lecture Service |
| `order.mentoring-completed.v1` | 멘토링 예약 완료 처리 요청 | Mentoring Service |
| `order.mentoring-canceled.v1` | 멘토링 예약 취소 처리 요청 | Mentoring Service |

#### Order Service 구독 토픽

| 토픽 | 설명 | 발행 서비스 |
|---|---|---|
| `payment.confirmed.v1` | 결제 승인 완료 | Payment Service |
| `payment.confirm-failed.v1` | 결제 승인 실패 | Payment Service |
| `payment.canceled.v1` | 결제 취소 완료 | Payment Service |
| `payment.cancel-failed.v1` | 결제 취소 실패 | Payment Service |

---

## 🌐 제공 API

> 모든 요청은 API Gateway를 통해 전달되며, `X-User-Id` / `X-User-Role` 헤더가 필요합니다.

| 메서드 | 경로 | 설명 |
|---|---|---|
| GET | `/api/v1/orders` | 주문 목록 조회 (페이지네이션, 다중 정렬, 상태 필터) |
| GET | `/api/v1/orders/{orderId}` | 주문 상세 조회 |
| POST | `/api/v1/orders/lecture` | 강의 주문 생성 |
| POST | `/api/v1/orders/mentoring` | 멘토링 주문 생성 |
| POST | `/api/v1/orders/lecture/{orderId}/cancellation` | 강의 주문 취소 |
| POST | `/api/v1/orders/mentoring/{orderId}/cancellation` | 멘토링 주문 취소 |

---

## 🔀 OrderStatus 상태 전이

<img width="500" height="500" alt="주문 상태 전이" src="https://github.com/user-attachments/assets/3a3c3edc-0c78-49a1-aa8b-4a4abe02e84d" />

| 상태 | 설명 | 전이 가능 상태 |
|---|---|---|
| `PAYMENT_PENDING` | 결제 대기 중 | `PAID`, `PAYMENT_FAILED`, `CANCEL_REQUESTED` |
| `PAID` | 결제 승인 완료 | `COMPLETED`, `CANCEL_REQUESTED` |
| `COMPLETED` | 주문 완료 | `CANCEL_REQUESTED` |
| `PAYMENT_FAILED` | 결제 실패 (**종료 상태**) | — |
| `CANCEL_REQUESTED` | 취소 요청 처리 중 | `PAID_CANCELED` |
| `PAID_CANCELED` | 결제 취소 완료 | `CANCELED` |
| `CANCELED` | 주문 취소 완료 (**종료 상태**) | — |

---

## 🧱 DDD 패키지 구조

```
goggles/orderservice
├── presentation/                # 표현 계층
│   ├── controller/              # 주문 CRUD API
│   └── dto/                     # Request / Response DTO
├── application/                 # 유스케이스 계층
│   ├── service/                 # OrderCommandService, OrderQueryService
│   │   └── impl/
│   ├── dto/
│   │   ├── command/             # 주문 생성·취소 커맨드
│   │   ├── query/               # 주문 조회 쿼리
│   │   ├── result/              # 서비스 반환 결과
│   │   └── external/            # 외부 서비스 응답 매핑
│   ├── port/
│   │   └── out/                 # 외부 의존성 인터페이스 (DIP)
│   ├── common/                  # 공통 (UserRole 등)
│   └── exception/               # 애플리케이션 예외
├── domain/                      # 도메인 계층
│   ├── model/                   # 도메인 모델 & VO (Order, OrderItem, Payment 등)
│   ├── event/                   # 도메인 이벤트 정의
│   ├── repository/              # Repository 인터페이스 (DIP)
│   └── exception/               # 도메인 예외 & 에러 코드
└── infrastructure/              # 인프라 계층
    ├── client/                  # Feign Client + Adapter (Lecture / Mentoring / User)
    │   └── dto/
    ├── consumer/                # Kafka Consumer (결제 확인·실패·취소)
    ├── event/                   # Kafka Producer & Outbox/Inbox 처리
    │   └── exception/
    ├── repository/              # JPA / QueryDSL 구현체
    ├── config/                  # Resilience4j, Outbox, QueryDSL 설정
    └── slack/                   # 보상 트랜잭션 실패 알림
```

---

## ⚙️ Resilience4j 설정

| 인스턴스 | Circuit Breaker 정책 | Retry | TimeLimiter |
|---|---|---|---|
| `lecture-service-write` | 실패율 30% → Open, 60s 대기 | 최대 3회, 지수 백오프(×2) | 10s |
| `mentoring-service-write` | 실패율 30% → Open, 60s 대기 | 최대 3회, 지수 백오프(×2) | 10s |
| `lecture-service-rollback` | 실패율 30% → Open, 60s 대기 | 최대 2회, 지수 백오프(×2) | 5s |
| `mentoring-service-rollback` | 실패율 30% → Open, 60s 대기 | 최대 2회, 지수 백오프(×2) | 5s |
| `user-service-read` | 실패율 50% → Open, 30s 대기 | 최대 3회, 지수 백오프(×2) | 3s |

> 비즈니스 예외(`BadRequestException`, `NotFoundException` 등)는 Circuit Breaker 및 Retry 대상에서 제외됩니다.

---

## 🛠️ 기술 스택

| 분류 | 기술 |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5, Spring Cloud 2025.0.2 |
| ORM | Spring Data JPA, QueryDSL 5.1 |
| Database | PostgreSQL (Cloud SQL) |
| Messaging | Apache Kafka (3 broker) |
| Service Discovery | Spring Cloud Netflix Eureka |
| Config | Spring Cloud Config Server |
| Communication | OpenFeign, WebFlux (WebClient) |
| Resilience | Resilience4j (Circuit Breaker, Retry, TimeLimiter) |
| Observability | Prometheus, Loki, Zipkin (Micrometer Brave), Grafana, AlertManager |
| Documentation | SpringDoc OpenAPI (Swagger UI) |
| Notification | Slack API |
| Code Style | Spotless + Google Java Format |
| Build | Gradle 8+ |
| Container | Docker |
| CI/CD | GitHub Actions → Container Registry → GCP Compute Engine |
| Cache | Redis (Master + 2 Replica) |

---

## 🚀 실행 방법

### 사전 요구 사항

- Java 21
- Docker & Docker Compose
- PostgreSQL, Kafka, Redis 접근 가능 상태
- GitHub Packages 접근을 위한 `gradle.properties` 설정

```properties
# ~/.gradle/gradle.properties
GitHubPackagesUsername=<your-github-username>
GitHubPackagesPassword=<your-github-token>
```

### ⚠️ 서비스 실행 순서

서비스 의존성 순서를 반드시 지켜야 합니다.

```
1. Eureka Server 실행          (서비스 디스커버리)
2. Config Server 실행          (중앙 설정 서버)
   └── Config 설정 파일 레포   (별도 레포 관리)
3. PostgreSQL / Kafka / Redis 실행
4. Order Service 실행
```

> Config Server와 Config 설정 파일 레포는 별도 레포로 관리됩니다.

### 로컬 실행

```bash
# 환경변수 설정
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=goggles
export DB_USERNAME=goggles
export DB_PASSWORD=goggles
export EUREKA_SERVER_URL1=localhost
export EUREKA_SERVER_URL2=localhost
export ZIPKIN_ENDPOINT=http://localhost:9411/api/v2/spans

# 빌드 및 실행
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### Docker 실행

```bash
# .env 파일 작성 후
docker-compose up -d
```

`.env` 파일 예시:

```env
IMAGE=order-service
IMAGE_TAG=latest
DB_HOST=<db-host>
DB_PORT=5432
DB_NAME=goggles
DB_USERNAME=goggles
DB_PASSWORD=<password>
EUREKA_SERVER_URL1=<eureka-host-1>
EUREKA_SERVER_URL2=<eureka-host-2>
ZIPKIN_ENDPOINT=http://<zipkin-host>:9411/api/v2/spans
```

---

## 📊 모니터링

| 도구 | 용도 | 접근 |
|---|---|---|
| Prometheus | 메트릭 수집 | `GET /actuator/prometheus` |
| Loki | 로그 수집 (Logback Appender) | Grafana 연동 |
| Zipkin | 분산 트레이싱 (100% 샘플링) | `ZIPKIN_ENDPOINT` 환경변수 |
| Grafana | 메트릭·로그 대시보드 | port 3000 |
| AlertManager | 알림 규칙 관리 | port 9093 |
| Swagger UI | API 문서 | `GET /swagger-ui.html` |
| Actuator Health | 헬스 체크 | `GET /actuator/health` |

로그 파일은 `/app/logs/{service-name}.log`에 저장되며, 50MB 롤링 / 최대 30일 / 총 1GB 정책이 적용됩니다.

---

## 📋 API 문서

서비스 실행 후 아래 URL에서 Swagger UI를 확인할 수 있습니다.

```
http://localhost:9007/swagger-ui.html
```
