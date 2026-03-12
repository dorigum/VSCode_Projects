# ☕ 카페 키오스크 관리자(Admin) MVC 구조 상세 분석

본 문서는 카페 키오스크 프로젝트의 관리자 기능에 적용된 MVC(Model-View-Controller) 패턴과 계층형 아키텍처를 상세히 분석합니다.

---

## 1. 전체 아키텍처 개요
본 프로젝트는 **Layered Architecture**를 채택하여 각 계층의 책임을 명확히 분리하고 있습니다.

- **View (MainView.java)**: 사용자 인터페이스(UI) 담당. 메뉴 출력 및 사용자 입력 수신.
- **Controller (AdminController.java)**: 사용자의 요청을 제어하고 서비스 계층으로 전달.
- **Service (AdminService.java)**: 비즈니스 로직(로그인 검증, 통계 계산, 데이터 가공) 수행.
- **Repository (Admin/Menu/Order Repository)**: 데이터베이스(DB)와의 직접적인 상호작용(CRUD).
- **Model (Category, Menu, Order 등)**: 시스템에서 유통되는 데이터의 구조 정의.

---

## 2. 주요 구성 요소 상세 분석

### 2.1. Controller: `AdminController.java`
관리자 모드의 전체적인 흐름을 제어합니다.
- **주요 기능**: 관리자 인증(ID/PW 확인), 관리자 메인 메뉴 루프 관리, 서비스 메서드 호출.
- **핵심 로직**: `while(true)` 문 내에서 `switch-case`를 사용하여 메뉴 관리, 통계 확인 등의 분기를 처리합니다.

### 2.2. Service: `AdminService.java`
데이터를 처리하는 '두뇌' 역할을 합니다.
- **주요 기능**: 
  - `loginAdmin()`: 관리자 권한 확인.
  - `addMenu()`, `updateMenu()`: 메뉴 수정 시 입력된 데이터의 유효성 검사 후 저장 요청.
  - `viewSalesStats()`: 판매 내역을 분석하여 총 매출액 계산.
- **특징**: 컨트롤러와 리포지토리 사이에서 데이터를 변환하거나 비즈니스 규칙을 적용합니다.

### 2.3. Repository: `MenuRepository.java` 외
JDBC를 사용하여 실제 DB에 쿼리를 실행합니다.
- **주요 기능**: `SELECT`, `INSERT`, `UPDATE`, `DELETE` 쿼리 실행.
- **특징**: `DBUtil.java`를 통해 커넥션을 관리하며, 결과를 모델 객체로 매핑합니다.

### 2.4. Model (DTO/Entity)
데이터베이스의 테이블 구조와 1:1로 대응하거나 화면에 전달될 데이터를 담습니다.
- **Category**: 메뉴 카테고리 정보 (커피, 논커피 등).
- **Menu**: 메뉴명, 가격, 가용 여부.
- **Order/OrderItem**: 주문 내역 및 상세 품목 스냅샷.

---

## 3. 데이터 흐름 (Sequence)
1. **입력**: `AdminController`에서 메뉴 번호를 입력받음.
2. **요청**: `AdminService`의 해당 기능(예: 메뉴 삭제)을 호출.
3. **처리**: `AdminService`에서 삭제 전 관련 데이터(주문 내역 등) 존재 여부 확인 로직 수행.
4. **저장**: `MenuRepository`를 통해 DB에 `DELETE` 또는 `UPDATE` 쿼리 실행.
5. **응답**: 처리 결과를 컨트롤러에 반환하고, 컨트롤러는 최종 결과를 사용자에게 출력.

---

## 4. 향후 개선 사항 제안
1. **비밀번호 보안**: `AdminService` 내의 평문 비밀번호를 암호화하여 저장/비교.
2. **예외 처리**: DB 연결 실패나 잘못된 입력값에 대한 `Try-Catch` 예외 처리 강화.
3. **통계 고도화**: 단순 매출액 합계 외에 일별/월별 추이 및 카테고리별 점유율 계산 로직 추가.
