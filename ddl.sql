

CREATE TABLE `MEMBER` (
  `member_id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT '회원 PK',
  `phone` varchar(20),
  `password` varchar(255) NOT NULL,
  `age` int,
  `point_balance` int NOT NULL DEFAULT 0 COMMENT '보유 포인트',
  `role` varchar(10) NOT NULL DEFAULT 'USER' COMMENT 'USER / ADMIN',
  `created_at` datetime NOT NULL DEFAULT (now()) COMMENT '가입일'
);

CREATE TABLE `CATEGORY` (
  `category_id` int PRIMARY KEY AUTO_INCREMENT,
  `category_name` varchar(30) NOT NULL COMMENT '커피 / 논커피 / 디저트 / 라떼 …'
);

CREATE TABLE `MENU` (
  `menu_id` bigint PRIMARY KEY AUTO_INCREMENT,
  `category_id` int NOT NULL,
  `menu_name` varchar(100) NOT NULL,
  `price` int NOT NULL,
  `description` text,
  `is_available` tinyint(1) NOT NULL DEFAULT 1 COMMENT '판매 가능 여부 (품절 시 0)',
  `created_at` datetime NOT NULL DEFAULT (now())
);

CREATE TABLE `OPTION_GROUP` (
  `group_id` int PRIMARY KEY AUTO_INCREMENT,
  `group_name` varchar(30) NOT NULL COMMENT '사이즈 / 온도 / 카페인 / 휘핑'
);

CREATE TABLE `OPTION` (
  `option_id` int PRIMARY KEY AUTO_INCREMENT,
  `group_id` int NOT NULL,
  `option_name` varchar(30) NOT NULL COMMENT 'Regular / Large / HOT / COLD / 카페인 / 디카페인 / 휘핑有 / 휘핑無',
  `extra_price` int NOT NULL DEFAULT 0 COMMENT '추가 금액',
  `display_order` int NOT NULL COMMENT '옵션 표시 순서 ex) regular(1)/medium(2)/large(3)'
);

CREATE TABLE `MENU_OPTION_GROUP` (
  `menu_id` bigint NOT NULL,
  `group_id` int NOT NULL,
  `display_order` int NOT NULL COMMENT '옵션 그룹 표시 순서 ex) 온도(1), 사이즈(2) 휘핑유무(3)등 ',
  PRIMARY KEY (`menu_id`, `group_id`)
);

CREATE TABLE `WISHLIST` (
  `wishlist_id` bigint PRIMARY KEY AUTO_INCREMENT,
  `member_id` bigint NOT NULL,
  `menu_id` bigint NOT NULL,
  `created_at` datetime NOT NULL DEFAULT (now())
);

CREATE TABLE `ORDERS` (
  `order_id` bigint PRIMARY KEY AUTO_INCREMENT,
  `member_id` bigint COMMENT '비회원이면 NULL',
  `total_amount` int NOT NULL COMMENT '최종 결제 금액 (포인트 차감 후)',
  `point_used` int NOT NULL DEFAULT 0,
  `point_earned` int NOT NULL DEFAULT 0 COMMENT '결제 금액의 10%',
  `status` varchar(10) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING / COMPLETED / CANCELLED',
  `order_date` datetime NOT NULL DEFAULT (now())
);

CREATE TABLE `ORDER_ITEM` (
  `order_item_id` bigint PRIMARY KEY AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `menu_id` bigint NOT NULL,
  `quantity` int NOT NULL COMMENT '주문 개수',
  `unit_price` int NOT NULL COMMENT '주문 시점 단가 스냅샷',
  `menu_name_snapshot` varchar(100) NOT NULL COMMENT '주문 시점 메뉴 이름 스냅샷',
  `category_name_snapshot` varchar(100) NOT NULL COMMENT '주문 시점 카테고리 스냅샷'
);

CREATE TABLE `ORDER_ITEM_OPTION` (
  `order_item_id` bigint NOT NULL,
  `option_id` int NOT NULL,
  PRIMARY KEY (`order_item_id`, `option_id`)
);

CREATE INDEX `MENU_index_0` ON `MENU` (`category_id`);

CREATE INDEX `MENU_index_1` ON `MENU` (`is_available`);

CREATE INDEX `MENU_index_2` ON `MENU` (`created_at`);

CREATE INDEX `OPTION_index_3` ON `OPTION` (`group_id`);

CREATE INDEX `ORDERS_index_4` ON `ORDERS` (`status`, `order_date`);

CREATE INDEX `ORDERS_index_5` ON `ORDERS` (`member_id`, `status`);

CREATE INDEX `ORDER_ITEM_index_6` ON `ORDER_ITEM` (`order_id`);

CREATE INDEX `ORDER_ITEM_index_7` ON `ORDER_ITEM` (`menu_id`);

ALTER TABLE `MENU_OPTION_GROUP` COMMENT = '메뉴별 적용 옵션 그룹 매핑 (예: 디저트엔 온도 옵션 없음)';

ALTER TABLE `ORDER_ITEM_OPTION` COMMENT = '결제 시점에 메뉴가 어떤 옵션이 선택되었는지';

ALTER TABLE `MENU` ADD FOREIGN KEY (`category_id`) REFERENCES `CATEGORY` (`category_id`);

ALTER TABLE `OPTION` ADD FOREIGN KEY (`group_id`) REFERENCES `OPTION_GROUP` (`group_id`);

ALTER TABLE `MENU_OPTION_GROUP` ADD FOREIGN KEY (`menu_id`) REFERENCES `MENU` (`menu_id`);

ALTER TABLE `MENU_OPTION_GROUP` ADD FOREIGN KEY (`group_id`) REFERENCES `OPTION_GROUP` (`group_id`);

ALTER TABLE `WISHLIST` ADD FOREIGN KEY (`member_id`) REFERENCES `MEMBER` (`member_id`);

ALTER TABLE `WISHLIST` ADD FOREIGN KEY (`menu_id`) REFERENCES `MENU` (`menu_id`);

ALTER TABLE `ORDERS` ADD FOREIGN KEY (`member_id`) REFERENCES `MEMBER` (`member_id`);

ALTER TABLE `ORDER_ITEM` ADD FOREIGN KEY (`order_id`) REFERENCES `ORDERS` (`order_id`);

ALTER TABLE `ORDER_ITEM` ADD FOREIGN KEY (`menu_id`) REFERENCES `MENU` (`menu_id`);

ALTER TABLE `ORDER_ITEM_OPTION` ADD FOREIGN KEY (`order_item_id`) REFERENCES `ORDER_ITEM` (`order_item_id`);

ALTER TABLE `ORDER_ITEM_OPTION` ADD FOREIGN KEY (`option_id`) REFERENCES `OPTION` (`option_id`);
