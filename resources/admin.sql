USE kiosk;

-- 카테고리별 기본 옵션 그룹 설정 테이블 생성
CREATE TABLE `CATEGORY_OPTION_GROUP`
(`category_id` int NOT NULL, `group_id` int NOT NULL,
`display_order` int NOT NULL DEFAULT 0 COMMENT '옵션 그룹 표시 순서',
PRIMARY KEY (`category_id`, `group_id`));

SELECT * FROM ORDERS;