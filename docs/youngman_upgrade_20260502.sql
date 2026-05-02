-- youngman 旧库升级脚本（2026-05-02）
-- 仅用于已有数据库升级，不删除业务数据
SET NAMES utf8mb4;
USE `youngman`;

-- 1) sys_user 新增字段（通过 information_schema 判断，避免重复添加）
SET @db_name = DATABASE();

SET @col_exists = (
  SELECT COUNT(1) FROM information_schema.columns
  WHERE table_schema = @db_name AND table_name = 'sys_user' AND column_name = 'avatar'
);
SET @sql = IF(@col_exists = 0,
  'ALTER TABLE `sys_user` ADD COLUMN `avatar` varchar(500) DEFAULT NULL COMMENT ''头像URL'' AFTER `phone`',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @col_exists = (
  SELECT COUNT(1) FROM information_schema.columns
  WHERE table_schema = @db_name AND table_name = 'sys_user' AND column_name = 'email'
);
SET @sql = IF(@col_exists = 0,
  'ALTER TABLE `sys_user` ADD COLUMN `email` varchar(100) DEFAULT NULL COMMENT ''邮箱'' AFTER `avatar`',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @col_exists = (
  SELECT COUNT(1) FROM information_schema.columns
  WHERE table_schema = @db_name AND table_name = 'sys_user' AND column_name = 'id_card'
);
SET @sql = IF(@col_exists = 0,
  'ALTER TABLE `sys_user` ADD COLUMN `id_card` varchar(30) DEFAULT NULL COMMENT ''身份证号'' AFTER `email`',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @col_exists = (
  SELECT COUNT(1) FROM information_schema.columns
  WHERE table_schema = @db_name AND table_name = 'sys_user' AND column_name = 'emergency_contact'
);
SET @sql = IF(@col_exists = 0,
  'ALTER TABLE `sys_user` ADD COLUMN `emergency_contact` varchar(50) DEFAULT NULL COMMENT ''紧急联系人'' AFTER `id_card`',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @col_exists = (
  SELECT COUNT(1) FROM information_schema.columns
  WHERE table_schema = @db_name AND table_name = 'sys_user' AND column_name = 'emergency_phone'
);
SET @sql = IF(@col_exists = 0,
  'ALTER TABLE `sys_user` ADD COLUMN `emergency_phone` varchar(20) DEFAULT NULL COMMENT ''紧急联系人手机号'' AFTER `emergency_contact`',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @col_exists = (
  SELECT COUNT(1) FROM information_schema.columns
  WHERE table_schema = @db_name AND table_name = 'sys_user' AND column_name = 'remark'
);
SET @sql = IF(@col_exists = 0,
  'ALTER TABLE `sys_user` ADD COLUMN `remark` varchar(500) DEFAULT NULL COMMENT ''备注'' AFTER `emergency_phone`',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2) sys_user 索引（不存在则创建）
SET @idx_exists = (
  SELECT COUNT(1) FROM information_schema.statistics
  WHERE table_schema = @db_name AND table_name = 'sys_user' AND index_name = 'uk_sys_user_email'
);
SET @sql = IF(@idx_exists = 0,
  'ALTER TABLE `sys_user` ADD UNIQUE KEY `uk_sys_user_email` (`email`)',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @idx_exists = (
  SELECT COUNT(1) FROM information_schema.statistics
  WHERE table_schema = @db_name AND table_name = 'sys_user' AND index_name = 'idx_sys_user_id_card'
);
SET @sql = IF(@idx_exists = 0,
  'ALTER TABLE `sys_user` ADD KEY `idx_sys_user_id_card` (`id_card`)',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @idx_exists = (
  SELECT COUNT(1) FROM information_schema.statistics
  WHERE table_schema = @db_name AND table_name = 'sys_user' AND index_name = 'idx_sys_user_emergency_phone'
);
SET @sql = IF(@idx_exists = 0,
  'ALTER TABLE `sys_user` ADD KEY `idx_sys_user_emergency_phone` (`emergency_phone`)',
  'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 3) 新表创建（仅不存在时创建）
CREATE TABLE IF NOT EXISTS `register_verify_code` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `phone` varchar(20) NOT NULL,
  `code` varchar(10) NOT NULL,
  `expire_time` datetime NOT NULL,
  `used` tinyint NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_register_verify_code_phone` (`phone`),
  KEY `idx_register_verify_code_expire_time` (`expire_time`),
  KEY `idx_register_verify_code_used` (`used`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='注册验证码表';

CREATE TABLE IF NOT EXISTS `user_payment_setting` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint unsigned NOT NULL,
  `pay_type` tinyint NOT NULL,
  `balance` decimal(10,2) NOT NULL DEFAULT '0.00',
  `is_default` tinyint NOT NULL DEFAULT '0',
  `status` tinyint NOT NULL DEFAULT '1',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_payment_setting_user_pay_type` (`user_id`,`pay_type`),
  KEY `idx_user_payment_setting_user_id` (`user_id`),
  CONSTRAINT `fk_user_payment_setting_user_id` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户支付设置表';

CREATE TABLE IF NOT EXISTS `home_banner` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(100) NOT NULL,
  `subtitle` varchar(255) DEFAULT NULL,
  `image_url` varchar(500) NOT NULL,
  `link_url` varchar(500) DEFAULT NULL,
  `sort_no` int NOT NULL DEFAULT '0',
  `status` tinyint NOT NULL DEFAULT '1',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_home_banner_status` (`status`),
  KEY `idx_home_banner_sort_no` (`sort_no`),
  KEY `idx_home_banner_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='首页轮播图表';

CREATE TABLE IF NOT EXISTS `message_board` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint unsigned NOT NULL,
  `category` tinyint NOT NULL,
  `title` varchar(200) NOT NULL,
  `content` varchar(4000) NOT NULL,
  `contact_info` varchar(200) DEFAULT NULL,
  `status` tinyint NOT NULL DEFAULT '0',
  `view_count` int NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_message_board_user_id` (`user_id`),
  CONSTRAINT `fk_message_board_user_id` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='留言板表';

CREATE TABLE IF NOT EXISTS `hostel_activity` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(200) NOT NULL,
  `cover_image` varchar(500) DEFAULT NULL,
  `description` varchar(4000) DEFAULT NULL,
  `activity_time` datetime NOT NULL,
  `location` varchar(255) NOT NULL,
  `max_people` int NOT NULL DEFAULT '0',
  `joined_people` int NOT NULL DEFAULT '0',
  `status` tinyint NOT NULL DEFAULT '0',
  `recommend` tinyint NOT NULL DEFAULT '0',
  `sort_no` int NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_hostel_activity_status` (`status`),
  KEY `idx_hostel_activity_activity_time` (`activity_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='青旅活动表';

CREATE TABLE IF NOT EXISTS `hostel_activity_signup` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `activity_id` bigint unsigned NOT NULL,
  `user_id` bigint unsigned NOT NULL,
  `signup_status` tinyint NOT NULL DEFAULT '1',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_activity_signup_activity_user` (`activity_id`,`user_id`),
  KEY `idx_activity_signup_user_id` (`user_id`),
  CONSTRAINT `fk_activity_signup_activity_id` FOREIGN KEY (`activity_id`) REFERENCES `hostel_activity` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `fk_activity_signup_user_id` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='活动报名表';

CREATE TABLE IF NOT EXISTS `customer_chat_session` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint unsigned NOT NULL,
  `admin_id` bigint unsigned DEFAULT NULL,
  `status` tinyint NOT NULL DEFAULT '1',
  `last_message` varchar(2000) DEFAULT NULL,
  `last_message_time` datetime DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_customer_chat_session_user_id` (`user_id`),
  KEY `idx_customer_chat_session_admin_id` (`admin_id`),
  CONSTRAINT `fk_customer_chat_session_user_id` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `fk_customer_chat_session_admin_id` FOREIGN KEY (`admin_id`) REFERENCES `sys_user` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='客服会话表';

CREATE TABLE IF NOT EXISTS `customer_chat_message` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `session_id` bigint unsigned NOT NULL,
  `sender_id` bigint unsigned NOT NULL,
  `sender_role` tinyint NOT NULL,
  `content` varchar(4000) NOT NULL,
  `is_read` tinyint NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_customer_chat_message_session_id` (`session_id`),
  KEY `idx_customer_chat_message_sender_id` (`sender_id`),
  CONSTRAINT `fk_customer_chat_message_session_id` FOREIGN KEY (`session_id`) REFERENCES `customer_chat_session` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `fk_customer_chat_message_sender_id` FOREIGN KEY (`sender_id`) REFERENCES `sys_user` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='客服消息表';

-- 4) 初始化数据：仅在不存在同标题时插入
INSERT IGNORE INTO `home_banner` (`id`,`title`,`subtitle`,`image_url`,`link_url`,`sort_no`,`status`,`deleted`)
SELECT 1001, '城市青年旅社外观', '地铁步行5分钟，交通便利', 'https://cdn.youngman.example/banner/hostel-exterior.jpg', '/user/home', 1, 1, 0
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM `home_banner` WHERE `title` = '城市青年旅社外观');

INSERT IGNORE INTO `home_banner` (`id`,`title`,`subtitle`,`image_url`,`link_url`,`sort_no`,`status`,`deleted`)
SELECT 1002, '大堂公共休息区', '24小时自助前台与咖啡吧', 'https://cdn.youngman.example/banner/lobby.jpg', '/user/home', 2, 1, 0
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM `home_banner` WHERE `title` = '大堂公共休息区');

INSERT IGNORE INTO `home_banner` (`id`,`title`,`subtitle`,`image_url`,`link_url`,`sort_no`,`status`,`deleted`)
SELECT 1003, '共享厨房与活动区', '开放式厨房、桌游与社交空间', 'https://cdn.youngman.example/banner/public-area.jpg', '/user/home', 3, 1, 0
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM `home_banner` WHERE `title` = '共享厨房与活动区');

INSERT IGNORE INTO `hostel_activity` (`id`,`title`,`cover_image`,`description`,`activity_time`,`location`,`max_people`,`joined_people`,`status`,`recommend`,`sort_no`,`deleted`)
SELECT 2001, '周五城市夜骑', 'https://cdn.youngman.example/activity/night-ride.jpg', '由青旅志愿者带队，沿江夜骑约12公里，适合新入住旅客快速熟悉城市。', DATE_ADD(CURDATE(), INTERVAL 5 DAY) + INTERVAL 19 HOUR, '青旅门口集合', 20, 0, 1, 1, 1, 0
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM `hostel_activity` WHERE `title` = '周五城市夜骑');

INSERT IGNORE INTO `hostel_activity` (`id`,`title`,`cover_image`,`description`,`activity_time`,`location`,`max_people`,`joined_people`,`status`,`recommend`,`sort_no`,`deleted`)
SELECT 2002, '周末桌游社交局', 'https://cdn.youngman.example/activity/boardgame.jpg', '在公共活动区开展桌游交流，欢迎单人报名，现场随机组队。', DATE_ADD(CURDATE(), INTERVAL 7 DAY) + INTERVAL 15 HOUR, '一楼公共活动区', 16, 0, 1, 0, 2, 0
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM `hostel_activity` WHERE `title` = '周末桌游社交局');
