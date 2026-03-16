# OrderingView 명세

## 개요

`OrderingView`는 키오스크의 손님 주문 화면이다.
아래 흐름을 담당한다.

- 카테고리 선택
- 메뉴 조회 및 선택
- 옵션 선택
- 수량 입력
- 장바구니 관리
- 주문 확정
- 회원 포인트 사용

대상 소스:
- `src/view/OrderingView.java`

## 메인 진입 흐름

`run(MenuController menuController, Member member)` 실행 시 메모리상의 장바구니(`List<OrderItem> cart`)를 생성하고 반복 루프를 시작한다.

상위 메뉴:
- `1. 인기 상품`
- `2. 신상품`
- `3. 커피`
- `4. 논커피`
- `5. 디저트`
- `8. 카트확인`
- `9. 주문하기`
- `0. 뒤로가기`

동작:
- `0`: 주문 화면 종료
- `1~5`: 카테고리/조건별 메뉴 목록 조회
- `8`: 장바구니 관리 화면 진입
- `9`: 주문 확인 진행

## 메뉴 선택 흐름

메뉴 목록이 로드되면 `runMenuSelectionLoop(...)`가 실행된다.

출력 방식:
- `EndView.printOrderMenu(...)` 사용
- 손님이 선택할 수 있도록 화면 표시 순서 기준 `1..N` 인덱스를 출력
- 관리자용 `menuId` 선택 방식은 사용하지 않음

입력 동작:
- `0`: 카테고리 화면으로 복귀
- `8`: 장바구니 관리로 이동
- `9`: 주문 확인 진행
- `1..N`: 해당 메뉴 선택 후 옵션/수량 입력 단계로 이동

## 옵션 선택 흐름

메뉴 선택 후 동작:
1. `MenuController.getOptionGroups(menu)`로 옵션 그룹 조회
2. `selectMenuOptions(...)` 실행
3. `OptionSelectionService.createDefaultSelection(...)`으로 기본 옵션 선택 상태 생성

입력 동작:
- `0`: 메뉴 담기 취소
- `9`: 현재 옵션 상태 확정
- `1..N`: 옵션 그룹 선택 후 세부 옵션 선택

## 수량 입력 흐름

옵션 확정 후 수량을 입력한다.

- 프롬프트: `개수 선택 (0. 뒤로)`
- `0`: 담기 취소
- `1 이상`: `OrderItem` 생성 후 장바구니 추가

생성 경로:
- `createOrderItem(...)`
- `OrderItemService.createOrderItem(...)`

## 장바구니 관리 명세

`8. 카트확인` 선택 시 `runCartManagementLoop(...)`로 진입한다.

진입 메서드:
- `runCartManagementLoop(MenuController menuController, List<OrderItem> cart, Member member)`

화면 흐름:
1. `EndView.printCart(cart)`로 현재 장바구니 출력
2. `EndView.printCartManagementMenu()`로 관리 메뉴 출력
3. 사용자 입력 대기
4. 선택한 작업 수행
5. 장바구니 관리 화면 재출력

장바구니 관리 메뉴:
- `1. 상품 삭제`
- `2. 수량 변경`
- `3. 장바구니 비우기`
- `9. 주문하기`
- `0. 뒤로`

### 상품 삭제

메서드:
- `removeCartItem(List<OrderItem> cart)`

동작:
- 삭제할 장바구니 상품 번호 입력
- 유효 범위는 `1..cart.size()`
- `0` 입력 시 취소
- 선택한 항목을 장바구니에서 제거
- 성공 메시지 출력

검증:
- 장바구니가 비어 있으면 삭제 불가
- 잘못된 번호 입력 시 실패 메시지 출력

### 수량 변경

메서드:
- `changeCartItemQuantity(List<OrderItem> cart)`

동작:
- 변경할 상품 번호 입력
- 새 수량 입력
- 새 수량은 `1 이상`이어야 함
- `0` 입력 시 취소
- 기존 `OrderItem`을 새 객체로 교체하여 수량 반영

구현 메모:
- `OrderItem`은 수량 setter가 없음
- `copyOrderItemWithQuantity(...)`로 새 객체를 생성해 교체함

검증:
- 장바구니가 비어 있으면 변경 불가
- 잘못된 상품 번호 입력 시 실패 메시지 출력
- `1` 미만 수량 입력 시 실패 메시지 출력

### 장바구니 비우기

메서드:
- `clearCart(List<OrderItem> cart)`

동작:
- 확인 입력 요청
- `1`: 전체 비우기
- 그 외: 취소

검증:
- 장바구니가 비어 있으면 실행 전 차단

## 주문 시 포인트 사용 명세

주문 확정 메서드:
- `confirmOrder(MenuController menuController, List<OrderItem> cart, Member member)`

동작:
1. 장바구니 출력
2. 장바구니 총액 계산
3. 회원이면 현재 보유 포인트 출력
4. 사용할 포인트 입력
5. 차감 후 결제 예정 금액 출력
6. `1. 주문, 0. 뒤로` 입력
7. 주문 성공 시 장바구니 비우기

포인트 입력 규칙:
- 비회원: 포인트 사용 불가, 자동으로 `0`
- 회원: `0 이상` 입력 가능
- 보유 포인트 초과 사용 불가
- 주문 총액 초과 사용 불가

주문 저장:
- `menuController.order(cart, member, pointUsed)` 호출
- 저장소에서는 `point_used`, `point_earned`를 함께 저장
- 회원 포인트는 `기존 포인트 - 사용 포인트 + 적립 포인트`로 갱신

적립 포인트 계산:
- `(총 주문금액 - 사용 포인트) / 10`

## 입력 검증 규칙

공통:
- 모든 숫자 입력은 `readInt(...)` 사용
- 숫자가 아닌 입력은 실패 메시지 출력

장바구니 관련:
- 상품 번호는 항상 `1..cart.size()` 범위여야 함
- 수량 변경은 `1 이상`

담기 수량 관련:
- 수량은 `0 이상`
- `0`은 취소 의미

포인트 관련:
- 포인트 사용값은 `0 이상`
- 회원 보유 포인트 이하
- 주문 총액 이하

## 현재 구현 범위

구현됨:
- 장바구니 조회
- 장바구니 상품 삭제
- 장바구니 수량 변경
- 장바구니 전체 비우기
- 장바구니에서 바로 주문하기
- 회원 포인트 사용

미구현:
- 장바구니 상품 옵션 변경
- 장바구니 상품 순서 이동
- 같은 메뉴 자동 합치기

## 다음 확장 추천

다음 우선순위 기능:
- 장바구니 상품 옵션 변경

추천 방식:
1. 장바구니 상품 선택
2. 대상 메뉴의 옵션 그룹 재조회
3. 기존 선택 옵션을 초기값으로 세팅
4. 수정 후 기존 `OrderItem` 교체
