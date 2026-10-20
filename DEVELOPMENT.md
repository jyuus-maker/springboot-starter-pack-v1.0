# 개발환경 및 실행 가이드

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
