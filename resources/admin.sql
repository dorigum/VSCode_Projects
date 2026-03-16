USE kiosk;

DROP TABLE CATEGORY_OPTION_GROUP;

desc kiosk.MENU_OPTION_GROUP;
SELECT * FROM kiosk.MENU_OPTION_GROUP;
SELECT * FROM kiosk.MEMBER;
SELECT * FROM kiosk.OPTION_GROUP;

DELETE FROM kiosk.OPTION_GROUP WHERE GROUP_ID IN (9, 10, 11);

-- 카테고리별 기본 옵션 그룹 설정 테이블 생성
CREATE TABLE IF NOT EXISTS `CATEGORY_OPTION_GROUP` (
  `category_id` int NOT NULL,
  `group_id` int NOT NULL,
  `display_order` int NOT NULL DEFAULT 0 COMMENT '옵션 그룹 표시 순서',
  PRIMARY KEY (`category_id`, `group_id`)
);

-- 포인트 변동 히스토리 기록 테이블 생성
CREATE TABLE IF NOT EXISTS `POINT_HISTORY` (
  `history_id` INT AUTO_INCREMENT PRIMARY KEY,
  `member_id` BIGINT NOT NULL COMMENT '회원 고유 ID',
  `amount` INT NOT NULL COMMENT '변동 금액 (+는 적립, -는 차감)',
  `reason` VARCHAR(255) NOT NULL COMMENT '변동 사유 (예: 이벤트 적립)',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '변동 일시',
  FOREIGN KEY (`member_id`) REFERENCES `MEMBER`(`member_id`) ON DELETE CASCADE
);

-- 퍼스널 옵션 그룹 및 세부 옵션 생성 (2026-03-15 추가)
-- 주의: 이미 데이터가 있는 경우 중복 오류가 발생할 수 있으므로 상황에 맞춰 실행하세요.
/*
INSERT INTO `OPTION_GROUP` (`group_name`) VALUES ('샷 추가'), ('시럽 추가'), ('퍼스널 옵션');

-- 샷 추가 (그룹ID가 순차적이라고 가정)
INSERT INTO `MENU_OPTION` (`group_id`, `option_name`, `extra_price`, `display_order`) VALUES 
(4, '에스프레소 샷 추가', 500, 1);

-- 시럽 추가
INSERT INTO `MENU_OPTION` (`group_id`, `option_name`, `extra_price`, `display_order`) VALUES 
(5, '헤이즐넛 시럽', 500, 1),
(5, '바닐라 시럽', 500, 2),
(5, '카라멜 시럽', 500, 3);

-- 퍼스널 옵션
INSERT INTO `MENU_OPTION` (`group_id`, `option_name`, `extra_price`, `display_order`) VALUES 
(6, '드리즐 추가', 500, 1),
(6, '자바칩 추가', 600, 2);

-- 커피 카테고리에 매핑 (커피 카테고리 ID가 1번이라고 가정)
INSERT INTO `CATEGORY_OPTION_GROUP` (`category_id`, `group_id`, `display_order`) VALUES 
(1, 4, 3), (1, 5, 4), (1, 6, 5);
*/

-- 확인용 쿼리
SELECT * FROM `POINT_HISTORY` ORDER BY `created_at` DESC;
SELECT * FROM `CATEGORY_OPTION_GROUP`;
SELECT * FROM `OPTION_GROUP`;
SELECT * FROM `MENU_OPTION`;
SELECT * FROM `ORDERS`;
SELECT * FROM `MEMBER`;