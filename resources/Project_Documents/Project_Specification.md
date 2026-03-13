# ☕ 카페 키오스크 프로젝트 명세서 (Cafe Kiosk Project)

## 1. 프로젝트 개요
- **목표**: MVC 패턴을 적용한 콘솔 기반 카페 키오스크 시스템 구축 (Layered Architecture)
- **주요 기능**: 회원/비회원 주문 서비스 및 관리자 모드 (상품/카테고리/회원/매출 관리)
- **개발 환경**: Java 18 (OpenJDK), **MySQL 8.0**, JDBC (mysql-connector-j-9.6.0)

## 2. 주요 기능 요구사항 (진행 현황)

### 👤 사용자(회원/비회원) 기능
- [x] **회원 로그인**: 휴대폰 번호/비밀번호 인증 (`MemberRepository` 연동)
- [x] **주문 내역 조회**: JOIN 쿼리를 통한 상세 내역 확인
- [ ] 상품 선택 및 장바구니 담기 (구현 예정)
- [ ] 결제 처리 및 포인트 적립 (구현 예정)

### 🛠️ 관리자(Admin) 통합 관리 시스템
- [x] **카테고리 관리 (CRUD)**: 카테고리 추가, 목록 조회, 삭제
- [x] **메뉴 및 옵션 관리 (CRUD)**: 
    - **메뉴 정보 관리**: 카테고리별 상품 등록, 조회, 삭제
    - **메뉴 옵션 관리**: 옵션 그룹(온도, 사이즈 등) 및 세부 옵션(ICE, Regular 등) 관리 기능 추가
    - **카테고리별 옵션 설정**: 각 카테고리에 기본 적용될 옵션 그룹 매핑 설정 기능 추가
- [x] **회원 관리**: 가입된 전체 회원 목록 조회 및 특정 회원 삭제
- [x] **매출 통계 및 분석**:
    - 누적 총 매출액 집계
    - **카테고리별 매출 분석** (Coffee, Tea 등)
    - **인기 메뉴 Top 3** 선정 (판매량 기준)
    - **일별 매출 추이 시각화** (최근 7일 막대 그래프)
- [x] **주문 관리**: 전체 주문 목록 확인 및 **결제 취소(CANCELLED)** 처리

## 3. 시스템 아키텍처 (Layered Architecture)
- **Model**: `Menu`, `Member`, `Order`, `MenuOption`, `OptionGroup` (DB 테이블 대응 객체)
- **View**: `StartView` (진입점), `MenuView` (메인 루프), `EndView` (출력 전담), `FailView` (에러 전담), `OrderingView` (주문 전담)
- **Controller**: `AdminController`, `MenuController`, `MemberController`
- **Service**: `AdminService`, `MemberService`, `MenuService`
- **Repository**: `MenuRepository`, `MemberRepository`, `CategoryRepository`, `OrderRepository`, `MenuOptionRepository`, `OptionGroupRepository`
- **Configuration**: `dbinfo.properties` (외부 설정 파일을 통한 DB 정보 관리)

## 4. 데이터 설계 (MySQL 테이블 구조)
| 구분 | 테이블명 | 설명 |
| :--- | :--- | :--- |
| **Category** | CATEGORY | 상품 분류 (Coffee, Tea, Dessert 등) |
| **Menu** | MENU | 상품명, 가격, 설명, 품절 여부 |
| **MenuOption** | MENU_OPTION | 세부 메뉴 옵션 (HOT, ICE, Regular, Large 등) |
| **OptionGroup** | OPTION_GROUP | 옵션 그룹 (온도, 사이즈, 카페인유무 등) |
| **CategoryOption** | CATEGORY_OPTION_GROUP | 카테고리별 기본 적용 옵션 그룹 매핑 (신규) |
| **Mapping** | MENU_OPTION_GROUP | 메뉴별 적용 가능한 옵션 그룹 매핑 |
| **Member** | MEMBER | 회원 정보, 휴대폰 번호(Unique), 포인트, 역할 |
| **Order** | ORDERS | 주문 총액, 사용/적립 포인트, 주문 상태, 주문 일시 |
| **OrderItem** | ORDER_ITEM | 주문 상세(수량, 단가), 메뉴/카테고리명 스냅샷 포함 |
| **OrderItemOption**| ORDER_ITEM_OPTION | 주문 시점에 선택된 세부 옵션 기록 |
| **Wishlist** | WISHLIST | 회원의 찜 목록 |

## 5. 진행 현황 및 히스토리

### [2026-03-13] 카테고리별 옵션 체계 고도화 및 팀 프로젝트 통합
- **카테고리 기반 옵션 관리**:
    - `CATEGORY_OPTION_GROUP` 테이블 도입을 통한 카테고리 단위 옵션 일괄 설정 구현.
    - 커피(온도/사이즈/카페인), 논커피(온도/사이즈/휘핑) 등 카테고리별 기본 옵션 제공 규칙 정의.
- **Menu 모델 및 연동 로직 강화**:
    - `Menu` 모델에 `List<OptionGroup>` 필드를 추가하여 메뉴 조회 시 해당 메뉴에 적용 가능한 옵션 정보를 함께 로드하도록 개선.
    - `MenuRepositoryImpl`에서 `fetchOptionGroups` 메서드를 통해 객체 그래프 탐색 구조 구현.
- **Repository & Service 고도화**:
    - `CategoryRepository`에서 `LEFT JOIN`을 사용하여 카테고리와 옵션 그룹 정보를 일괄 로드하도록 개선.
    - `Category` 모델 내부에 옵션 그룹 리스트(`List<OptionGroup>`)를 포함하여 객체 지향적 구조 강화.
- **UI/UX 및 관리자 기능 확장**:
    - `EndView`의 메뉴 목록 출력 시 각 메뉴별 '이용 가능한 옵션' 정보를 시각적으로 표시하여 사용자 편의성 증대.
    - `MenuView`의 카테고리 관리 메뉴 내에 '옵션 그룹 매핑 설정' 기능을 추가하여 실시간 규칙 제어 가능.
- **팀 최신 기능 병합**:
    - 팀 저장소(`develop`)의 `OrderingView` 및 최신 검색/조회 기능들을 성공적으로 병합 및 통합 완료.

### [2026-03-13] 데이터베이스 예약어 충돌 해결 및 관리자 기능 고도화
- **DB 아키텍처 개선**:
    - `OPTION` 테이블명을 `MENU_OPTION`으로 변경하여 예약어 충돌 이슈 원천 차단.
    - `Option.java` → `MenuOption.java` 리팩토링 및 오타 수정.
- **관리자 기능 계층화**:
    - 메뉴 관리를 '메뉴 정보 관리'와 '메뉴 옵션 관리'로 분리.
    - 옵션 그룹 및 세부 옵션에 대한 독자적인 CRUD 체계 구축.

### [2026-03-12] AdminController 리팩토링 및 MVC 패턴 고도화
- (중략)

### [2026-03-11] 프로젝트 환경 고도화 및 이클립스 최적화
- (중략)

## 6. 개발 및 협업 가이드 (실행 전 확인)
1.  **MySQL 설정**: `resources/DDL.sql` -> `resources/DML.sql` 순서로 실행
2.  **DB 접속 정보**: `resources/dbinfo.properties` 파일에서 계정 정보 수정
3.  **인코딩**: 실행 설정에서 **Encoding을 UTF-8**로 지정 필수
