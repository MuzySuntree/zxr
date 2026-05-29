-- youngman 完整重建版 SQL（适用于删除数据库后全量导入）
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP DATABASE IF EXISTS `youngman`;
CREATE DATABASE `youngman` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `youngman`;

CREATE TABLE `sys_user` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) NOT NULL COMMENT '用户名，系统唯一',
  `password` varchar(255) NOT NULL COMMENT '登录密码',
  `real_name` varchar(50) NOT NULL COMMENT '真实姓名',
  `gender` tinyint NOT NULL COMMENT '性别：1-男，2-女，0-未知',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(500) DEFAULT NULL COMMENT '头像URL',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `id_card` varchar(30) DEFAULT NULL COMMENT '身份证号',
  `emergency_contact` varchar(50) DEFAULT NULL COMMENT '紧急联系人',
  `emergency_phone` varchar(20) DEFAULT NULL COMMENT '紧急联系人手机号',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `role` tinyint NOT NULL DEFAULT '2' COMMENT '角色：1-管理员，2-普通用户',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '账号状态：1-启用，0-禁用',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_user_username` (`username`),
  UNIQUE KEY `uk_sys_user_phone` (`phone`),
  UNIQUE KEY `uk_sys_user_email` (`email`),
  KEY `idx_sys_user_role` (`role`),
  KEY `idx_sys_user_gender` (`gender`),
  KEY `idx_sys_user_deleted` (`deleted`),
  KEY `idx_sys_user_id_card` (`id_card`),
  KEY `idx_sys_user_emergency_phone` (`emergency_phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';

CREATE TABLE `hostel_room` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `room_no` varchar(30) NOT NULL,
  `room_name` varchar(100) NOT NULL,
  `room_type` tinyint NOT NULL,
  `gender_type` tinyint NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `max_bed_count` int NOT NULL,
  `status` tinyint NOT NULL DEFAULT '1',
  `room_image` varchar(500) DEFAULT NULL,
  `description` varchar(500) DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_room_room_no` (`room_no`),
  KEY `idx_room_gender_type` (`gender_type`),
  KEY `idx_room_status` (`status`),
  KEY `idx_room_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='房间表';

CREATE TABLE `hostel_bed` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `bed_no` varchar(30) NOT NULL,
  `room_id` bigint unsigned NOT NULL,
  `status` tinyint NOT NULL DEFAULT '1',
  `sort_no` int NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_bed_bed_no` (`bed_no`),
  KEY `idx_bed_room_id` (`room_id`),
  KEY `idx_bed_status` (`status`),
  KEY `idx_bed_deleted` (`deleted`),
  CONSTRAINT `fk_bed_room_id` FOREIGN KEY (`room_id`) REFERENCES `hostel_room` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='床位表';

CREATE TABLE `booking_order` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `order_no` varchar(50) NOT NULL,
  `user_id` bigint unsigned NOT NULL,
  `room_id` bigint unsigned DEFAULT NULL,
  `bed_id` bigint unsigned DEFAULT NULL,
  `check_in_date` datetime NOT NULL,
  `check_out_date` datetime NOT NULL,
  `order_status` tinyint NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `pay_time` datetime DEFAULT NULL,
  `assign_time` datetime DEFAULT NULL,
  `cancel_time` datetime DEFAULT NULL,
  `remark` varchar(500) DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_booking_order_order_no` (`order_no`),
  KEY `idx_booking_order_user_id` (`user_id`),
  KEY `idx_booking_order_room_id` (`room_id`),
  KEY `idx_booking_order_bed_id` (`bed_id`),
  CONSTRAINT `fk_booking_order_user_id` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `fk_booking_order_room_id` FOREIGN KEY (`room_id`) REFERENCES `hostel_room` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `fk_booking_order_bed_id` FOREIGN KEY (`bed_id`) REFERENCES `hostel_bed` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订房订单表';

CREATE TABLE `booking_notice` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint unsigned NOT NULL,
  `order_id` bigint unsigned DEFAULT NULL,
  `title` varchar(100) NOT NULL,
  `content` varchar(2000) NOT NULL,
  `is_read` tinyint NOT NULL DEFAULT '0',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_notification_user_id` (`user_id`),
  KEY `idx_notification_order_id` (`order_id`),
  CONSTRAINT `fk_notification_user_id` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `fk_notification_order_id` FOREIGN KEY (`order_id`) REFERENCES `booking_order` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='通知记录表';

CREATE TABLE `register_verify_code` (
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

CREATE TABLE `user_payment_setting` (
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

CREATE TABLE `home_banner` (
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

CREATE TABLE `message_board` (
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

CREATE TABLE `hostel_activity` (
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

CREATE TABLE `hostel_activity_signup` (
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

CREATE TABLE `customer_chat_session` (
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

CREATE TABLE `customer_chat_message` (
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

INSERT INTO `home_banner` (`title`, `subtitle`, `image_url`, `link_url`, `sort_no`, `status`, `deleted`) VALUES
('城市青年旅社外观', '地铁步行5分钟，交通便利', 'https://cdn.youngman.example/banner/hostel-exterior.jpg', '/user/home', 1, 1, 0),
('大堂公共休息区', '24小时自助前台与咖啡吧', 'https://cdn.youngman.example/banner/lobby.jpg', '/user/home', 2, 1, 0),
('共享厨房与活动区', '开放式厨房、桌游与社交空间', 'https://cdn.youngman.example/banner/public-area.jpg', '/user/home', 3, 1, 0);

INSERT INTO `hostel_activity` (`title`, `cover_image`, `description`, `activity_time`, `location`, `max_people`, `joined_people`, `status`, `recommend`, `sort_no`, `deleted`) VALUES
('周五城市夜骑', 'https://cdn.youngman.example/activity/night-ride.jpg', '由青旅志愿者带队，沿江夜骑约12公里，适合新入住旅客快速熟悉城市。', DATE_ADD(CURDATE(), INTERVAL 5 DAY) + INTERVAL 19 HOUR, '青旅门口集合', 20, 0, 1, 1, 1, 0),
('周末桌游社交局', 'https://cdn.youngman.example/activity/boardgame.jpg', '在公共活动区开展桌游交流，欢迎单人报名，现场随机组队。', DATE_ADD(CURDATE(), INTERVAL 7 DAY) + INTERVAL 15 HOUR, '一楼公共活动区', 16, 0, 1, 0, 2, 0);

SET FOREIGN_KEY_CHECKS = 1;
