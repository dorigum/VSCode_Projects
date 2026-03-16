use kiosk;

SELECT * FROM CATEGORY;
SELECT * FROM MENU;
SELECT * FROM CATEGORY_OPTION;
DESC CATEGORY_OPTION;

-- 데이터 초기화 (순서 주의)
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE `ORDER_ITEM_OPTION`;
TRUNCATE TABLE `ORDER_ITEM`;
TRUNCATE TABLE `ORDERS`;
TRUNCATE TABLE `WISHLIST`;
TRUNCATE TABLE `MENU_OPTION_GROUP`;
TRUNCATE TABLE `CATEGORY_OPTION_GROUP`;
TRUNCATE TABLE `MENU_OPTION`;
TRUNCATE TABLE `OPTION_GROUP`;
TRUNCATE TABLE `MENU`;
TRUNCATE TABLE `CATEGORY`;
TRUNCATE TABLE `MEMBER`;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. MEMBER 데이터
INSERT INTO MEMBER (phone, password, age, point_balance, role) VALUES 
('010-1234-5678', 'admin123', 30, 0, 'ADMIN'),
('010-1111-2222', 'user123', 25, 500, 'USER'),
('010-3333-4444', 'user456', 22, 1200, 'USER');

-- 2. CATEGORY 데이터
INSERT INTO `CATEGORY` (category_name) VALUES 
('커피'),
('논커피'),
('디저트');

-- 3. MENU 데이터
INSERT INTO `MENU` (category_id, menu_name, price, description, is_available) VALUES 
(1, '아메리카노', 4500, '진한 에스프레소와 물의 조화', 1),
(1, '카페라떼', 5000, '부드러운 우유가 들어간 커피', 1),
(2, '초코라떼', 5500, '달콤한 초콜릿과 우유', 1),
(3, '치즈케이크', 6000, '진한 크림치즈의 풍미', 1);

-- 4. OPTION_GROUP 데이터
INSERT INTO `OPTION_GROUP` (group_name) VALUES 
('온도'),        -- ID: 1
('사이즈'),      -- ID: 2
('휘핑유무'),    -- ID: 3
('카페인유무');  -- ID: 4

-- 5. MENU_OPTION 데이터
INSERT INTO `MENU_OPTION` (group_id, option_name, extra_price, display_order) VALUES 
(1, 'HOT', 0, 1),
(1, 'ICE', 500, 2),
(2, 'Regular', 0, 1),
(2, 'Large', 1000, 2),
(3, '휘핑유', 0, 1),
(3, '휘핑무', 0, 2),
(4, '카페인', 0, 1),
(4, '디카페인', 500, 2);

-- 6. CATEGORY_OPTION_GROUP 매핑 (카테고리별 기본 옵션 설정)
INSERT INTO `CATEGORY_OPTION_GROUP` (category_id, group_id, display_order) VALUES 
(1, 1, 1), (1, 2, 2), (1, 4, 3), -- 커피: 온도(1), 사이즈(2), 카페인(4)
(2, 2, 1), (2, 3, 2);           -- 논커피: 사이즈(2), 휘핑(3) (온도/카페인 제외)

-- 7. MENU_OPTION_GROUP 매핑 (개별 메뉴별 옵션 상속)
-- 아메리카노(1), 카페라떼(2) -> 커피 카테고리 옵션 상속
INSERT INTO `MENU_OPTION_GROUP` (menu_id, group_id, display_order) VALUES 
(1, 1, 1), (1, 2, 2), (1, 4, 3),
(2, 1, 1), (2, 2, 2), (2, 4, 3);
-- 초코라떼(3) -> 논커피 카테고리 옵션 상속 (사이즈, 휘핑)
INSERT INTO `MENU_OPTION_GROUP` (menu_id, group_id, display_order) VALUES 
(3, 2, 1), (3, 3, 2);

-- 8. ORDERS 데이터 (샘플 주문 1건)
INSERT INTO `ORDERS` (member_id, total_amount, point_used, point_earned, status) VALUES 
(2, 5000, 0, 500, 'COMPLETED');

-- 9. ORDER_ITEM 데이터
INSERT INTO `ORDER_ITEM` (order_id, menu_id, quantity, unit_price, menu_name_snapshot, category_name_snapshot) VALUES 
(1, 1, 1, 4500, '아메리카노', '커피');

-- 10. ORDER_ITEM_OPTION 데이터 (아메리카노 ICE 선택)
INSERT INTO `ORDER_ITEM_OPTION` (order_item_id, option_id) VALUES 
(1, 2);