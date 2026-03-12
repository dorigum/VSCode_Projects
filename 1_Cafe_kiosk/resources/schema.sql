-- 1. 데이터베이스 생성 및 선택
CREATE DATABASE IF NOT EXISTS cafe_kiosk;
USE cafe_kiosk;

-- 2. 기존 테이블 삭제 (초기화용)
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS ORDER_ITEM_OPTION, ORDER_ITEM, ORDERS, WISHLIST, MENU_OPTION_GROUP, OPTION, OPTION_GROUP, MENU, CATEGORY, MEMBER;
SET FOREIGN_KEY_CHECKS = 1;

-- 3. 테이블 생성 (ddl.sql 기반)
CREATE TABLE `MEMBER` (
  `member_id` bigint PRIMARY KEY AUTO_INCREMENT COMMENT '회원 PK',
  `phone` varchar(20) UNIQUE,
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
  `created_at` datetime NOT NULL DEFAULT (now()),
  FOREIGN KEY (`category_id`) REFERENCES `CATEGORY` (`category_id`)
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
  `display_order` int NOT NULL DEFAULT 0 COMMENT '옵션 표시 순서',
  FOREIGN KEY (`group_id`) REFERENCES `OPTION_GROUP` (`group_id`)
);

CREATE TABLE `MENU_OPTION_GROUP` (
  `menu_id` bigint NOT NULL,
  `group_id` int NOT NULL,
  `display_order` int NOT NULL DEFAULT 0 COMMENT '옵션 그룹 표시 순서',
  PRIMARY KEY (`menu_id`, `group_id`),
  FOREIGN KEY (`menu_id`) REFERENCES `MENU` (`menu_id`),
  FOREIGN KEY (`group_id`) REFERENCES `OPTION_GROUP` (`group_id`)
);

CREATE TABLE `WISHLIST` (
  `wishlist_id` bigint PRIMARY KEY AUTO_INCREMENT,
  `member_id` bigint NOT NULL,
  `menu_id` bigint NOT NULL,
  `created_at` datetime NOT NULL DEFAULT (now()),
  FOREIGN KEY (`member_id`) REFERENCES `MEMBER` (`member_id`),
  FOREIGN KEY (`menu_id`) REFERENCES `MENU` (`menu_id`)
);

CREATE TABLE `ORDERS` (
  `order_id` bigint PRIMARY KEY AUTO_INCREMENT,
  `member_id` bigint COMMENT '비회원이면 NULL',
  `total_amount` int NOT NULL COMMENT '최종 결제 금액 (포인트 차감 후)',
  `point_used` int NOT NULL DEFAULT 0,
  `point_earned` int NOT NULL DEFAULT 0 COMMENT '결제 금액의 10%',
  `status` varchar(10) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING / COMPLETED / CANCELLED',
  `order_date` datetime NOT NULL DEFAULT (now()),
  FOREIGN KEY (`member_id`) REFERENCES `MEMBER` (`member_id`)
);

CREATE TABLE `ORDER_ITEM` (
  `order_item_id` bigint PRIMARY KEY AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `menu_id` bigint NOT NULL,
  `quantity` int NOT NULL COMMENT '주문 개수',
  `unit_price` int NOT NULL COMMENT '주문 시점 단가 스냅샷',
  `menu_name_snapshot` varchar(100) NOT NULL COMMENT '주문 시점 메뉴 이름 스냅샷',
  `category_name_snapshot` varchar(100) NOT NULL COMMENT '주문 시점 카테고리 스냅샷',
  FOREIGN KEY (`order_id`) REFERENCES `ORDERS` (`order_id`),
  FOREIGN KEY (`menu_id`) REFERENCES `MENU` (`menu_id`)
);

CREATE TABLE `ORDER_ITEM_OPTION` (
  `order_item_id` bigint NOT NULL,
  `option_id` int NOT NULL,
  PRIMARY KEY (`order_item_id`, `option_id`),
  FOREIGN KEY (`order_item_id`) REFERENCES `ORDER_ITEM` (`order_item_id`),
  FOREIGN KEY (`option_id`) REFERENCES `OPTION` (`option_id`)
);

-- 인덱스 생성
CREATE INDEX `MENU_idx_category` ON `MENU` (`category_id`);
CREATE INDEX `ORDERS_idx_date` ON `ORDERS` (`order_date`);
CREATE INDEX `ORDER_ITEM_idx_order` ON `ORDER_ITEM` (`order_id`);
