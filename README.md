# DGU Graduation Machine

동국대학교 졸업요건을 데이터 기반으로 관리하고, 학생 성적표를 업로드해 졸업 충족 여부를 점검하는 Spring Boot 백엔드 프로젝트입니다.

현재 구현은 컴퓨터공학과 2025학년도 기준 규칙을 중심으로 시연 가능하도록 구성되어 있으며, 구조 자체는 다른 학과 규칙도 DB 데이터로 확장할 수 있도록 설계되어 있습니다.

## Overview

이 프로젝트는 크게 두 축으로 구성됩니다.

1. 규칙 관리
졸업요건, 영역, 과목 그룹, 그룹-과목 매핑을 관리자 API로 등록합니다.

2. 학생 성적 판정
학생 성적표 `.xlsx`를 업로드하면 총학점, 전공학점, 교양학점, GPA와 함께 부족한 졸업요건을 `missed` 형태로 반환합니다.

## Tech Stack

- Java 21
- Spring Boot 3.5.x
- Spring Web
- Spring Data JPA
- PostgreSQL
- Apache POI
- Docker / Docker Compose
- Gradle Kotlin DSL

## Domain Model

핵심 규칙 모델은 다음 관계를 가집니다.

- `GraduationRequirement`
학과 / 입학년도 / 커리큘럼 / 전공유형별 최상위 졸업요건

- `AreaRequirement`
전공, 공통교양, MSC, 기본소양 같은 검증 단위 영역

- `CourseGroup`
특정 규칙 묶음
예: `기본소양`, `MSC-수학`, `전공필수-자료구조`

- `AreaCourseGroup`
영역과 그룹을 연결하는 매핑
`minCount`, `isEssential` 등을 통해 “이 그룹에서 몇 개를 들어야 하는가”를 표현

- `Course`
실제 개설 과목 사전

- `CourseGroupItem`
그룹과 과목을 연결하는 매핑
`isEssential`을 통해 그룹 내부의 필수 과목 여부를 관리

## What The API Checks

현재 졸업 판정 응답에서는 다음 항목을 계산합니다.

- `total_credit`
- `major_credit`
- `liberal_credit`
- `overall_gpa`
- `major_gpa`
- `liberal_gpa`
- `missed`

`missed`에는 현재 다음 종류의 부족 조건이 들어갑니다.

- 총학점 부족
- 전공학점 부족
- 그룹 최소 이수 개수 부족
예: 기본소양 3개 중 2개 이수
- 그룹 필수 과목 누락
예: MSC-수학 필수 과목 중 아직 듣지 않은 과목 목록

## Project Structure

```text
src/main/java/com/dongguk/graduation_be
├─ graduation
│  ├─ dto
│  └─ service
├─ requirement
│  ├─ controller
│  ├─ dto
│  ├─ entity
│  ├─ repository
│  └─ service
├─ student
│  ├─ controller
│  ├─ dto
│  └─ service
└─ GraduationBeApplication.java
```

추가 데이터 파일:

- `data/seeds`
규칙 및 과목 시드 CSV

- `data/raw`
원본 엑셀 파일

- `data/testcases`
검증용 데이터셋

## Run Locally

PostgreSQL을 로컬 또는 Docker로 띄운 뒤 애플리케이션을 실행합니다.

환경 변수 기본값:

- `SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/graduation`
- `SPRING_DATASOURCE_USERNAME=postgres`
- `SPRING_DATASOURCE_PASSWORD=postgres`

실행:

```bash
./gradlew bootRun
```

Windows PowerShell:

```powershell
.\gradlew.bat bootRun
```

## Run With Docker Compose

이 프로젝트는 PostgreSQL과 Spring Boot 앱을 함께 올릴 수 있도록 구성되어 있습니다.

실행:

```bash
docker compose up --build
```

백그라운드 실행:

```bash
docker compose up -d --build
```

종료:

```bash
docker compose down
```

기본 포트:

- App: `8080`
- PostgreSQL: `5432`

## Key APIs

### Health Check

```http
GET /hello
```

### Student APIs

성적표 파싱:

```http
POST /api/students/transcripts/parse
```

졸업 판정:

```http
POST /api/students/graduation/check-minimum-credits
```

필수 파라미터:

- `file` (`multipart/form-data`)
- `entranceYear`
- `departmentId`
- `curriculum`
- `majorType`

예시:

```powershell
curl.exe -X POST "http://localhost:8080/api/students/graduation/check-minimum-credits" `
  -F "file=@D:\workspace\Dongguk-Graduation-BE\data\raw\transcript\20260310.xlsx" `
  -F "entranceYear=2025" `
  -F "departmentId=1" `
  -F "curriculum=GENERAL" `
  -F "majorType=SINGLE_MAJOR"
```

### Admin APIs

규칙 세팅용 CRUD:

- `GET/POST/PUT/DELETE /api/admin/departments`
- `GET/POST/PUT/DELETE /api/admin/graduation-requirements`
- `GET/POST /api/admin/area-requirements`
- `GET/POST/PUT/DELETE /api/admin/area-course-groups`
- `GET/POST/PUT/DELETE /api/admin/courses`
- `POST /api/admin/courses/import-csv`
- `GET/POST/PUT/DELETE /api/admin/course-groups`
- `GET/POST/PUT/DELETE /api/admin/course-group-items`
- `POST /api/admin/course-group-items/import-csv`

## Example Workflow

1. 학과와 졸업요건을 등록합니다.
2. 영역(`전공`, `공통교양`, `MSC`, `기본소양`)을 등록합니다.
3. `Course`, `CourseGroup`, `CourseGroupItem`, `AreaCourseGroup`을 설정합니다.
4. 학생 성적표를 업로드합니다.
5. API 응답의 `missed`를 통해 부족 학점과 미이수 과목을 확인합니다.

## Seed Data

프로젝트에는 시연용 CSV가 포함되어 있습니다.

- [course_seed.csv](/d:/workspace/Dongguk-Graduation-BE/data/seeds/course_seed.csv)
- [course_group_seed.csv](/d:/workspace/Dongguk-Graduation-BE/data/seeds/course_group_seed.csv)
- [course_group_item_seed.csv](/d:/workspace/Dongguk-Graduation-BE/data/seeds/course_group_item_seed.csv)
- [area_course_group_seed.csv](/d:/workspace/Dongguk-Graduation-BE/data/seeds/area_course_group_seed.csv)

## Current Scope

현재는 다음 범위를 우선 구현했습니다.

- 성적표 `.xlsx` 파싱
- 총학점 / 전공학점 / 교양학점 / GPA 계산
- 규칙 기반 그룹 최소 이수 개수 검사
- 규칙 기반 필수 과목 누락 검사
- 관리자용 규칙 CRUD 및 CSV 업로드

아직 이후 확장 여지가 있는 부분:

- 선수과목(`Prerequisite`) 검사
- 대체 인정(`Substitution`) 검사
- 학과별 규칙 대량 업로드 자동화
- 프론트엔드 UI

## Notes

- 시연용 응답 구조는 계속 다듬는 중입니다.
- 현재 규칙 데이터는 컴퓨터공학과 예시를 중심으로 채워져 있습니다.
- 프로젝트 목적상 “규칙을 코드에 하드코딩하지 않고 데이터로 판정하는 구조”를 우선으로 두고 있습니다.
