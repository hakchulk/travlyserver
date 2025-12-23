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
---

## 🏗 Project Architecture

본 프로젝트는 **도메인형 구조(Domain-driven packaging)**로 설계되어 각 기능별로 응집도가 높습니다.

- **`board`**: 여행 게시글 관련 핵심 비즈니스 로직 (북마크, 댓글, 좋아요 기능 포함)
- **`auth` / `member`**: **HS256 대칭키** 방식의 JWT 인증 및 회원 관리 시스템
- **`file` / `fileupload`**: 여행지 인증 및 이미지 업로드 관리
- **`repository`**: Spring Data JPA를 이용한 선언적 데이터 처리 및 메서드 쿼리 활용
- **`config`**: Security 필터 체인 및 DB 연결 설정 관리

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
