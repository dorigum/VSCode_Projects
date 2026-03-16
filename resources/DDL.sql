-- 1. 데이터베이스 생성 및 선택
CREATE DATABASE IF NOT EXISTS kiosk;
USE kiosk;

-- 2. 기존 테이블 삭제 (외래 키 제약 조건 때문에 자식 테이블부터 삭제)
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS `ORDER_ITEM_OPTION`;
DROP TABLE IF EXISTS `ORDER_ITEM`;
DROP TABLE IF EXISTS `ORDERS`;
DROP TABLE IF EXISTS `POINT_HISTORY`;
DROP TABLE IF EXISTS `WISHLIST`;
DROP TABLE IF EXISTS `MENU_OPTION_GROUP`;
DROP TABLE IF EXISTS `MENU_OPTION`;
DROP TABLE IF EXISTS `OPTION_GROUP`;
DROP TABLE IF EXISTS `MENU`;
DROP TABLE IF EXISTS `CATEGORY`;
DROP TABLE IF EXISTS `MEMBER`;
SET FOREIGN_KEY_CHECKS = 1;

-- 3. 테이블 생성
CREATE TABLE `MEMBER` (
  `member_id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT '회원 PK',
  `phone` varchar(20),
  `password` varchar(255) NOT NULL,
  `age` int,
  `point_balance` int NOT NULL DEFAULT 0 COMMENT '보유 포인트',
  `role` varchar(10) NOT NULL DEFAULT 'USER' COMMENT 'USER / ADMIN',
  `preferred_category_id` int DEFAULT 0 COMMENT '선호 카테고리 ID',
  `created_at` datetime NOT NULL DEFAULT (now()) COMMENT '가입일'
);

CREATE TABLE `CATEGORY` (
  `category_id` int PRIMARY KEY AUTO_INCREMENT,
  `category_name` varchar(30) NOT NULL UNIQUE COMMENT '커피 / 논커피 / 디저트 / 라떼 …'
);

CREATE TABLE `MENU` (
  `menu_id` bigint PRIMARY KEY AUTO_INCREMENT,
  `category_id` int NOT NULL,
  `menu_name` varchar(100) NOT NULL UNIQUE,
  `price` int NOT NULL,
  `description` text,
  `is_available` tinyint(1) NOT NULL DEFAULT 1 COMMENT '판매 가능 여부 (품절 시 0)',
  `created_at` datetime NOT NULL DEFAULT (now())
);

CREATE TABLE `OPTION_GROUP` (
  `group_id` int PRIMARY KEY AUTO_INCREMENT,
  `group_name` varchar(30) NOT NULL COMMENT '사이즈 / 온도 / 카페인 / 휘핑'
);

CREATE TABLE `MENU_OPTION` (
  `option_id` int PRIMARY KEY AUTO_INCREMENT,
  `group_id` int NOT NULL,
  `option_name` varchar(30) NOT NULL COMMENT 'Regular / Large / HOT / COLD / 카페인 / 디카페인 / 휘핑有 / 휘핑無',
  `extra_price` int NOT NULL DEFAULT 0 COMMENT '추가 금액',
  `display_order` int NOT NULL COMMENT '옵션 표시 순서'
);

-- 메뉴별 개별 옵션 매핑 테이블
CREATE TABLE `MENU_OPTION_GROUP` (
  `menu_id` bigint NOT NULL,
  `group_id` int NOT NULL,
  `display_order` int NOT NULL DEFAULT 0 COMMENT '옵션 그룹 표시 순서',
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
  `total_amount` int NOT NULL COMMENT '최종 결제 금액',
  `point_used` int NOT NULL DEFAULT 0,
  `point_earned` int NOT NULL DEFAULT 0,
  `status` varchar(10) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING / COMPLETED / CANCELLED',
  `order_date` datetime NOT NULL DEFAULT (now())
);

CREATE TABLE `ORDER_ITEM` (
  `order_item_id` bigint PRIMARY KEY AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `menu_id` bigint NOT NULL,
  `quantity` int NOT NULL,
  `unit_price` int NOT NULL,
  `menu_name_snapshot` varchar(100) NOT NULL,
  `category_name_snapshot` varchar(100) NOT NULL
);

CREATE TABLE `ORDER_ITEM_OPTION` (
  `order_item_id` bigint NOT NULL,
  `option_id` int NOT NULL,
  PRIMARY KEY (`order_item_id`, `option_id`)
);

CREATE TABLE `POINT_HISTORY` (
  `history_id` int PRIMARY KEY AUTO_INCREMENT,
  `member_id` bigint NOT NULL,
  `amount` int NOT NULL,
  `reason` varchar(255) NOT NULL,
  `created_at` datetime NOT NULL DEFAULT (now()),
  FOREIGN KEY (`member_id`) REFERENCES `MEMBER` (`member_id`) ON DELETE CASCADE
);

-- 4. 인덱스 설정
CREATE INDEX `MENU_index_0` ON `MENU` (`category_id`);
CREATE INDEX `MENU_index_1` ON `MENU` (`is_available`);
CREATE INDEX `MENU_OPTION_index_3` ON `MENU_OPTION` (`group_id`);
CREATE INDEX `ORDERS_index_4` ON `ORDERS` (`status`, `order_date`);
CREATE INDEX `ORDER_ITEM_index_6` ON `ORDER_ITEM` (`order_id`);

-- 5. 외래 키 제약 조건 설정
ALTER TABLE `MENU` ADD FOREIGN KEY (`category_id`) REFERENCES `CATEGORY` (`category_id`);
ALTER TABLE `MENU_OPTION` ADD FOREIGN KEY (`group_id`) REFERENCES `OPTION_GROUP` (`group_id`);
ALTER TABLE `MENU_OPTION_GROUP` ADD FOREIGN KEY (`menu_id`) REFERENCES `MENU` (`menu_id`);
ALTER TABLE `MENU_OPTION_GROUP` ADD FOREIGN KEY (`group_id`) REFERENCES `OPTION_GROUP` (`group_id`);
ALTER TABLE `WISHLIST` ADD FOREIGN KEY (`member_id`) REFERENCES `MEMBER` (`member_id`);
ALTER TABLE `WISHLIST` ADD FOREIGN KEY (`menu_id`) REFERENCES `MENU` (`menu_id`);
ALTER TABLE `ORDERS` ADD FOREIGN KEY (`member_id`) REFERENCES `MEMBER` (`member_id`);
ALTER TABLE `ORDER_ITEM` ADD FOREIGN KEY (`order_id`) REFERENCES `ORDERS` (`order_id`);
ALTER TABLE `ORDER_ITEM` ADD FOREIGN KEY (`menu_id`) REFERENCES `MENU` (`menu_id`);
ALTER TABLE `ORDER_ITEM_OPTION` ADD FOREIGN KEY (`order_item_id`) REFERENCES `ORDER_ITEM` (`order_item_id`);
ALTER TABLE `ORDER_ITEM_OPTION` ADD FOREIGN KEY (`option_id`) REFERENCES `MENU_OPTION` (`option_id`);
