# ==============================================================================
# [스프링부트 4.x & Java 21 경량 멀티스테이지 Dockerfile]
#
# 스테이지 1 (Builder): Eclipse Temurin JDK 21 기반 소스 컴파일 및 JAR 빌드
# 스테이지 2 (Runner): 경량 Alpine JRE 21 기반 실행 이미지 (용량 최적화 & 보안 강화)
# ==============================================================================

# ------------------------------------------------------------------------------
# 1. 빌드 스테이지 (Build Stage)
# ------------------------------------------------------------------------------
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /workspace

# Gradle 래퍼 및 빌드 스크립트 먼저 복사 (도커 레이어 캐시 활용)
COPY gradlew gradlew.bat settings.gradle build.gradle ./
COPY gradle ./gradle

# Gradle 실행 권한 부여 및 의존성 사전 캐싱
RUN chmod +x ./gradlew && ./gradlew dependencies --no-daemon || true

# 전체 소스 코드 복사 후 프로덕션 JAR 빌드
COPY src ./src
RUN ./gradlew bootJar --no-daemon -x test

# ------------------------------------------------------------------------------
# 2. 런타임 실행 스테이지 (Runtime Stage)
# ------------------------------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine AS runner
WORKDIR /app

# 보안을 위한 non-root 전용 시스템 계정(spring) 생성
RUN addgroup -S spring && adduser -S spring -G spring

# 상품 이미지 업로드 디렉터리 생성 및 권한 부여
RUN mkdir -p /data/shopmall/item && chown -R spring:spring /data/shopmall

# 빌드 스테이지에서 생성된 최종 실행 JAR 복사
COPY --from=builder /workspace/build/libs/*-SNAPSHOT.jar /app/app.jar

# 파일 소유권 non-root 사용자로 변경
RUN chown spring:spring /app/app.jar

# non-root 유저로 전환
USER spring:spring

# 내부 애플리케이션 포트 노출
EXPOSE 8080

# JVM 메모리 최적화 옵션 (컨테이너 환경 자동 인식 및 G1GC 적용)
ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=75.0 -Dfile.encoding=UTF-8"

# 컨테이너 시작 엔트리포인트
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
