USE cafe_kiosk;

-- 1. CATEGORY 데이터
INSERT INTO CATEGORY (category_name) VALUES ('Coffee'), ('Tea'), ('Dessert');

-- 2. MENU 데이터 (Coffee: 1, Tea: 2, Dessert: 3)
INSERT INTO MENU (category_id, menu_name, price, description) VALUES 
(1, 'Americano', 3000, '기본에 충실한 시원한 아메리카노'),
(1, 'Cafe Latte', 3500, '고소한 우유가 들어간 라떼'),
(2, 'Green Tea', 4000, '유기농 녹차'),
(3, 'Cheese Cake', 5000, '진한 치즈의 풍미');

-- 3. OPTION_GROUP 데이터 (사이즈, 온도)
INSERT INTO OPTION_GROUP (group_name) VALUES ('Size'), ('Temperature');

-- 4. OPTION 데이터 (Size: 1, Temperature: 2)
INSERT INTO OPTION (group_id, option_name, extra_price, display_order) VALUES 
(1, 'Regular', 0, 1),
(1, 'Large', 500, 2),
(2, 'HOT', 0, 1),
(2, 'ICE', 0, 2);

-- 5. MENU_OPTION_GROUP 연결
INSERT INTO MENU_OPTION_GROUP (menu_id, group_id, display_order) VALUES 
(1, 1, 1), (1, 2, 2), -- 아메리카노: 사이즈, 온도
(2, 1, 1), (2, 2, 2); -- 라떼: 사이즈, 온도

-- 6. MEMBER 데이터
INSERT INTO MEMBER (phone, password, age, point_balance, role) VALUES 
('010-1234-5678', 'pass123', 25, 1000, 'USER'),
('010-9876-5432', 'admin789', 30, 0, 'ADMIN'),
('010-1111-2222', 'user111', 22, 500, 'USER');

-- 7. ORDERS 데이터 (회원 1번, 3번 주문)
INSERT INTO ORDERS (member_id, total_amount, point_used, point_earned, status, order_date) VALUES 
(1, 3500, 0, 350, 'COMPLETED', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(3, 8500, 500, 800, 'COMPLETED', NOW());

-- 8. ORDER_ITEM 데이터 (주문 상세 + 스냅샷)
-- 주문 1: 아메리카노 1잔 (3500원 - 라지 사이즈 적용된 가격 스냅샷)
INSERT INTO ORDER_ITEM (order_id, menu_id, quantity, unit_price, menu_name_snapshot, category_name_snapshot) VALUES 
(1, 1, 1, 3500, 'Americano', 'Coffee');

-- 주문 2: 라떼 1잔(3500원) + 치즈케이크 1개(5000원) = 8500원
INSERT INTO ORDER_ITEM (order_id, menu_id, quantity, unit_price, menu_name_snapshot, category_name_snapshot) VALUES 
(2, 2, 1, 3500, 'Cafe Latte', 'Coffee'),
(2, 4, 1, 5000, 'Cheese Cake', 'Dessert');

-- 9. ORDER_ITEM_OPTION 데이터 (주문 상세 옵션)
-- 주문 1의 아메리카노: Large(2), ICE(4) 선택
INSERT INTO ORDER_ITEM_OPTION (order_item_id, option_id) VALUES (1, 2), (1, 4);
-- 주문 2의 라떼: Regular(1), HOT(3) 선택
INSERT INTO ORDER_ITEM_OPTION (order_item_id, option_id) VALUES (2, 1), (2, 3);

-- 10. WISHLIST 데이터
INSERT INTO WISHLIST (member_id, menu_id) VALUES (1, 4); -- 회원 1이 치즈케이크 찜
