# 개발환경 수정 검증 · 2026-10-20

- Java: Corretto 21.0.12.1 / Linux x86_64
- Spring Boot: 4.0.8 / Gradle: 9.7.1
- `test bootJar`: 성공, 기존 테스트 19개 통과(삭제·비활성화 없음).
- 격리 H2 기반 테스트이며 실제 PostgreSQL 검증을 대신하지 않습니다.
- 검증 호스트의 Gradle 프록시 충돌을 제거하고, 검증 전용 init script로 Maven Central을 추가했습니다. 이 임시 스크립트와 프록시 설정은 프로젝트에 포함하지 않습니다.
- Compose YAML 파싱, 서비스 간 DB 주소, 포트, 볼륨, 헬스체크 설정 대조 완료.
- Docker 엔진이 없으므로 이미지 빌드, 실제 PostgreSQL 최초 기동, 볼륨 재사용·재시작은 미검증입니다.
- Google OAuth 외부 인증은 실제 자격증명으로 검증하지 않았습니다.

## Docker 환경에서 이어서 확인할 항목

1. `docker compose config --quiet`
2. 새 개발 볼륨으로 `docker compose up -d --build` 후 `docker compose ps`, `docker compose logs app` 확인
3. 홈·로그인·관리자·작성/업로드 등 기존 기능 확인
4. 테스트 데이터를 남기고 `docker compose down` 후 재실행하여 데이터와 이미지 보존 확인
5. 두 프로젝트를 동시에 실행하면 한쪽 DB_PORT·APP_PORT를 분리

기존 데이터 볼륨을 삭제하지 마세요. `down -v`는 의도적인 완전 초기화 때만 사용합니다.

- 추가 확인: Shop 로그인 페이지의 지연 CSRF 세션 생성 오류를 기존 CsrfCookieFilter 연결로 수정했습니다.

- 실제 JAR + 임시 H2 HTTP 확인: `/`, `/members/login` 모두 HTTP 200 및 응답 본문 끝까지 정상 수신. 실제 PostgreSQL/컨테이너 테스트는 아닙니다.
