# 📘 REHMIA 스타터팩 v1.0 기획 구조 & 풀스택 아키텍처 마스터 가이드

> **대상 독자:** 바이브코더(Vibe Coder), 풀스택 개발자, 솔루션 아키텍트  
> **아키텍처 스택:** Spring Boot 4.0.8 / Java 21 / Spring Security 6 / PostgreSQL / JPA / QueryDSL 5.1 / Tailwind CSS v4  
> **문서 버전:** v1.0.0 (개발자 & 아키텍트 통합본)  
> **최종 수정일:** 2026-09-22  

---

## 📌 목차 (Table of Contents)
1. [Part 1. 스타터팩 기획 구조 & 백엔드 아키텍처 개요](#part-1-스타터팩-기획-구조--백엔드-아키텍처-개요)
2. [Part 2. [영역 1] E-Commerce 코어 도메인 구축 내역](#part-2-영역-1-e-commerce-코어-도메인-구축-내역)
3. [Part 3. [영역 2] 2027 브랜드 경험(/story) UX & 인터랙션 스펙](#part-3-영역-2-2027-브랜드-경험story-ux--인터랙션-스펙)
4. [Part 4. 개발자 실행, 시드 데이터 & 런칭 운영 가이드](#part-4-개발자-실행-시드-데이터--런칭-운영-가이드)

---

## Part 1. 스타터팩 기획 구조 & 백엔드 아키텍처 개요

### 1.1 2-Track 듀얼 코어 기획 구조
본 스타터팩은 **실무형 커머스 트랜잭션 엔진**과 **2027 하이엔드 인터랙티브 브랜드 경험 뷰**가 완벽히 결합된 2-Track 구조입니다.

```
┌────────────────────────────────────────────────────────────────────────┐
│                   REHMIA Full-Stack Starter Pack v1.0                   │
├───────────────────────────────────┬────────────────────────────────────┤
│  [Track A] E-Commerce Core Engine │ [Track B] 2027 Parallax Story View │
│  - Spring Security 6 RBAC 인증/인가 │ - Obsidian Dark & Sand White 무드   │
│  - QueryDSL 5.1 동적 필터/페이징  │ - Scroll-Driven 자간 가변 확장     │
│  - 원자적 재고 증감 트랜잭션 엔진 │ - Centered Scale-on-Scroll 캔버스  │
│  - 5슬롯 다중 이미지 파이프라인    │ - 마그네틱 앰비언트 커서 글로우     │
└───────────────────────────────────┴────────────────────────────────────┘
```

### 1.2 풀스택 계층형 아키텍처 (Layered Architecture)
- **프레젠테이션 계층 (Presentation):** Thymeleaf 템플릿 엔진 + Tailwind CSS v4 CLI + Vanilla JS 인터랙션
- **보안 및 컨트롤러 계층 (Security & Web):** `SecurityFilterChain`, RESTful JSON 엔드포인트 및 View 컨트롤러
- **비즈니스 계층 (Service):** `@Transactional` 기반 비즈니스 무결성 보장 (`MemberService`, `ItemService`, `CartService`, `OrderService`)
- **영속성 계층 (Persistence):** Spring Data JPA Repositories + QueryDSL Q-타입 동적 쿼리 (`ItemRepositoryCustomImpl`) + PostgreSQL / H2(Test)

### 1.3 핵심 엔티티 도메인 관계 (ERD)
- **Member (1) : (N) Orders**: 회원은 여러 건의 주문을 보유
- **Member (1) : (1) Cart**: 회원 1명당 1개의 고유 장바구니 매핑
- **Cart (1) : (N) CartItem (N) : (1) Item**: 장바구니 내 상품 및 수량 보관
- **Orders (1) : (N) OrderItem (N) : (1) Item**: 주문 시점의 가격/수량 스냅샷 및 재고 증감 연동
- **Item (1) : (N) ItemImg**: 상품당 최대 5장의 이미지 슬롯 및 대표 이미지(`repimgYn`) 플래그 관리

---

## Part 2. [영역 1] E-Commerce 코어 도메인 구축 내역

### 2.1 인증 및 인가 체계 (Spring Security 6)
- **RBAC 권한 분리:** `ROLE_USER`(일반 고객)와 `ROLE_ADMIN`(시스템 관리자) 분리
- **CSRF 방어:** Form 제출 및 모든 비동기 AJAX 요청 헤더에 `X-CSRF-TOKEN` 자동 주입
- **예외 엔트리포인트:** 비인가 접근 시 `CustomAuthenticationEntryPoint`에 의해 안전한 리다이렉트 수행

### 2.2 상품 & 미디어 파이프라인
- **5슬롯 이미지 업로드:** 1번 슬롯(대표 썸네일 `repimgYn = 'Y'`), 2~5번 슬롯(서브 디테일컷 `repimgYn = 'N'`)
- **QueryDSL 5.1 동적 필터:** 등록일자, 판매상태(`SELL`/`SOLD_OUT`), 등록자, 상품명을 1개의 통합 `BooleanBuilder`로 동적 페이징 조회

### 2.3 트랜잭션 주문-재고 동기화 엔진
- **주문 접수:** `Item.removeStock(count)` → 재고 부족 시 `OutOfStockException` 발생 및 트랜잭션 롤백
- **주문 취소:** `Order.cancelOrder()` → 취소 수량만큼 `Item.addStock(count)` 자동 호출 및 상태를 `OrderStatus.CANCEL`로 갱신

### 2.4 개발자 확장 포인트 (Extension Points)
- **PG사 카드결제 연동:** `OrderController.order()` 호출 전후 토스페이먼츠/이니시스 결제창 호출 및 승인 API 연동
- **AWS S3 연동:** `FileService.java`의 로컬 파일 I/O를 S3 SDK(`PutObjectRequest`)로 전환

---

## Part 3. [영역 2] 2027 브랜드 경험(/story) UX & 인터랙션 스펙

### 3.1 2027 톤앤매너 & 디자인 시스템
- **Obsidian Dark (`#121212`) & Sand White (`#F5F5F4`):** 하이엔드 럭셔리 애슬레틱 감성
- **Glassmorphism:** `backdrop-blur-md`와 1px 미세 테두리를 적용한 플로팅 카드

### 3.2 구현된 5대 하이엔드 UX 인터랙션 메커니즘
1. **Scroll-Driven Dynamic Typography:** 스크롤 진행도에 맞춰 헤드라인 자간(`letter-spacing`)이 `-0.05em`에서 `0.25em`으로 확장
2. **Centered Scale-on-Scroll Canvas:** 뷰포트 중앙 고정 캔버스가 스크롤에 맞춰 0.85배에서 1.05배로 자연스럽게 풀스크린 줌인
3. **Magnetic Ambient Glow:** 마우스 커서 궤적을 `requestAnimationFrame`으로 추적하는 부드러운 방사형 조명 연출
4. **Interactive Material Showcase:** 3대 기능성 원단(AEROKNIT™, VORTEX-FLEX™, TECH-SHIELD™) 3D 틸트 카드
5. **Real-Time Progress & 5-Step Quick Navigator:** 1px 상단 스크롤 게이지 바 및 5단계 챕터 즉시 점프 내비게이션

---

## Part 4. 개발자 실행, 시드 데이터 & 런칭 운영 가이드

### 4.1 시스템 기본 제공 시드 계정 (`InitData`)
| 구분 | 아이디 (이메일) | 비밀번호 | 권한 | 주요 접근 영역 |
| :--- | :--- | :---: | :---: | :--- |
| **시스템 관리자** | `admin@shop01.com` | `1234` | `ROLE_ADMIN` | 쇼핑몰 전체 + 관리자 센터 (`/admin/**`) |
| **일반 고객** | `user@shop01.com` | `1234` | `ROLE_USER` | 장바구니, 단일/선택 주문, 마이페이지 |

### 4.2 관리자 실무 프로세스
- **신규 상품 등록:** `/admin/item/new`에서 기본 정보 입력 및 대표 이미지 등록
- **품절 제어:** 상품 상태를 `품절(SOLD_OUT)`로 전환하여 결제 방지
- **주문 취소 관리:** `/orders`에서 주문 취소 처리 시 DB 재고 원상 복구

### 4.3 개발자 FAQ & 트러블슈팅
- **테스트 환경 격리:** `src/test/resources/application.properties`의 H2 In-Memory DB 설정으로 PostgreSQL 실 DB와 무관하게 100% 독립 테스트 통과
- **이미지 엑스박스 방지:** `application.properties`의 `itemImgLocation` (`C:/shopmall/item`) 폴더 생성 여부 확인
- **터미널 JAVA_HOME:** PowerShell에서 `$env:JAVA_HOME = "C:\Users\<사용자>\.jdks\temurin-21.0.12.1"` 지정

---
*© 2026-2027 REHMIA Athletic. All rights reserved. Technical Support: support@rehmia.com*
