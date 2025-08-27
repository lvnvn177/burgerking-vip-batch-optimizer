```markdown
# 버거킹 VIP 멤버십 배치 성능 최적화 프로젝트 (burgerking-vip-batch-optimizer)

## 프로젝트 소개

본 프로젝트는 대규모 사용자를 가진 실제 비즈니스 환경(가상: 버거킹 멤버십 시스템)에서 발생하는 **"대량 배치 작업으로 인한 서비스 성능 저하 문제"**를 진단하고, **Java, Spring Boot, Spring Batch, JPA** 기술 스택을 활용하여 **최적화된 해결 방안을 설계 및 구현하며, 정량적인 성능 개선을 입증**하는 것을 목표로 합니다.

특히, **매월 초 정기적으로 진행되는 VIP 멤버십 등급(하락/유지/상승) 갱신 배치 작업**이 실시간 주문/조회 API 서비스에 미치는 부정적인 영향을 분석하고, **기존 인프라 자원의 증설 없이 소프트웨어적 최적화**를 통해 문제를 해결하는 과정에 중점을 두었습니다.

## 주요 기능 및 목표

*   **문제 정의 및 재현**: 수백만 건의 주문 데이터를 가진 환경에서 VIP 멤버십 등급 갱신 배치 작업 시 발생하는 CPU, 메모리, DB 락, API 응답 시간 지연 현상 진단.
*   **비최적화 배치 구현**: 문제 상황을 명확히 보여줄 수 있는 비효율적인 배치 로직 구현.
*   **성능 최적화 설계 및 구현**:
    *   **DB 레벨**: 인덱스 최적화, 쿼리 튜닝, 격리 레벨 고려.
    *   **애플리케이션 레벨**: Spring Batch의 Chunk 기반 처리, 병렬 처리, 벌크(Bulk) 연산, 비동기 처리.
    *   **JPA 활용**: N+1 문제 해결, 영속성 컨텍스트 최적화, 벌크성 업데이트.
*   **부하 테스트 및 성능 검증**:
    *   대규모 더미 데이터 생성.
    *   비최적화/최적화 버전 각각에 대해 부하 테스트 진행.
    *   **CPU 사용량, 메모리 사용량, API 응답 시간, 배치 실행 시간** 등 핵심 지표를 Before/After **정량적 데이터와 시각화된 그래프**로 비교하여 개선 효과 명확히 입증.
*   **문제 해결 및 회고**: 기술적인 깊이를 기반으로 문제 해결 과정을 문서화하고, 본 프로젝트를 통해 얻은 인사이트 및 향후 발전 방향 제시.

## 기술 스택

*   **Language**: Java 11 (or 17)
*   **Framework**: Spring Boot 2.x, Spring Batch, Spring Data JPA
*   **Database**: MySQL (Embedded H2 for development/testing)
*   **ORM**: Hibernate
*   **Build Tool**: Maven
*   **Dependencies**: Lombok, Validation
*   **Testing**: Spring Boot Test, Spring Batch Test
*   **Performance Monitoring/Testing Tools**: JMeter / Locust, htop / VisualVM / Spring Boot Actuator

## 프로젝트 구조

```
burgerking-vip-batch-optimizer/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── burgerking/
│   │   │           └── membership/
│   │   │               └── vipbatchoptimizer/
│   │   │                   ├── config/          # Spring Batch Job 및 Step 설정
│   │   │                   ├── domain/          # JPA Entity (Member, Order)
│   │   │                   ├── repository/      # Spring Data JPA Repository
│   │   │                   ├── service/         # 비즈니스 로직 및 비최적화 배치 서비스
│   │   │                   └── web/             # API Controller (부하 테스트 대상)
│   │   └── resources/
│   │       └── application.properties           # DB 및 Batch 설정
│   └── test/                # 테스트 코드
├── pom.xml                  # Maven 의존성 및 프로젝트 설정
└── README.md
```

## 개발 환경 설정 및 실행 방법

### 1. 전제 조건

*   Java Development Kit (JDK) 11 또는 17 설치
*   MySQL 데이터베이스 설치 및 실행
*   Maven 설치 (혹은 IDE에 내장된 Maven 사용)

### 2. 프로젝트 클론 및 설정

```bash
git clone https://github.com/YourGitHubID/burgerking-vip-batch-optimizer.git
cd burgerking-vip-batch-optimizer
```

### 3. 데이터베이스 설정

`application.properties` 파일을 열어 본인의 MySQL 환경에 맞게 데이터베이스 연결 정보를 수정합니다.

```properties
# src/main/resources/application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/burgerking_vip?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password
# 데이터베이스 'burgerking_vip'를 미리 생성해주세요.
```

### 4. 프로젝트 빌드 및 실행

```bash
# Maven을 이용하여 빌드
mvn clean install

# Spring Boot 애플리케이션 실행
mvn spring-boot:run
```

애플리케이션이 실행되면, Spring Batch 관련 테이블이 자동으로 생성됩니다 (단, `spring.batch.jdbc.initialize-schema=always` 설정 시).

### 5. 대규모 더미 데이터 생성

애플리케이션 내에 구현된 더미 데이터 생성 기능을 활용하여 수백만 건의 `Order` 및 `Member` 데이터를 생성합니다. (일반적으로 `CommandLineRunner`를 통해 최초 1회 실행하거나 별도 컨트롤러를 통해 트리거).

### 6. 배치 Job 실행

Spring Batch Job은 주로 수동으로 실행하거나 스케줄러(ex: Quartz, OS 스케줄러)를 통해 외부에서 트리거합니다.

*   `http://localhost:8080/batch/run-non-optimized` (비최적화 배치)
*   `http://localhost:8080/batch/run-optimized` (최적화 배치)

(예시 URL이며, 실제 구현에 따라 달라질 수 있습니다.)

## 📊 성능 분석 및 결과

프로젝트에서 구현된 비최적화 배치와 최적화 배치를 각각 실행하며, API 부하 테스트 도구(JMeter/Locust)와 시스템 모니터링 도구(htop/VisualVM)를 사용하여 다음 지표들을 측정합니다.

*   **배치 실행 시간**: (Before) N분 N초 -> (After) M초
*   **배치 실행 중 API 평균 응답 시간**: (Before) N초 -> (After) 0.x초
*   **CPU 사용률**: (Before) Peak N% -> (After) Peak M%
*   **메모리 사용량**: (Before) N GB -> (After) M GB

측정된 데이터를 기반으로 시각화된 그래프와 함께 정량적인 개선 효과를 `(프로젝트 서류의) 문제 해결 과정` 섹션에 명시합니다.

## 회고 및 학습 경험

본 프로젝트를 통해 대규모 데이터 환경에서 발생하는 백엔드 성능 문제를 진단하고, Java, Spring Boot, Spring Batch, JPA를 활용한 실질적인 최적화 기법을 적용하며 **"성능 개선의 정량적 입증"** 과정 전반을 경험했습니다. 특히 **"기존 인프라 자원 제약 내에서의 소프트웨어적 최적화"**의 중요성을 이해하고, 문제 해결을 위한 다양한 접근법과 그 효과를 체득할 수 있었습니다. 이는 향후 어떠한 백엔드 시스템에서도 발생할 수 있는 성능 병목 현상을 효과적으로 해결하는 데 큰 자산이 될 것입니다.

---
