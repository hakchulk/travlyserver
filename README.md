![Travly](https://github.com/hakchulk/travlyserver/blob/dev/src/main/resources/travly.png?raw=true)
## - 여행 기록과 공유, 그리고 사용자 성장 경험을 결합한 지도 기반 커뮤니티 플랫폼 API

**Travly Server**는 사용자들이 자신만의 특별한 여행 경로를 기록하고 공유할 수 있도록 돕는 RESTful API 서버입니다. Spring Boot와 JPA를 기반으로 효율적인 데이터 처리를 구현.

---

## 🛠 Tech Stack

### Backend

<img src="https://img.shields.io/badge/Java%2017-007396?style=for-the-badge&logo=java&logoColor=white"> <img src="https://img.shields.io/badge/Spring%20Boot%203.5.8-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white"> <img src="https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=spring-security&logoColor=white"> <img src="https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white">

### Database & Auth

<img src="https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white"> <img src="https://img.shields.io/badge/Supabase-3ECF8E?style=for-the-badge&logo=supabase&logoColor=white"> <img src="https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=json-web-tokens&logoColor=white">

### Libraries & Tools

<img src="https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white"> <img src="https://img.shields.io/badge/Swagger(Springdoc)-85EA2D?style=for-the-badge&logo=swagger&logoColor=black"> <img src="https://img.shields.io/badge/Lombok-BC2139?style=for-the-badge"> <img src="https://img.shields.io/badge/Thumbnailator-FF6F00?style=for-the-badge&logo=image-charts&logoColor=white">
 <img src="https://img.shields.io/badge/DBeaver-382923?style=for-the-badge&logo=dbeaver&logoColor=white">
 <img src="https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white">
 <img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white">

### Design & Management

<img src="https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=notion&logoColor=white"> <img src="https://img.shields.io/badge/Figma-F24E1E?style=for-the-badge&logo=figma&logoColor=white"> <img src="https://img.shields.io/badge/Canva-00C4CC?style=for-the-badge&logo=canva&logoColor=white">

---

## 🏗 Project Architecture

본 프로젝트는 도메인형 구조(Domain-driven packaging)로 설계되어 각 기능별로 응집도가 높습니다.

- **`board`**: 여행 게시글 관련 핵심 비즈니스 로직 (북마크, 댓글, 좋아요 기능 포함)
- **`auth` / `member`**: **HS256 대칭키** 방식의 JWT 인증 및 회원 관리 시스템
- **`file` / `fileupload`**: 여행지 인증 및 이미지 업로드 관리
- **`repository`**: Spring Data JPA를 이용한 선언적 데이터 처리 및 메서드 쿼리 활용
- **`config`**: Security 필터 체인 및 DB 연결 설정 관리

---

## 🗄️ Database Design

본 프로젝트는 데이터 무결성과 확장성을 최우선으로 고려하여 설계되었습니다.

### 1. 설계 원칙 및 특징
* **정규화 표준 준수**: 데이터 중복을 최소화하고 데이터 간의 관계(Relationship)를 명확히 정의하여, 개발 단계에서 발생할 수 있는 데이터 정합성 오류를 방지했습니다.
* **중심 엔티티 구조 (`Member` & `Board`)**: 사용자(`member`)와 게시글(`board`) 테이블을 핵심 축으로 설정하고, 이를 중심으로 활동 데이터(댓글, 좋아요, 북마크)가 유기적으로 연결되는 구조를 채택했습니다.

### 2. 유연한 확장성 (Metadata & Category)
* **데이터 기반 필터링**: 새로운 카테고리(예: 주차 가능 여부, 반려동물 동반 등)가 추가될 때 DB 스키마를 변경하는 것이 아니라, **데이터(Row) 추가만으로 서비스에 즉시 반영**할 수 있는 메타데이터 관리 방식을 채택하여 운영 유연성을 확보했습니다.

### 3. 복합 콘텐츠 및 경로 표현
* **다중 장소 및 사진 매핑**: 하나의 게시물에 여러 장소를 포함하고, 각 장소마다 여러 사진을 매칭할 수 있는 **1:N 구조**를 가집니다.
* **확장 가능한 여행 동선**: 데이터 로우를 추가하는 것만으로 복잡한 여행 동선을 표현할 수 있어, 향후 '여행 코스 공유'나 '테마 맛집 투어' 등 복합 콘텐츠로의 기능 확장에 유연하게 대응합니다.

### 4. 파일 관리 시스템의 중앙화
* **통합 파일 엔티티 전략**: 프로필 사진(`member`), 장소 사진(`board_place`), 문의 게시판(`qna`) 등 서비스 전반의 이미지 리소스를 별도의 `file` 테이블에서 통합 관리하고 참조하는 방식을 사용하여 리소스 관리 효율성을 높였습니다.

---
## ✨ Key Features

- [x] **HS256 JWT 인증**: Supabase의 JWT Secret을 활용한 대칭키 기반 사용자 인증
- [x] **JPA 기반 데이터 관리**: 인터페이스 메서드를 활용한 깔끔한 데이터 조회 및 영속성 관리
- [x] **다중 파일 업로드**: 여행 코스별 사진을 안정적으로 업로드 및 처리
- [x] **게시판 인터랙션**: 좋아요, 북마크, 댓글 등 사용자의 실시간 활동 기록
- [x] **커스텀 예외 처리**: 서비스 계층의 에러를 통합 관리하는 Exception Handler 구축

---

## 🔒 Security & Auth (HS256)

본 프로젝트는 **HS256(HMAC with SHA-256)** 알고리즘을 사용하여 보안을 강화했습니다.

- **Symmetric Key**: 하나의 Secret Key를 공유하여 발행(Sign)과 검증(Verify)을 수행합니다.
- **Supabase Integration**: Supabase API 설정의 JWT Secret을 서버 환경 변수로 관리하여 보안성을 확보했습니다.

---

## 🚀 Installation & Setup

1. **Repository Clone**
   ```bash
   git clone [https://github.com/hakchulk/travlyserver.git](https://github.com/hakchulk/travlyserver.git)
   ```
