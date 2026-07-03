# Stage 1: Build — 컴파일 및 패키징 전용

#컴파일용, stage2에서 JRE 사용
FROM eclipse-temurin:26-jdk AS builder
WORKDIR /build

# 의존성 정의 파일만 먼저 복사 (소스 코드보다 훨씬 덜 바뀜) -> 코드만 수정했을 때 라이브러리 다시 다운 및 빌드하지 않도록
COPY gradlew settings.gradle build.gradle ./
COPY gradle/ gradle/

# 의존성 다운로드
# --no-daemon: Gradle은 원래 데몬(백그라운드 프로세스)을 띄워서 다음 실행을 빠르게 하는데, 컨테이너는 빌드 끝나면 어차피 사라지니까 데몬을 켜봤자 낭비이므로 꺼둠
RUN ./gradlew dependencies --no-daemon

# ── 소스 코드를 복사 시작 (여기부터 "자주 바뀌는 것") ──

# 애플리케이션 소스 코드를 이미지 안으로 복사
COPY src/ src/

# 컴파일 + 실행 가능한 Spring Boot jar 생성
RUN ./gradlew bootJar --no-daemon

# 만들어진 jar를 (dependencies / spring-boot-loader / snapshot-dependencies / application) 레이어별 폴더로 추출
RUN java -Djarmode=tools -jar build/libs/*.jar extract --layers --destination extracted


# Stage 2: Runtime — 실제로 서비스가 돌아갈 이미지

# JRE(실행 전용, 컴파일 도구 없음)로 새로 시작
FROM eclipse-temurin:26-jre
WORKDIR /app

RUN groupadd -g 1000 appgroup && useradd -u 1000 -g appgroup -r appuser

# spring-boot-loader(거의 안 바뀜) → dependencies(가끔 바뀜) → snapshot-dependencies → application(제일 자주 바뀜)

# Spring Boot 자체를 구동시키는 부트스트랩 코드. Spring Boot 버전 올릴 때만 바뀜
COPY --from=builder --chown=appuser:appgroup /build/extracted/spring-boot-loader/ ./

# 외부 라이브러리 jar
COPY --from=builder --chown=appuser:appgroup /build/extracted/dependencies/ ./

# 아직 정식 릴리즈 안 된(SNAPSHOT) 의존성
COPY --from=builder --chown=appuser:appgroup /build/extracted/snapshot-dependencies/ ./

# application 코드
COPY --from=builder --chown=appuser:appgroup /build/extracted/application/ ./

# JVM 옵션 주입용 — 배포 시점(compose)에 주입
ENV JAVA_OPTS=""

# 이 앱이 8080 포트를 쓴다는 "문서화"(no-op)
# 이 컨테이너는 -p 사용 안함 — Nginx만 외부에 노출되고,
# Nginx는 같은 브리지 네트워크 안에서 springboot:8080으로 바로 접근 -> EXPOSE는 순수하게 문서 용도
EXPOSE 8080

USER appuser

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]
