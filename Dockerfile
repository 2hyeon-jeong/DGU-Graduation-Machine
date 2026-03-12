FROM eclipse-temurin:21-jdk-alpine

# 컨테이너 내부에서 작업할 디렉토리를 설정
WORKDIR /app

# 빌드 도구인 Gradle 설정 파일들을 먼저 복사 (캐싱을 통한 속도 향상)
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts ./

# 윈도우 환경에서 만든 gradlew 파일이 맥/리눅스에서 실행되도록 권한 부여
RUN chmod +x ./gradlew

RUN ./gradlew --no-daemon dependencies

COPY . .

CMD ["./gradlew", "bootRun"]