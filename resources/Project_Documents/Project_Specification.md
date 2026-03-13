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
    - **기간별 상세 조회**: 날짜 범위 지정을 통한 매출액 및 **객단가(AVG)** 분석 (신규)
    - **시간대별 매출 분석**: 하루 중 주문 집중 시간대(피크타임) 파악 (신규)
    - **우수 회원 기여도 분석**: 누적 결제액 기준 상위 **VVIP 회원** 추출 (신규)
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

### [2026-03-13] 주문 취소 포인트 복구 시스템 및 관리자 UI/UX 고도화
- **지능형 메뉴 등록 로직 도입**:
    - 메뉴 이름에 특정 키워드(**'라떼', '프라푸치노'**)가 포함된 경우 '휘핑유무' 옵션 그룹을 자동으로 매핑하는 지능형 서비스 로직 구현.
    - 이를 통해 관리자의 반복적인 옵션 매핑 작업을 자동화하고 데이터 등록 생산성 향상.
- **주문 취소 및 포인트 정합성 보장**:
    - `CANCELLED` 처리 시 **트랜잭션(Transaction)**을 적용하여 상태 변경과 포인트 업데이트를 원자적으로 처리.
    - 사용자가 주문 시 사용했던 포인트는 **반환**하고, 주문으로 인해 적립되었던 포인트는 **자동 회수**하는 비즈니스 로직 구현.
- **관리자 가독성 및 조작 편의성 개선**:
    - **주문 관리**: 주문 목록 조회 시 회원 ID 대신 **휴대폰 번호**를 출력하도록 JOIN 쿼리 및 모델 확장.
    - **메뉴 관리**: 메뉴 목록 출력 시 실제 **데이터베이스 ID(`[메뉴 ID: X]`)**를 명시적으로 노출하여 삭제/수정 시 식별성 강화.
    - **안전 장치**: 모든 관리자 입력 단계(삭제, 수정 등)에서 **'취소(0)'** 기능을 도입하여 사용자 실수 방지.
- **시스템 안정화**:
    - 카테고리 상세 조회 시 발생하는 SQL 조인 오류 해결 및 단계별 조회 로직으로 리팩토링.
    - `CATEGORY_OPTION_GROUP` 테이블 부재 이슈 해결 및 DDL 가이드 보완.

### [2026-03-13] 관리자 매출 통계 분석 고도화 (Business Intelligence)
- **통계 데이터 가공 및 분석 로직 확장**:
    - **기간별 상세 조회**: 시작일과 종료일을 입력받아 주문 건수, 총액, **객단가(AVG)**를 산출하는 로직 구현.
    - **시간대별 매출 분석**: `HOUR()` SQL 함수를 사용하여 하루 중 피크타임을 시각화(막대 그래프)하는 기능 추가.
    - **VVIP 회원 분석**: 누적 결제액 기준 상위 5명의 회원 리스트와 기여도를 분석하여 마케팅 지표 제공.
- **Repository 및 Service 계층 강화**:
    - `OrderRepository`에 `getSalesStatsByPeriod`, `getHourlySales`, `getTopSpenders` 메서드 추가 및 구현.
    - `AdminService`를 통해 위 기능들을 관리자 메뉴와 연동.
- **데이터 정합성 관리 도구 강화**:
    - 관리자의 오입력 데이터 수정을 위한 **옵션 그룹 및 세부 옵션 삭제** 기능 UI 연동 완료.

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

### [2026-03-12] AdminController 리팩토링 및 MVC 패턴 고도화 (상세 메모 복구)
- **Controller 구조 분리**:
    - `AdminController`를 도입하여 View(UI)와 Service(로직) 사이의 흐름 제어를 전담하도록 설계.
    - 컨트롤러 내에서 모든 예외(`Try-Catch`)를 처리하여 사용자에게 일관된 `FailView` 메시지 전달.
- **View 계층 정문화 (Static 메서드화)**:
    - `EndView`: 성공 메시지 및 데이터 목록(카테고리/메뉴/회원)의 시각적 출력 전담.
    - `FailView`: 모든 에러 상황에 대한 안내 문구 출력 전담.
    - `MenuView`: 관리자 전용 무한 루프 및 입력 처리를 담당하여 비즈니스 로직과 UI 분리.
- **Service 인터페이스 및 구현체 강화**:
    - `AdminService` 인터페이스와 `AdminServiceImpl` 구현체를 분리하여 유연한 확장 기반 마련.
    - 주문 취소(`cancelOrder`) 등의 결과에 따라 `boolean` 타입을 반환하여 컨트롤러에서 분기 처리 가능하게 개선.

### [2026-03-12] DB 데이터 무결성 제약 조건 강화 및 UX 개선 (상세 메모 복구)
- **데이터 무결성 확보**:
    - `CATEGORY` 테이블의 `category_name`에 `UNIQUE` 제약 조건 추가로 중복 등록 원천 차단.
    - `MENU` 테이블의 `menu_name`에도 `UNIQUE` 제약 조건을 추가하여 상품명 중복 방지.
- **주문 관리 UI 고도화**:
    - 주문 목록 조회 시 상태별 마킹 도입 (`[V]` 완료, `[X]` 취소).
    - '폰번호' 필드명을 사용자 친화적인 '주문자'로 변경 및 출력 포맷 최적화.
    - 주문 취소 로직: `COMPLETED` 상태인 주문만 취소 가능하도록 상태 기반 검증 추가.
- **메뉴 정보 시각화 개선**:
    - 메뉴 조회 시 카테고리 ID 대신 실제 **카테고리명**이 출력되도록 JOIN 쿼리 및 매핑 로직 반영.
    - 메뉴의 상세 설명(`description`)과 판매 가능 여부를 목록에 함께 노출.
- **등록 로직 안정화**:
    - 카테고리 ID 입력 시 존재하지 않는 ID를 입력할 경우, 즉시 에러 메시지를 띄우고 메뉴판을 다시 보여주는 루프 로직(`While-Try`) 구현.
- **매출 분석 기능 정밀화**:
    - 날짜별 매출 추이: 일별, 주별, 월별, 연도별 분석 기능을 추가하고 `DATE_FORMAT`을 활용한 통계 쿼리 구현.
    - 주차별 데이터 가독성 개선: 'YYYY-MM-W주' 형태로 가공하여 시각적 직관성 확보.

### [2026-03-11] 프로젝트 환경 고도화 및 이클립스 최적화 (상세 메모 복구)
- **데이터베이스 설정 외부화**:
    - `dbinfo.properties` 파일을 도입하여 DB 접속 주소, ID, PW를 소스 코드 외부에서 관리하도록 개선 (보안 및 유지보수성 향상).
- **JDBC 유틸리티 개선**:
    - `DBUtil` 클래스에 Properties 파일을 자동으로 로드하는 정적 초기화 블록 추가.
    - `SQLException` 발생 시 상세한 로그를 출력하여 디버깅 편의성 확보.
- **환경 설정 동기화**:
    - 프로젝트 인코딩을 **UTF-8**로 강제 지정하여 한글 깨짐 이슈 방지.
    - `lib` 폴더를 생성하고 `mysql-connector-j-9.6.0.jar` 라이브러리 추가 및 빌드 경로(Build Path) 설정 완료.
- **초기 매출 통계 시스템 구축**:
    - 누적 매출 합계 및 인기 메뉴 Top 3 추출을 위한 초기 쿼리 및 Repository 구현 완료.

## 6. 개발 및 협업 가이드 (실행 전 확인)
1.  **MySQL 설정**: `resources/DDL.sql` -> `resources/DML.sql` 순서로 실행
2.  **DB 접속 정보**: `resources/dbinfo.properties` 파일에서 본인의 MySQL 계정/비번 수정
3.  **라이브러리**: `lib/mysql-connector-j-9.6.0.jar`가 Build Path에 포함되어 있는지 확인
4.  **인코딩**: 이클립스 실행 설정(Run Configurations)에서 **Encoding을 UTF-8**로 지정 필수

## 7. 협업을 위한 변경사항
- **최신 DDL 반영 및 테이블 구조 최적화**:
  - `ORDERITEM` → `ORDER_ITEM`, `OPTIONGROUP` → `OPTION_GROUP` 등 테이블명 명명 규칙 통일 (Snake Case)
  - `ORDER_ITEM` 테이블에 `menu_name_snapshot`, `category_name_snapshot` 컬럼 추가하여 데이터 정합성 강화 (메뉴명 변경 시에도 과거 주문 내역 보존)
  - `ORDERS` 테이블의 `ordered_at` → `order_date` 컬럼명 변경
- **소스 코드 동기화**:
  - `OrderRepository`, `OrderItem`, `Order` 클래스의 필드 및 SQL 쿼리를 변경된 DDL에 맞춰 전수 업데이트
  - 인덱스(Index) 설정을 통해 대용량 주문 조회 및 매출 통계 쿼리 성능 최적화
- **DB 스크립트 현행화**:
  - 루트의 `ddl.sql` 내용을 `resources/DDL.sql`에 반영하여 실행 환경 일치화 (기존 schema.sql 대체)
  - 변경된 스키마 구조에 맞춰 샘플 데이터(`DML.sql`) 재구성
