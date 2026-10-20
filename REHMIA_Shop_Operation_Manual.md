# 📘 REHMIA 풀스택 스타터팩 v1.0 바이브코딩 & 도커 배포 마스터 가이드

> **대상 독자:** 바이브코더(Vibe Coder), 주니어/풀스택 개발자, 솔루션 아키텍트
> **아키텍처 스택:** Spring Boot 4.0.8 / Java 21 / Spring Security 7 / PostgreSQL 16 / JPA / QueryDSL 5.1 / Tailwind CSS v4
> **패키징 & 배포:** Docker Multi-stage + Nginx Alpine (80포트 리버스 프록시)
> **문서 버전:** v1.1.0 (100점 만점 기술검정 및 도커 패키징 통합본)
> **최종 수정일:** 2026-10-16

---

## 📌 목차 (Table of Contents)
1. [Part 1. [초보자 퀵스타트] 5분 만에 내 IDE에서 바로 시작하기](#part-1-초보자-퀵스타트-5분-만에-내-ide에서-바로-시작하기)
2. [Part 2. [바이브코딩 핵심 규칙] AI와 함께 코드를 고칠 때 지켜야 할 규격](#part-2-바이브코딩-핵심-규칙-ai와-함께-코드를-고칠-때-지켜야-할-규격)
3. [Part 3. [도커 & 클라우드 배포] 1줄 명령어로 완성하는 상용 패키징](#part-3-도커--클라우드-배포-1줄-명령어로-완성하는-상용-패키징)
4. [Part 4. [도메인 핵심 엔진] 이커머스 트랜잭션 & 2027 브랜드 스토리](#part-4-도메인-핵심-엔진-이커머스-트랜잭션--2027-브랜드-스토리)
5. [Part 5. [상황별 장애 조치 & 트러블슈팅 FAQ]](#part-5-상황별-장애-조치--트러블슈팅-faq)

---

## Part 1. [초보자 퀵스타트] 5분 만에 내 IDE에서 바로 시작하기

본 스타터팩을 다운로드한 직후, **"딱 3가지만 확인/수정"**하면 내 컴퓨터에서 즉시 실행할 수 있습니다.

### 1.1 사전 준비물
- **JDK 21 필수:** Eclipse Temurin 21 또는 Oracle OpenJDK 21
- **IDE 권장:** IntelliJ IDEA (Ultimate / Community) 또는 VS Code
- **PostgreSQL 16:** 로컬 PC 설치 또는 도커 컨테이너

### 1.2 "이것만 고치고 시작하세요!" (3대 필수 설정)

#### 1) DB 비밀번호 맞추기 (`src/main/resources/application.properties`)
내 컴퓨터에 설치된 PostgreSQL의 `shopdb` 데이터베이스 및 비밀번호와 일치시킵니다:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/shopdb
spring.datasource.username=postgres
spring.datasource.password=내_DB_비밀번호  # ← 여기를 내 비밀번호로 수정!
```
*(※ 아직 DB가 없다면 pgAdmin 또는 터미널에서 `CREATE DATABASE shopdb;` 1회 실행)*

#### 2) 상품 이미지 저장 폴더 1초 생성 (`./uploads/item`)
관리자가 상품을 등록할 때 업로드된 이미지가 저장되는 로컬 폴더입니다. 앱 실행 위치를 기준으로 아래 폴더에 저장되며, 첫 업로드 시 자동 생성합니다:
```text
📁 생성할 폴더 경로: ./uploads/item
```
*(※ Mac/Linux 사용자라면 `ITEM_IMG_LOCATION=/Users/내계정/shopmall/item, UPLOAD_PATH=file:///Users/내계정/shopmall/`으로 수정)*

#### 3) 기본 제공 테스트 계정 (`InitData.java`)
서버 최초 구동 시 자동으로 등록되는 테스트 계정입니다:
- **시스템 관리자:** `admin@shop01.com` / `1234` (`ROLE_ADMIN` - 상품 등록/수정/재고관리 전용)
- **일반 테스트 고객:** `user@shop01.com` / `1234` (`ROLE_USER` - 상품 둘러보기, 장바구니, 주문 결제)

### 1.3 실행하기 (Run & Verify)
1. IntelliJ에서 프로젝트 루트 폴더를 **Open**합니다.
2. `Shop01Application.java` 파일을 찾아 **▶ Run** 버튼을 클릭합니다.
3. 브라우저에서 `http://localhost:8080` 접속 시 르미아 쇼핑몰이 즉시 표시됩니다.

### 1.4 테스트 스위트 무결성 검증 (`./gradlew test`)
본 프로젝트의 테스트 환경에는 독립된 **H2 In-Memory DB (PostgreSQL 호환 모드)**가 설정되어 있어 실제 DB에 영향을 주지 않고 실제 DB와 분리하여 테스트할 수 있습니다:
```bash
# Windows
.\gradlew.bat test

# Mac / Linux
./gradlew test
```

---

## Part 2. [바이브코딩 핵심 규칙] AI와 함께 코드를 고칠 때 지켜야 할 규격

### 2.1 프론트엔드 모듈 (브라우저 Native alert/confirm 사용 금지!)
본 프로젝트는 브라우저 기본 팝업이 100% 제거되어 있으며, 모든 템플릿에 전용 UI 모듈이 내장되어 있습니다:

- **감성 알림 토스트 (`toast.js`):**
  ```javascript
  Toast.success('쇼핑백에 담았습니다.');
  Toast.error('상품 재고가 부족합니다.');
  Toast.info('로그인이 필요한 서비스입니다.');
  ```
- **비동기 컨펌 모달 (`modal.js`):**
  ```javascript
  const confirmed = await Modal.confirm('해당 상품을 삭제하시겠습니까?', {
      title: '삭제 확인',
      confirmText: '삭제하기',
      type: 'danger'
  });
  if (!confirmed) return;
  ```
- **표준 비동기 API 클라이언트 (`api.js`):**
  ```javascript
  // 버튼 요소를 넘겨주면 클릭 시 로딩 스피너 및 중복 클릭 방지가 자동 적용됩니다.
  const res = await Api.post('/order', { itemId: 1, count: 2 }, myButtonEl);
  if (res.success) {
      Toast.success(res.message);
  } else {
      Toast.error(res.message);
  }
  ```

### 2.2 백엔드 표준 API 응답 규격 (`ApiResponse<T>`)
모든 비동기 Controller 엔드포인트는 `ApiResponse<T>` 규격을 반환합니다:
```java
@PostMapping("/order")
@ResponseBody
public ResponseEntity<ApiResponse<Long>> order(@RequestBody @Valid OrderDto orderDto, ...) {
    Long orderId = orderService.order(orderDto, email);
    return ResponseEntity.ok(ApiResponse.success(orderId, "주문이 성공적으로 접수되었습니다."));
}
```

### 2.3 글로벌 예외 처리기 (`GlobalExceptionHandler.java`)
Service 계층에서 `throw new IllegalStateException("재고 부족");`을 던지면, 컨트롤러의 복잡한 try-catch 없이도 글로벌 예외 처리기가 자동으로 400 Bad Request JSON 에러를 반환합니다.

---

## Part 3. [도커 & 클라우드 배포] 1줄 명령어로 완성하는 상용 패키징

### 3.1 3단 컨테이너 구성
- **Nginx (Alpine):** 80 포트 리버스 프록시, Gzip 압축, `/images/` 볼륨 직통 30일 캐싱
- **App (Spring Boot):** Eclipse Temurin 21 JRE 경량 멀티스테이지, non-root 보안 유저, G1GC 튜닝
- **DB (PostgreSQL 16):** `pg_isready` 헬스체크 기반 백엔드 구동 순서 보장, 영속 볼륨 연동

### 3.2 배포 명령어 (1줄!)
```bash
# 1. 일괄 빌드 및 백그라운드 구동
docker compose up -d --build

# 2. 서비스 접속 (Nginx 80 포트)
http://localhost

# 3. 로그 실시간 확인
docker compose logs -f app
```

---

## Part 4. [도메인 핵심 엔진] 이커머스 트랜잭션 & 2027 브랜드 스토리

### 4.1 2-Track 듀얼 코어 구조
- **Track A (E-Commerce 코어):** Spring Security 7 RBAC, 5슬롯 이미지 파이프라인, 실시간 재고 차감/복구 트랜잭션, QueryDSL 동적 검색
- **Track B (2027 패럴랙스 브랜드 스토리 `/story`):** Obsidian Dark 모던 룩앤필, Scroll-Driven 자간 확장, Scale-on-Scroll 줌인, 마그네틱 앰비언트 글로우

### 4.2 주문-재고 트랜잭션 규칙
- **주문 접수:** 주문 수량만큼 DB 재고 즉시 차감 (부족 시 `IllegalStateException` 및 롤백)
- **주문 취소:** 취소된 수량만큼 DB 재고 원상 복구 및 상태 `CANCEL` 변경
- **장바구니 일괄 주문:** 선택 상품 중 1개라도 재고 부족 시 전체 주문 롤백 (원자성 보장)

---

## Part 5. [상황별 장애 조치 & 트러블슈팅 FAQ]

### Q1. "Connection refused" DB 연결 실패 오류
- 로컬 PostgreSQL 서비스(`postgresql-x64-16`)가 실행 중인지 확인하세요.
- 도커를 사용할 경우 `docker compose up -d db`로 DB 컨테이너를 먼저 띄울 수 있습니다.

### Q2. 업로드한 상품 이미지가 엑스박스로 깨짐
- `./uploads/item` 폴더가 내 컴퓨터에 실제로 존재하는지 확인하고, 없다면 새 폴더를 직접 생성하세요.

### Q3. 8080 포트가 이미 사용 중 (Address already in use)
- `application.properties` 맨 아래에 `server.port=8081`을 추가하여 포트를 변경하세요.

### Q4. `./gradlew` 실행 시 JAVA_HOME 에러
- PowerShell에서 임시 경로를 지정하세요:
  ```powershell
  $env:JAVA_HOME="C:\Users\<사용자명>\.jdks\temurin-21.0.12.1"
  .\gradlew.bat test
  ```

### Q5. 새 HTML 작성 시 Tailwind CSS 디자인 미반영
- 프로젝트 루트에서 `npm run build`를 실행하여 `output.css`를 재컴파일하세요.


---

## Linux · Windows · Docker 개발환경 안내 (2026-10-20)

### 버전과 실행 방식
- Java 21, Spring Boot 4.0.8, Gradle Wrapper 9.7.1을 사용합니다. 별도 Gradle 설치는 필요하지 않습니다.
- IntelliJ IDEA, Eclipse, VS Code 등 원하는 IDE에서 JDK 21과 프로젝트 루트를 지정합니다.
- WSL 2 + Docker Desktop을 사용할 때는 Docker Desktop의 해당 WSL 배포판 연동을 켭니다. Windows PostgreSQL 설치는 필수가 아닙니다.
- Dev Containers는 제공하지 않습니다. IDE에서 앱을 실행하고 DB만 Docker로 띄우거나, 전체 Compose를 실행할 수 있습니다.

### A. 전체 Docker 실행
```bash
# .env.example을 .env로 복사하고 값을 확인합니다. PowerShell에서는 Copy-Item 사용
cp .env.example .env
docker compose up -d --build
docker compose logs -f app
```
- 접속: `http://localhost` (APP_PORT를 바꿨다면 해당 포트).
- 앱 → DB: `db:5432/shopdb`. 컨테이너 안에서 DB를 localhost로 지정하지 않습니다.
- DB 준비는 pg_isready로 확인하며, 앱 시작 전 DB가 준비될 때까지 기다립니다.

### B. IDE/WSL에서 앱 실행 + Docker DB
```bash
docker compose up -d db
# 아래 변수는 IDE의 Run Configuration에도 동일하게 지정할 수 있습니다.
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/shopdb
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD='My123456789!'
./gradlew bootRun
```
- 이미지 기본 저장 위치는 실행 작업 디렉터리 기준 `./uploads/item`입니다. Windows/Mac/Linux에서 동일합니다.
- 커스텀 경로는 `ITEM_IMG_LOCATION`과 `UPLOAD_PATH`를 함께 지정합니다. 예: `/home/me/shop/item`, `file:///home/me/shop/`.
- 기존 `C:/shopmall/item` 이미지를 계속 쓰려면 기존 경로를 환경변수로 지정하거나 새 폴더로 복사하세요. 자동 이동하지 않습니다.

- 앱 직접 실행 주소는 `http://localhost:8080`입니다.
- PowerShell 환경변수 예: `$env:SPRING_DATASOURCE_PASSWORD='My123456789!'`, 실행은 `.\gradlew.bat bootRun`.
- `.env`는 Compose 변수 치환용입니다. IDE/Gradle이 자동으로 읽는 파일이 아니므로 직접 실행 시 IDE나 셸에 환경변수를 설정합니다.
- DB_PORT를 바꾸면 직접 실행 JDBC URL의 포트도 맞춥니다. Compose 내부 DB 포트는 계속 5432입니다.

### 환경변수와 데이터 보존
| 변수 | 용도 |
|---|---|
| POSTGRES_DB / POSTGRES_USER / POSTGRES_PASSWORD | PostgreSQL 컨테이너의 초기 DB·계정 설정 |
| DB_PORT | 호스트에 공개하는 DB 포트, 기본 5432 |
| APP_PORT | Nginx 외부 포트, 기본 80 |
| SPRING_DATASOURCE_URL / USERNAME / PASSWORD | 직접 실행 시 SPRING_DATASOURCE_* 사용; Compose는 POSTGRES_*에서 주입 |

DB와 업로드 파일은 named volume에 보존합니다. `docker compose down`은 컨테이너·네트워크를 정리하고 볼륨은 보존합니다. `docker compose stop` / `start`는 기존 컨테이너를 중지·재시작합니다. **`docker compose down -v`는 데이터까지 지우므로 초기화가 필요할 때만 사용하세요.**

이미 데이터가 있는 볼륨에서는 POSTGRES_PASSWORD 변경만으로 기존 DB 암호가 바뀌지 않습니다. 기존 계정 암호와 접속 설정을 함께 관리하세요. Windows DB는 Docker DB로 자동 이전되지 않으므로 보존할 데이터는 Windows PostgreSQL 삭제 전에 pg_dump로 백업하고 Docker DB에 복원합니다.

두 스타터팩을 동시에 띄우면 5432·80 포트가 겹칩니다. 한쪽의 DB_PORT·APP_PORT를 변경하세요. 별도 개발 컨테이너를 사용한다면 DB와 같은 네트워크에 연결하고 DB 서비스명으로 접속합니다.

### 빌드·테스트
```bash
# Linux / WSL / macOS
./gradlew test bootJar
# 실행 권한을 보존하지 않는 ZIP이나 파일시스템에서는 sh ./gradlew test bootJar
# Windows PowerShell
.\gradlew.bat test bootJar
```
테스트는 test resources의 H2 설정을 사용합니다. H2 통과는 실제 PostgreSQL·Docker·Google OAuth 연동 검증을 대신하지 않습니다. Dockerfile의 bootJar는 테스트를 생략하므로 위 테스트를 별도로 수행합니다.
