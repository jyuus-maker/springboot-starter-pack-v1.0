# 🛍️ Spring Boot E-Commerce Starter Pack v1.0

[![Java](https://img.shields.io/badge/Java-21-orange.svg?style=flat-square&logo=openjdk)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.8-brightgreen.svg?style=flat-square&logo=springboot)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/Database-PostgreSQL-blue.svg?style=flat-square&logo=postgresql)](https://www.postgresql.org/)
[![Spring Security](https://img.shields.io/badge/Security-Spring%20Security%206-green.svg?style=flat-square&logo=springsecurity)](https://spring.io/projects/spring-security)
[![Tailwind CSS](https://img.shields.io/badge/Frontend-Tailwind%20CSS%20v4-38bdf8.svg?style=flat-square&logo=tailwindcss)](https://tailwindcss.com/)
[![QueryDSL](https://img.shields.io/badge/ORM-QueryDSL%205.1-informational.svg?style=flat-square)](http://querydsl.com/)

> **Spring Boot 기반의 엔터프라이즈급 이커머스(E-Commerce) 풀스택 스타터 팩**  
> 모던하고 안정적인 백엔드 아키텍처(Spring Data JPA + QueryDSL + Spring Security)와 감각적인 하이엔드 반응형 프론트엔드(Thymeleaf + Tailwind CSS + 2027 패럴랙스 인터랙션)가 결합된 실무형 프로젝트 템플릿입니다.

---

## 📢 [안내] 가상 브랜딩(Virtual Mock Branding) 관련 공지
> [!NOTE]
> 본 스타터 팩 프로젝트 및 동봉된 매뉴얼/브랜드북에 등장하는 **'르미아(REHMIA)' 쇼핑몰**은 본 시스템의 이커머스 비즈니스 로직과 세련된 UI/UX 디자인을 실증하기 위해 구축된 **가상의 콘셉트 브랜딩(Virtual Mock Brand)** 입니다.  
> 
> 사용자는 자유롭게 비즈니스 요구사항에 맞추어 프로젝트 명칭, 도메인, 로고, 상품 카테고리 및 디자인 테마를 커스터마이징하여 새로운 커머스 서비스로 확장하실 수 있습니다.

---

## 🧭 2-Track 듀얼 코어 기획 구조

본 프로젝트는 커머스 트랜잭션 엔진과 하이엔드 브랜드 스토리텔링 뷰가 결합된 2-Track 구조로 설계되었습니다:

1. **[Track A] E-Commerce Core Engine (`/`, `/cart`, `/orders`, `/admin/**`)**:
   - Spring Security 6 RBAC 기반 폼/비동기 인증 및 CSRF 방어
   - QueryDSL 5.1 기반 다중 동적 필터링 및 페이징 검색
   - 주문-재고 실시간 자동 차감/복구 원자적 트랜잭션
   - 5슬롯 다중 이미지 자동 리사이징 & 로컬 미디어 파이프라인
2. **[Track B] 2027 Interactive Brand Experience (`/story`, `/brand/story`)**:
   - Obsidian Dark & Sand White 럭셔리 미니멀리즘 디자인
   - Scroll-Driven Dynamic 자간 확장 애니메이션
   - Centered Scale-on-Scroll 뷰포트 고정 줌인 캔버스
   - 마우스 커서 실시간 추적 마그네틱 앰비언트 글로우 & 3D 원단 틸트 카드
   - 실시간 스크롤 프로그레스 게이지 및 5단계 퀵 점프 내비게이터

---

## 🛠️ 기술 스택 (Tech Stack)

| 구분 | 기술 / 라이브러리 | 버전 |
| :--- | :--- | :--- |
| **Language** | Java | OpenJDK 21 |
| **Framework** | Spring Boot | 4.0.8 |
| **Security** | Spring Security, Thymeleaf Extras Security 6 | 6.x |
| **Database** | PostgreSQL (운영) / H2 In-Memory (테스트 격리) | Latest |
| **Query Engine**| QueryDSL (Jakarta EE) | 5.1.0 |
| **Template Engine** | Thymeleaf, Thymeleaf Layout Dialect | 3.x |
| **Frontend Style** | Tailwind CSS (CLI) | 4.3.3 |
| **Utility** | Lombok, ModelMapper | 3.1.1 |
| **Build Tool** | Gradle | Wrapper 지원 |

---

## 📁 디렉토리 구조 (Directory Structure)

```text
springboot-starter-pack-v1.0/
├── .github/                      # CI/CD 및 저장소 설정
├── gradle/                       # Gradle Wrapper 설정
├── src/
│   ├── main/
│   │   ├── java/com/example/shop01/
│   │   │   ├── config/           # SecurityConfig, CustomAuthEntryPoint, InitData 등
│   │   │   ├── constant/         # Enum (ItemSellStatus, OrderStatus, Role 등)
│   │   │   ├── controller/       # Web & REST Controller (Main, Member, Item, Cart, Order)
│   │   │   ├── domain/           # JPA Entity (BaseEntity, Member, Item, Cart, Order 등)
│   │   │   ├── dto/              # Request / Response DTO
│   │   │   ├── repository/       # Spring Data JPA & QueryDSL Custom Repository
│   │   │   ├── service/          # 비즈니스 로직 및 트랜잭션 서비스
│   │   │   └── Shop01Application.java
│   │   └── resources/
│   │       ├── static/           # 컴파일된 CSS (output.css), JS, 아이콘/이미지
│   │       ├── templates/
│   │       │   ├── brand/        # 🌟 story.html (브랜드 스토리 패럴랙스 뷰)
│   │       │   ├── fragment/     # header.html, footer.html 등 공통 컴포넌트
│   │       │   ├── item/         # 상품 등록/수정/상세/관리 템플릿
│   │       │   ├── cart/         # 장바구니 템플릿
│   │       │   ├── order/        # 주문 목록 템플릿
│   │       │   └── member/       # 로그인/회원가입/마이페이지 템플릿
│   │       └── application.properties # 데이터베이스 및 애플리케이션 환경설정
│   └── test/                     # H2 In-Memory 격리 단위/통합 테스트 (100% Pass)
├── build.gradle                  # Gradle 의존성 및 빌드 스크립트
├── settings.gradle               # 프로젝트 루트 네임 설정
├── package.json                  # Tailwind CSS 컴파일 빌드 스크립트
├── REHMIA_Manual.html            # 📖 [마스터 매뉴얼] 기획 구조, 풀스택 아키텍처 & 운영 가이드 (HTML)
├── REHMIA_Shop_Operation_Manual.md # 📖 [마스터 매뉴얼] 아키텍처 및 2027 UX 스펙 명세 (MD)
├── REHMIA_Shop_Operation_Manual.pdf # 📕 [마스터 매뉴얼] 인쇄/배포용 PDF 문서
├── REHMIA_Visual_Brandbook.html  # 🎨 [비주얼 브랜드북] 디자인 토큰 및 톤앤매너 명세 (HTML)
├── REHMIA_Visual_Brandbook.pdf   # 🎨 [비주얼 브랜드북] 인쇄/배포용 PDF 문서
└── README.md
```

---

## 🚀 빠른 시작 (Getting Started)

### 1. 사전 요구사항 (Prerequisites)
- **Java 21** 설치 (시스템 환경변수 `JAVA_HOME` 설정 권장)
- **PostgreSQL** 데이터베이스 실행 중 (`shopdb` 데이터베이스 생성)
- **Node.js** (Tailwind CSS 스타일 수정 및 빌드 시 필요)

> 💡 **JAVA_HOME 설정 가이드 (터미널 `./gradlew` 실행 시 필요):**
> - Windows PowerShell: `$env:JAVA_HOME = "C:\Users\<사용자명>\.jdks\temurin-21.0.12.1"`
> - 시스템 환경변수 `JAVA_HOME`에 JDK 21 설치 경로를 등록해두시면 터미널 어디서나 바로 빌드 가능합니다.

### 2. 데이터베이스 및 업로드 설정 (`application.properties`)
[src/main/resources/application.properties](file:///D:/260630ai/new/springboot-starter-pack-v1.0/src/main/resources/application.properties) 파일에서 본인의 환경에 맞게 확인합니다.

```properties
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.url=jdbc:postgresql://localhost:5432/shopdb
spring.datasource.username=postgres
spring.datasource.password=1234

# 이미지 업로드 로컬 디렉토리 경로 (디렉토리가 없으면 자동 생성 또는 사전 준비)
itemImgLocation=C:/shopmall/item
uploadPath=file:///C:/shopmall/
```

### 3. 프로젝트 실행 방법

#### IDE(IntelliJ IDEA) 환경 (가장 권장)
1. 저장소 폴더를 IntelliJ에서 **Open**합니다.
2. 우측 `Gradle` 탭에서 **Reload All Gradle Projects 🔄** 클릭
3. `Shop01Application.java` 실행 (Run)

#### 터미널(CLI) 환경
```bash
# 1. (선택 사항) Tailwind CSS 컴파일 빌드
npm run build

# 2. Gradle 빌드 및 서버 구동
./gradlew bootRun
```

### 4. 테스트 실행 (100% Pass 격리 환경)
테스트 실행 시 H2 In-Memory DB(`src/test/resources/application.properties`)가 자동으로 적용되어 실 운영 DB에 영향을 주지 않고 안전하게 전체 테스트를 검증합니다.
```bash
./gradlew test
```

---

## 👥 시스템 기본 제공 테스트 계정 (InitData)

애플리케이션 구동 시 `InitData`에 의해 아래 기본 계정과 8종의 대표 상품이 자동 생성됩니다:

| 구분 | 아이디 (이메일) | 비밀번호 | 권한 | 주요 접근 영역 |
| :--- | :--- | :---: | :---: | :--- |
| **시스템 관리자** | `admin@shop01.com` | `1234` | `ROLE_ADMIN` | 쇼핑몰 전체 + 어드민 센터 (`/admin/**`) |
| **일반 고객** | `user@shop01.com` | `1234` | `ROLE_USER` | 메인 탐색, 장바구니, 주문 결제, 마이페이지 |

---

## 🌐 주요 접근 경로 (Routing Table)

| 화면명 | URL 경로 | 접근 권한 | 주요 설명 |
| :--- | :--- | :---: | :--- |
| **메인 홈** | `http://localhost:8080/` | 전체 공개 | 메인 비주얼 배너 및 실시간 상품 목록 |
| **브랜드 스토리** | `/story`, `/brand/story` | 전체 공개 | 르미아 브랜드 철학, 패럴랙스 스크롤링, 4대 가치 |
| **회원가입** | `/members/new` | 전체 공개 | 일반 회원 신규 등록 |
| **로그인** | `/members/login` | 전체 공개 | 일반 회원 및 관리자 로그인 |
| **장바구니** | `/cart` | 로그인 회원 | 담긴 상품 확인, 수량 증감, 선택 주문 |
| **주문 내역** | `/orders` | 로그인 회원 | 주문 진행 상태 조회 및 주문 취소 |
| **상품 관리** | `/admin/items` | **관리자 전용** | 등록된 상품 검색, 필터링, 수정 진입 |
| **상품 등록** | `/admin/item/new` | **관리자 전용** | 신규 상품 정보 및 최대 5장 이미지 등록 |

---

## 📚 동봉된 공식 레퍼런스 문서 안내

바이브코더와 개발자의 심층적인 아키텍처 이해와 커스터마이징을 돕기 위해 프로젝트 루트에 공식 마스터 문서가 동봉되어 있습니다:

1. **[📖 풀스택 아키텍처 & 운영 마스터 가이드 (HTML)](file:///D:/260630ai/new/springboot-starter-pack-v1.0/REHMIA_Manual.html)** / **[📕 PDF 다운로드](file:///D:/260630ai/new/springboot-starter-pack-v1.0/REHMIA_Shop_Operation_Manual.pdf)**
   - **Part 1 (아키텍처 개요):** 2-Track 듀얼 코어 구조, 계층형 아키텍처(Layered Architecture), 핵심 엔티티 ERD 관계도
   - **Part 2 (커머스 도메인):** Spring Security 6 RBAC 인증/CSRF 방어, 5슬롯 미디어 파이프라인, QueryDSL 동적 검색, 원자적 재고 트랜잭션 및 개발자 확장 포인트(PG/S3)
   - **Part 3 (2027 UX 스펙):** Obsidian & Sand 톤앤매너, 5대 하이엔드 패럴랙스 인터랙션(Scroll 자간 확장, Scale 줌인, Magnetic Glow, 3D 틸트 원단 카드, 퀵 네비게이터) 상세 메커니즘
   - **Part 4 (운영 & FAQ):** `InitData` 시드 데이터 명세, 어드민 실무 프로세스, H2 테스트 격리 및 트러블슈팅
2. **[📖 마크다운 운영 마스터 매뉴얼 (MD)](file:///D:/260630ai/new/springboot-starter-pack-v1.0/REHMIA_Shop_Operation_Manual.md)**
   - HTML/PDF 매뉴얼과 100% 싱크된 GitHub 친화형 마크다운 기술 명세서
3. **[🎨 비주얼 브랜드북 가이드 (HTML)](file:///D:/260630ai/new/springboot-starter-pack-v1.0/REHMIA_Visual_Brandbook.html)** / **[🎨 PDF 다운로드](file:///D:/260630ai/new/springboot-starter-pack-v1.0/REHMIA_Visual_Brandbook.pdf)**
   - 타이포그래피, 컬러 팔레트, 글래스모피즘 디자인 토큰 및 UI 컴포넌트 명세

---

## 📄 License & Attribution
- 본 스타터 팩 프로젝트는 오픈소스 학습 및 상업용 프로젝트의 베이스 템플릿으로 자유롭게 활용하실 수 있습니다.
