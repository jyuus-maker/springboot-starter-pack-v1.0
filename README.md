# 🛍️ Spring Boot E-Commerce Starter Pack v1.0

[![Java](https://img.shields.io/badge/Java-21-orange.svg?style=flat-square&logo=openjdk)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.8-brightgreen.svg?style=flat-square&logo=springboot)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/Database-PostgreSQL-blue.svg?style=flat-square&logo=postgresql)](https://www.postgresql.org/)
[![Spring Security](https://img.shields.io/badge/Security-Spring%20Security%206-green.svg?style=flat-square&logo=springsecurity)](https://spring.io/projects/spring-security)
[![Tailwind CSS](https://img.shields.io/badge/Frontend-Tailwind%20CSS%20v4-38bdf8.svg?style=flat-square&logo=tailwindcss)](https://tailwindcss.com/)
[![QueryDSL](https://img.shields.io/badge/ORM-QueryDSL%205.1-informational.svg?style=flat-square)](http://querydsl.com/)

> **Spring Boot 기반의 엔터프라이즈급 이커머스(E-Commerce) 풀스택 스타터 팩**  
> 모던하고 안정적인 백엔드 아키텍처(Spring Data JPA + QueryDSL + Spring Security)와 세련된 반응형 프론트엔드(Thymeleaf + Tailwind CSS)가 결합된 실무형 프로젝트 템플릿입니다.

---

## 📢 [안내] 가상 브랜딩(Virtual Mock Branding) 관련 공지
> [!NOTE]
> 본 스타터 팩 프로젝트 및 동봉된 매뉴얼/브랜드북에 등장하는 **'르미아(REHMIA)' 쇼핑몰**은 본 시스템의 이커머스 비즈니스 로직과 세련된 UI/UX 디자인을 실증하기 위해 구축된 **가상의 콘셉트 브랜딩(Virtual Mock Brand)** 입니다.  
> 
> 사용자는 자유롭게 비즈니스 요구사항에 맞추어 프로젝트 명칭, 도메인, 로고, 상품 카테고리 및 디자인 테마를 커스터마이징하여 새로운 커머스 서비스로 확장하실 수 있습니다.

---

## ✨ 핵심 기능 (Key Features)

### 1. 🔐 인증 & 인가 (Spring Security 6)
- **역할 기반 접근 제어 (RBAC):** 일반 고객(`ROLE_USER`)과 시스템 관리자(`ROLE_ADMIN`)의 명확한 권한 분리
- **보안 표준 준수:** 비밀번호 `BCrypt` 단방향 해시 암호화 및 비인가 사용자의 관리자 페이지 접근 자동 차단
- **CSRF 방어:** 장바구니 추가, 수량 변경, 주문 결제 등 비동기(AJAX) 및 폼 요청 전반에 CSRF 토큰 검증 적용

### 2. 📦 상품 관리 & 미디어 파이프라인 (Admin Center)
- **다중 이미지 업로드:** 최대 5장의 이미지 등록 (1번 슬롯: 메인 대표 썸네일 자동 지정, 2~5번: 디테일 컷)
- **동적 상태 제어:** `판매중(SELL)` / `품절(SOLD_OUT)` 상태 전환 및 실시간 고객 결제 제어
- **QueryDSL 동적 필터링:** 상품명 검색, 등록 기간별 필터링, 판매 상태별 페이징 조회

### 3. 💳 주문 & 자동 재고 동기화 (Order Engine)
- **실시간 재고 차감/복구 트랜잭션:**
  - 주문 접수 시 주문 수량만큼 DB 재고 즉시 차감 (재고 부족 시 예외 발생 및 트랜잭션 롤백)
  - 주문 취소 요청 시 취소 수량만큼 DB 재고 원상 복구 및 상태 변경
- **단일 / 다중 주문 지원:** 상품 상세 페이지에서의 '즉시 구매' 및 장바구니 내 '선택 상품 일괄 주문' 지원

### 4. 🛒 쇼핑백 (Cart System)
- 사용자별 독립 장바구니 생성 및 실시간 수량 증감/삭제
- 비동기(RESTful JSON) 기반 실시간 금액 합산 계산

### 5. 🎨 모던 반응형 UI (Tailwind CSS v4 + Thymeleaf)
- 모바일-태블릿-데스크톱 완벽 대응 반응형 그리드 시스템
- 직관적인 모달, 알림 토스트, 드롭다운 네비게이션 및 세련된 미니멀리즘 디자인

---

## 🛠️ 기술 스택 (Tech Stack)

| 구분 | 기술 / 라이브러리 | 버전 |
| :--- | :--- | :--- |
| **Language** | Java | OpenJDK 21 |
| **Framework** | Spring Boot | 4.0.8 |
| **Security** | Spring Security, Thymeleaf Extras Security 6 | 6.x |
| **Database** | PostgreSQL, Spring Data JPA, Hibernate ORM | Latest |
| **Query Engine**| QueryDSL (Jakarta EE) | 5.1.0 |
| **Template Engine** | Thymeleaf, Thymeleaf Layout Dialect | 3.x |
| **Frontend Style** | Tailwind CSS (CLI) | 4.3.3 |
| **Utility** | Lombok, ModelMapper | 3.1.1 |
| **Build Tool** | Gradle | Wrapper 지원 |

---

## 📁 디렉터리 구조 (Directory Structure)

```text
springboot-starter-pack-v1.0/
├── .github/                      # CI/CD 및 저장소 설정
├── gradle/                       # Gradle Wrapper 설정
├── src/
│   ├── main/
│   │   ├── java/com/example/shop01/
│   │   │   ├── constant/         # Enum (ItemSellStatus, OrderStatus, Role 등)
│   │   │   ├── controller/       # Web & REST Controller
│   │   │   ├── domain/           # JPA Entity (BaseEntity, Member, Item, Cart, Order 등)
│   │   │   ├── dto/              # Request / Response DTO
│   │   │   ├── repository/       # Spring Data JPA & QueryDSL Custom Repository
│   │   │   ├── service/          # 비즈니스 로직 및 트랜잭션 서비스
│   │   │   └── Shop01Application.java
│   │   └── resources/
│   │       ├── static/           # 컴파일된 CSS (output.css), JS, 아이콘/이미지
│   │       ├── templates/        # Thymeleaf HTML 뷰 템플릿
│   │       └── application.properties # 데이터베이스 및 애플리케이션 환경설정
│   └── test/                     # 단위 및 통합 테스트 코드
├── build.gradle                  # Gradle 의존성 및 빌드 스크립트
├── settings.gradle               # 프로젝트 루트 네임 설정
├── package.json                  # Tailwind CSS 컴파일 빌드 스크립트
├── REHMIA_Manual.html            # 📖 웹 기반 상세 운영 매뉴얼
├── REHMIA_Shop_Operation_Manual.md # 📖 마크다운 운영 마스터 매뉴얼
├── REHMIA_Visual_Brandbook.html  # 🎨 비주얼 브랜드 가이드북
└── README.md
```

---

## 🚀 빠른 시작 (Getting Started)

### 1. 사전 요구사항 (Prerequisites)
- **Java 21** 이상 설치
- **PostgreSQL** 데이터베이스 실행 중 (`shopdb` 데이터베이스 생성)
- **Node.js** (CSS 스타일 수정 및 빌드 시 필요)

### 2. 데이터베이스 설정 (`application.properties`)
[src/main/resources/application.properties](file:///D:/260630ai/new/springboot-starter-pack-v1.0/src/main/resources/application.properties) 파일에서 본인의 DB 계정 정보를 확인/수정합니다.

```properties
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.url=jdbc:postgresql://localhost:5432/shopdb
spring.datasource.username=postgres
spring.datasource.password=your_password

# 이미지 업로드 로컬 디렉터리 경로 (환경에 맞게 지정)
itemImgLocation=C:/shopmall/item
uploadPath=file:///C:/shopmall/
```

### 3. 프로젝트 실행 방법

#### 터미널(CLI) 환경
```bash
# 1. Gradle 빌드 및 서버 구동
./gradlew bootRun

# (선택 사항) Tailwind CSS 실시간 감시 빌드
npm install
npm run watch:css
```

#### IDE(IntelliJ IDEA) 환경
1. 저장소 폴더를 IntelliJ에서 **Open**합니다.
2. 우측 `Gradle` 탭에서 **Reload All Gradle Projects 🔄** 클릭
3. `Shop01Application.java` 실행 (Run)

---

## 🌐 주요 접근 경로 (Routing Table)

| 화면명 | URL 경로 | 접근 권한 | 주요 설명 |
| :--- | :--- | :---: | :--- |
| **메인 홈** | `http://localhost:8080/` | 전체 공개 | 메인 비주얼 배너 및 실시간 상품 목록 |
| **회원가입** | `/members/new` | 전체 공개 | 일반 회원 신규 등록 |
| **로그인** | `/members/login` | 전체 공개 | 일반 회원 및 관리자 로그인 |
| **장바구니** | `/cart` | 로그인 회원 | 담긴 상품 확인, 수량 증감, 선택 주문 |
| **주문 내역** | `/orders` | 로그인 회원 | 주문 진행 상태 조회 및 주문 취소 |
| **상품 관리** | `/admin/items` | **관리자 전용** | 등록된 상품 검색, 필터링, 수정 진입 |
| **상품 등록** | `/admin/item/new` | **관리자 전용** | 신규 상품 정보 및 최대 5장 이미지 등록 |

---

## 📚 동봉된 공식 문서 및 레퍼런스

프로젝트 루트에 비즈니스 및 브랜드 이해를 돕기 위한 문서가 함께 포함되어 있습니다:
- [📖 쇼핑몰 운영 마스터 매뉴얼 (MD)](file:///D:/260630ai/new/springboot-starter-pack-v1.0/REHMIA_Shop_Operation_Manual.md) : 초보 관리자를 위한 운영 가이드 및 장애 조치(FAQ)
- [🌐 웹 인터랙티브 매뉴얼 (HTML)](file:///D:/260630ai/new/springboot-starter-pack-v1.0/REHMIA_Manual.html) : 브라우저에서 바로 열람 가능한 풀페이지 가이드
- [🎨 비주얼 브랜드북 (HTML)](file:///D:/260630ai/new/springboot-starter-pack-v1.0/REHMIA_Visual_Brandbook.html) : 타이포그래피, 컬러 팔레트, 디자인 토큰 명세

---

## 📄 License & Attribution
- 본 스타터 팩 프로젝트는 오픈소스 학습 및 상업용 프로젝트의 베이스 템플릿으로 자유롭게 활용하실 수 있습니다.
