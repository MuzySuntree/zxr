-- --------------------------------------------------------
-- 主机:                           127.0.0.1
-- 服务器版本:                        8.0.12 - MySQL Community Server - GPL
-- 服务器操作系统:                      Win64
-- HeidiSQL 版本:                  11.0.0.5919
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- 导出 youngman 的数据库结构
CREATE DATABASE IF NOT EXISTS `youngman` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci */;
USE `youngman`;

-- 导出  表 youngman.booking_notice 结构
CREATE TABLE IF NOT EXISTS `booking_notice` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) unsigned NOT NULL COMMENT '接收通知用户ID',
  `order_id` bigint(20) unsigned DEFAULT NULL COMMENT '关联订单ID，可为空',
  `title` varchar(100) NOT NULL COMMENT '通知标题',
  `content` varchar(2000) NOT NULL COMMENT '通知内容（可包含分配结果）',
  `is_read` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否已读：0-未读，1-已读',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_notification_user_id` (`user_id`),
  KEY `idx_notification_order_id` (`order_id`),
  KEY `idx_notification_is_read` (`is_read`),
  KEY `idx_notification_create_time` (`create_time`),
  CONSTRAINT `fk_notification_order_id` FOREIGN KEY (`order_id`) REFERENCES `booking_order` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_notification_user_id` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='通知记录表';

-- 正在导出表  youngman.booking_notice 的数据：~3 rows (大约)
DELETE FROM `booking_notice`;
/*!40000 ALTER TABLE `booking_notice` DISABLE KEYS */;
INSERT INTO `booking_notice` (`id`, `user_id`, `order_id`, `title`, `content`, `is_read`, `create_time`, `update_time`) VALUES
	(1, 3, 2, '床位分配成功通知', '您的订单 BO202604130002 已分配成功：房间 A101（阳光男生四人间），床位 A101-01；当前房间已入住人数 1，性别统计：男 1，女 0。', 0, '2026-04-13 10:34:01', '2026-04-13 14:58:40'),
	(2, 4, 5, '床位分配通知', '您的订单已成功分配至A101房间 A101-01床位。当前该房间已有1人入住，其中男1人，女0人。', 1, '2026-04-13 15:38:15', '2026-04-13 15:38:55'),
	(3, 2, 3, '床位分配通知', '您的订单已成功分配至A201房间 A201-01床位。当前该房间已有1人入住，其中男0人，女1人。', 0, '2026-04-13 15:41:36', '2026-04-13 15:41:36'),
	(4, 2, 1, '床位分配通知', '您的订单已成功分配至A201房间 A201-02床位。当前该房间已有2人入住，其中男0人，女2人。', 0, '2026-04-13 15:41:45', '2026-04-13 15:41:45'),
	(5, 4, 6, '床位分配通知', '您的订单已成功分配至A101房间 A101-01床位。当前该房间已有1人入住，其中男1人，女0人。', 1, '2026-04-13 16:27:59', '2026-04-13 16:28:19'),
	(6, 8, 7, '床位分配通知', '您的订单已成功分配至A101房间 A101-02床位。当前该房间已有2人入住，其中男2人，女0人。', 1, '2026-04-20 12:32:35', '2026-04-20 16:03:57'),
	(7, 8, 9, '床位分配通知', '您的订单已成功分配至A101房间 A101-02床位。当前该房间已有2人入住，其中男2人，女0人。', 0, '2026-04-20 16:10:54', '2026-04-20 16:10:54'),
	(8, 9, 10, '床位分配通知', '您的订单已成功分配至A101房间 A101-02床位。当前该房间已有2人入住，其中男2人，女0人。', 0, '2026-04-20 16:23:38', '2026-04-20 16:23:38');
/*!40000 ALTER TABLE `booking_notice` ENABLE KEYS */;

-- 导出  表 youngman.booking_order 结构
CREATE TABLE IF NOT EXISTS `booking_order` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_no` varchar(50) NOT NULL COMMENT '订单号，系统唯一',
  `user_id` bigint(20) unsigned NOT NULL COMMENT '下单用户ID',
  `room_id` bigint(20) unsigned DEFAULT NULL COMMENT '分配房间ID（支付前可为空）',
  `bed_id` bigint(20) unsigned DEFAULT NULL COMMENT '分配床位ID（支付前可为空）',
  `check_in_date` datetime NOT NULL COMMENT '入住日期时间',
  `check_out_date` datetime NOT NULL COMMENT '退房日期时间',
  `order_status` tinyint(4) NOT NULL COMMENT '订单状态：1-待支付，2-已支付，3-已分配，4-已入住，5-已完成，6-已取消',
  `amount` decimal(10,2) NOT NULL COMMENT '订单金额',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间（模拟支付）',
  `assign_time` datetime DEFAULT NULL COMMENT '分配床位时间',
  `cancel_time` datetime DEFAULT NULL COMMENT '取消时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_booking_order_order_no` (`order_no`),
  KEY `idx_booking_order_user_id` (`user_id`),
  KEY `idx_booking_order_room_id` (`room_id`),
  KEY `idx_booking_order_bed_id` (`bed_id`),
  KEY `idx_booking_order_status` (`order_status`),
  KEY `idx_booking_order_check_in` (`check_in_date`),
  KEY `idx_booking_order_check_out` (`check_out_date`),
  KEY `idx_booking_order_check_range` (`check_in_date`,`check_out_date`),
  KEY `idx_booking_order_bed_status_range` (`bed_id`,`order_status`,`check_in_date`,`check_out_date`),
  KEY `idx_booking_order_room_status_range` (`room_id`,`order_status`,`check_in_date`,`check_out_date`),
  KEY `idx_booking_order_deleted` (`deleted`),
  CONSTRAINT `fk_booking_order_bed_id` FOREIGN KEY (`bed_id`) REFERENCES `hostel_bed` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `fk_booking_order_room_id` FOREIGN KEY (`room_id`) REFERENCES `hostel_room` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `fk_booking_order_user_id` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订房订单表';

-- 正在导出表  youngman.booking_order 的数据：~10 rows (大约)
DELETE FROM `booking_order`;
/*!40000 ALTER TABLE `booking_order` DISABLE KEYS */;
INSERT INTO `booking_order` (`id`, `order_no`, `user_id`, `room_id`, `bed_id`, `check_in_date`, `check_out_date`, `order_status`, `amount`, `pay_time`, `assign_time`, `cancel_time`, `remark`, `create_time`, `update_time`, `deleted`) VALUES
	(1, 'BO202604130001', 2, 2, 6, '2026-04-20 00:00:00', '2026-04-22 00:00:00', 3, 178.00, '2026-04-13 15:41:45', '2026-04-13 15:41:45', NULL, '首次下单待支付', '2026-04-13 10:34:01', '2026-04-13 15:41:45', 0),
	(2, 'BO202604130002', 3, 1, 1, '2026-04-20 14:00:00', '2026-04-23 12:00:00', 3, 267.00, '2026-04-13 10:00:00', '2026-04-13 10:01:00', NULL, '已自动分配床位', '2026-04-13 10:34:01', '2026-04-13 10:34:01', 0),
	(3, 'BO202604130003', 2, 2, 5, '2026-04-21 00:00:00', '2026-04-24 00:00:00', 3, 237.00, '2026-04-13 11:00:00', '2026-04-13 15:41:36', NULL, '支付完成待分配', '2026-04-13 10:34:01', '2026-04-13 15:41:36', 0),
	(4, 'BO202604130004', 3, 1, 1, '2026-04-25 14:00:00', '2026-04-26 12:00:00', 6, 89.00, '2026-04-20 16:08:13', NULL, NULL, '用户取消', '2026-04-13 10:34:01', '2026-04-20 16:08:14', 0),
	(5, 'BO20260413153802095509', 4, 1, 1, '2026-04-13 00:00:00', '2026-04-14 00:00:00', 6, 69.00, '2026-04-13 15:38:15', '2026-04-13 15:38:15', NULL, '', '2026-04-13 15:38:02', '2026-04-13 15:38:25', 0),
	(6, 'BO20260413162752150241', 4, 1, 1, '2026-04-13 00:00:00', '2026-04-14 00:00:00', 3, 69.00, '2026-04-13 16:27:59', '2026-04-13 16:27:59', NULL, '', '2026-04-13 16:27:52', '2026-04-13 16:27:59', 0),
	(7, 'BO20260420123229329697', 8, 1, 2, '2026-04-20 00:00:00', '2026-04-21 00:00:00', 6, 69.00, '2026-04-20 12:32:35', '2026-04-20 12:32:35', NULL, '', '2026-04-20 12:32:29', '2026-04-20 16:03:47', 0),
	(8, 'BO20260420161027776133', 8, NULL, NULL, '2026-04-23 00:00:00', '2026-04-25 00:00:00', 6, 69.00, NULL, NULL, NULL, '', '2026-04-20 16:10:28', '2026-04-20 16:10:31', 0),
	(9, 'BO20260420161047305344', 8, 1, 2, '2026-04-23 00:00:00', '2026-04-25 00:00:00', 3, 69.00, '2026-04-20 16:10:54', '2026-04-20 16:10:54', NULL, '', '2026-04-20 16:10:47', '2026-04-20 16:10:54', 0),
	(10, 'BO20260420162323339956', 9, 1, 2, '2026-04-21 00:00:00', '2026-04-22 00:00:00', 3, 69.00, '2026-04-20 16:23:38', '2026-04-20 16:23:38', NULL, '', '2026-04-20 16:23:23', '2026-04-20 16:23:38', 0);
/*!40000 ALTER TABLE `booking_order` ENABLE KEYS */;

-- 导出  表 youngman.hostel_bed 结构
CREATE TABLE IF NOT EXISTS `hostel_bed` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `bed_no` varchar(30) NOT NULL COMMENT '床位编号，如A101-01',
  `room_id` bigint(20) unsigned NOT NULL COMMENT '所属房间ID',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '床位状态：1-可用，0-停用',
  `sort_no` int(11) NOT NULL DEFAULT '0' COMMENT '排序号（用于前端展示）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_bed_bed_no` (`bed_no`),
  KEY `idx_bed_room_id` (`room_id`),
  KEY `idx_bed_status` (`status`),
  KEY `idx_bed_deleted` (`deleted`),
  CONSTRAINT `fk_bed_room_id` FOREIGN KEY (`room_id`) REFERENCES `hostel_room` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='床位表';

-- 正在导出表  youngman.hostel_bed 的数据：~19 rows (大约)
DELETE FROM `hostel_bed`;
/*!40000 ALTER TABLE `hostel_bed` DISABLE KEYS */;
INSERT INTO `hostel_bed` (`id`, `bed_no`, `room_id`, `status`, `sort_no`, `create_time`, `update_time`, `deleted`) VALUES
	(1, 'A101-01', 1, 1, 1, '2026-04-13 10:34:01', '2026-04-13 10:34:01', 0),
	(2, 'A101-02', 1, 1, 2, '2026-04-13 10:34:01', '2026-04-13 10:34:01', 0),
	(3, 'A101-03', 1, 1, 3, '2026-04-13 10:34:01', '2026-04-13 10:34:01', 0),
	(4, 'A101-04', 1, 1, 4, '2026-04-13 10:34:01', '2026-04-13 10:34:01', 0),
	(5, 'A201-01', 2, 1, 1, '2026-04-13 10:34:01', '2026-04-13 10:34:01', 0),
	(6, 'A201-02', 2, 1, 2, '2026-04-13 10:34:01', '2026-04-13 10:34:01', 0),
	(7, 'A201-03', 2, 1, 3, '2026-04-13 10:34:01', '2026-04-13 10:34:01', 0),
	(8, 'A201-04', 2, 1, 4, '2026-04-13 10:34:01', '2026-04-13 10:34:01', 0),
	(9, 'A201-05', 2, 1, 5, '2026-04-13 10:34:01', '2026-04-13 10:34:01', 0),
	(10, 'A201-06', 2, 1, 6, '2026-04-13 10:34:01', '2026-04-13 10:34:01', 0),
	(11, 'B301-01', 3, 1, 1, '2026-04-13 10:34:01', '2026-04-13 10:34:01', 0),
	(12, 'B301-02', 3, 1, 2, '2026-04-13 10:34:01', '2026-04-13 10:34:01', 0),
	(13, 'B301-03', 3, 1, 3, '2026-04-13 10:34:01', '2026-04-13 10:34:01', 0),
	(14, 'B301-04', 3, 1, 4, '2026-04-13 10:34:01', '2026-04-13 10:34:01', 0),
	(15, 'B301-05', 3, 1, 5, '2026-04-13 10:34:01', '2026-04-13 10:34:01', 0),
	(16, 'B301-06', 3, 1, 6, '2026-04-13 10:34:01', '2026-04-13 10:34:01', 0),
	(17, 'B301-07', 3, 1, 7, '2026-04-13 10:34:01', '2026-04-13 10:34:01', 0),
	(18, 'B301-08', 3, 1, 8, '2026-04-13 10:34:01', '2026-04-13 10:34:01', 0),
	(19, 'B302-1', 4, 1, 0, '2026-04-20 16:26:40', '2026-04-20 16:26:40', 0);
/*!40000 ALTER TABLE `hostel_bed` ENABLE KEYS */;

-- 导出  表 youngman.hostel_room 结构
CREATE TABLE IF NOT EXISTS `hostel_room` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `room_no` varchar(30) NOT NULL COMMENT '房间编号，如A101',
  `room_name` varchar(100) NOT NULL COMMENT '房间名称',
  `room_type` tinyint(4) NOT NULL COMMENT '房型：1-四人间，2-六人间，3-八人间，9-其他',
  `gender_type` tinyint(4) NOT NULL COMMENT '房间性别限制：1-男，2-女，3-混合',
  `price` decimal(10,2) NOT NULL COMMENT '床位单价（每晚）',
  `max_bed_count` int(11) NOT NULL COMMENT '最大床位数（冗余字段，便于展示和校验）',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '房间状态：1-可用，0-停用',
  `room_image` varchar(500) DEFAULT NULL COMMENT '房间图片URL',
  `description` varchar(500) DEFAULT NULL COMMENT '房间描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_room_room_no` (`room_no`),
  KEY `idx_room_gender_type` (`gender_type`),
  KEY `idx_room_status` (`status`),
  KEY `idx_room_deleted` (`deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='房间表';

-- 正在导出表  youngman.hostel_room 的数据：~4 rows (大约)
DELETE FROM `hostel_room`;
/*!40000 ALTER TABLE `hostel_room` DISABLE KEYS */;
INSERT INTO `hostel_room` (`id`, `room_no`, `room_name`, `room_type`, `gender_type`, `price`, `max_bed_count`, `status`, `room_image`, `description`, `create_time`, `update_time`, `deleted`) VALUES
	(1, 'A101', '阳光男生四人间', 1, 1, 89.00, 4, 1, NULL, '靠窗，通风好', '2026-04-13 10:34:01', '2026-04-13 10:34:01', 0),
	(2, 'A201', '海风女生六人间', 2, 2, 79.00, 6, 1, NULL, '安静楼层', '2026-04-13 10:34:01', '2026-04-13 10:34:01', 0),
	(3, 'B301', '城市混住八人间', 3, 3, 69.00, 8, 1, NULL, '公共区域较大', '2026-04-13 10:34:01', '2026-04-13 10:34:01', 0),
	(4, 'B302', '男生8人房间test', 1, 1, 80.00, 4, 1, '', NULL, '2026-04-20 16:26:04', '2026-04-20 16:26:04', 0);
/*!40000 ALTER TABLE `hostel_room` ENABLE KEYS */;

-- 导出  表 youngman.sys_user 结构
CREATE TABLE IF NOT EXISTS `sys_user` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) NOT NULL COMMENT '用户名，系统唯一',
  `password` varchar(255) NOT NULL COMMENT '登录密码（建议存储加密哈希）',
  `real_name` varchar(50) NOT NULL COMMENT '真实姓名',
  `gender` tinyint(4) NOT NULL COMMENT '性别：1-男，2-女，0-未知',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `role` tinyint(4) NOT NULL DEFAULT '2' COMMENT '角色：1-管理员，2-普通用户',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '账号状态：1-启用，0-禁用',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_user_username` (`username`),
  UNIQUE KEY `uk_sys_user_phone` (`phone`),
  KEY `idx_sys_user_role` (`role`),
  KEY `idx_sys_user_gender` (`gender`),
  KEY `idx_sys_user_deleted` (`deleted`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';

-- 正在导出表  youngman.sys_user 的数据：~0 rows (大约)
DELETE FROM `sys_user`;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` (`id`, `username`, `password`, `real_name`, `gender`, `phone`, `role`, `status`, `last_login_time`, `create_time`, `update_time`, `deleted`) VALUES
	(1, 'admin', '123456', '系统管理员', 1, '13800000001', 1, 1, '2026-05-02 10:03:21', '2026-04-13 10:34:01', '2026-05-02 10:03:21', 0),
	(2, 'alice', '123456', 'Alice', 2, '13800000002', 2, 1, NULL, '2026-04-13 10:34:01', '2026-04-13 15:00:23', 0),
	(3, 'bob', '123456', 'Bob', 1, '13800000003', 2, 1, NULL, '2026-04-13 10:34:01', '2026-04-13 15:00:24', 0),
	(4, 'user3', '123456', 'zxr', 1, '15111111111', 2, 1, '2026-04-13 16:27:34', '2026-04-13 14:55:45', '2026-04-13 16:27:34', 0),
	(7, 'user', '123456', 'asd', 1, '15122222222', 2, 1, NULL, '2026-04-20 12:29:37', '2026-04-20 12:29:37', 0),
	(8, 'zxr', '123456', 'zxr', 1, '123444454555', 2, 1, '2026-04-20 16:45:09', '2026-04-20 12:31:51', '2026-04-20 16:45:09', 0),
	(9, 'zxr1', '123456', '张旭瑞', 1, '13918192829', 2, 1, '2026-04-20 16:43:02', '2026-04-20 16:21:55', '2026-04-20 16:43:02', 0);
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;

-- ========================
-- 2026-05-02 数据库结构扩展
-- ========================

-- 1) 用户注册信息增强（保留既有字段，不破坏旧结构）
ALTER TABLE `sys_user`
  ADD COLUMN `avatar` varchar(500) DEFAULT NULL COMMENT '头像URL' AFTER `phone`,
  ADD COLUMN `email` varchar(100) DEFAULT NULL COMMENT '邮箱' AFTER `avatar`,
  ADD COLUMN `id_card` varchar(30) DEFAULT NULL COMMENT '身份证号' AFTER `email`,
  ADD COLUMN `emergency_contact` varchar(50) DEFAULT NULL COMMENT '紧急联系人' AFTER `id_card`,
  ADD COLUMN `emergency_phone` varchar(20) DEFAULT NULL COMMENT '紧急联系人手机号' AFTER `emergency_contact`,
  ADD COLUMN `remark` varchar(500) DEFAULT NULL COMMENT '个人备注' AFTER `emergency_phone`;

ALTER TABLE `sys_user`
  ADD UNIQUE KEY `uk_sys_user_email` (`email`),
  ADD KEY `idx_sys_user_id_card` (`id_card`),
  ADD KEY `idx_sys_user_emergency_phone` (`emergency_phone`);

-- 2) 注册验证码表
CREATE TABLE IF NOT EXISTS `register_verify_code` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `phone` varchar(20) NOT NULL COMMENT '手机号',
  `code` varchar(10) NOT NULL COMMENT '验证码',
  `expire_time` datetime NOT NULL COMMENT '过期时间',
  `used` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否已使用：0-否，1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_register_verify_code_phone` (`phone`),
  KEY `idx_register_verify_code_expire_time` (`expire_time`),
  KEY `idx_register_verify_code_used` (`used`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='注册验证码表';

-- 3) 用户支付设置表
CREATE TABLE IF NOT EXISTS `user_payment_setting` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) unsigned NOT NULL COMMENT '用户ID',
  `pay_type` tinyint(4) NOT NULL COMMENT '支付类型：1-余额模拟支付，2-微信模拟支付，3-支付宝模拟支付',
  `balance` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '支付余额（用于余额模拟支付）',
  `is_default` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否默认：0-否，1-是',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态：1-启用，0-禁用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_payment_setting_user_id` (`user_id`),
  KEY `idx_user_payment_setting_pay_type` (`pay_type`),
  KEY `idx_user_payment_setting_status` (`status`),
  KEY `idx_user_payment_setting_default` (`is_default`),
  UNIQUE KEY `uk_user_payment_setting_user_pay_type` (`user_id`,`pay_type`),
  CONSTRAINT `fk_user_payment_setting_user_id` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户支付设置表';

-- 4) 首页轮播图表
CREATE TABLE IF NOT EXISTS `home_banner` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(100) NOT NULL COMMENT '主标题',
  `subtitle` varchar(255) DEFAULT NULL COMMENT '副标题',
  `image_url` varchar(500) NOT NULL COMMENT '图片URL（酒店外观/大堂/公共区域等）',
  `link_url` varchar(500) DEFAULT NULL COMMENT '跳转链接',
  `sort_no` int(11) NOT NULL DEFAULT '0' COMMENT '排序号（值越小越靠前）',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态：1-启用，0-禁用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_home_banner_status` (`status`),
  KEY `idx_home_banner_sort_no` (`sort_no`),
  KEY `idx_home_banner_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='首页轮播图表';

-- 5) 留言板表
CREATE TABLE IF NOT EXISTS `message_board` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) unsigned NOT NULL COMMENT '发布用户ID',
  `category` tinyint(4) NOT NULL COMMENT '分类：1-失物招领，2-寻找搭子，3-入住交流，4-建议反馈，9-其他',
  `title` varchar(200) NOT NULL COMMENT '标题',
  `content` varchar(4000) NOT NULL COMMENT '正文内容',
  `contact_info` varchar(200) DEFAULT NULL COMMENT '联系方式',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态：0-待审核，1-已发布，2-已下架',
  `view_count` int(11) NOT NULL DEFAULT '0' COMMENT '浏览次数',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_message_board_user_id` (`user_id`),
  KEY `idx_message_board_category` (`category`),
  KEY `idx_message_board_status` (`status`),
  KEY `idx_message_board_create_time` (`create_time`),
  KEY `idx_message_board_deleted` (`deleted`),
  CONSTRAINT `fk_message_board_user_id` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='留言板表';

-- 6) 活动管理表
CREATE TABLE IF NOT EXISTS `hostel_activity` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` varchar(200) NOT NULL COMMENT '活动标题',
  `cover_image` varchar(500) DEFAULT NULL COMMENT '封面图URL',
  `description` varchar(4000) DEFAULT NULL COMMENT '活动描述',
  `activity_time` datetime NOT NULL COMMENT '活动时间',
  `location` varchar(255) NOT NULL COMMENT '活动地点',
  `max_people` int(11) NOT NULL DEFAULT '0' COMMENT '活动人数上限',
  `joined_people` int(11) NOT NULL DEFAULT '0' COMMENT '已报名人数',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态：0-未发布，1-报名中，2-已结束，3-已取消',
  `recommend` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否推荐：0-否，1-是',
  `sort_no` int(11) NOT NULL DEFAULT '0' COMMENT '排序号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_hostel_activity_status` (`status`),
  KEY `idx_hostel_activity_recommend` (`recommend`),
  KEY `idx_hostel_activity_activity_time` (`activity_time`),
  KEY `idx_hostel_activity_sort_no` (`sort_no`),
  KEY `idx_hostel_activity_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='青旅活动表';

-- 7) 活动报名表
CREATE TABLE IF NOT EXISTS `hostel_activity_signup` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `activity_id` bigint(20) unsigned NOT NULL COMMENT '活动ID',
  `user_id` bigint(20) unsigned NOT NULL COMMENT '报名用户ID',
  `signup_status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '报名状态：1-已报名，2-已取消',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_activity_signup_activity_user` (`activity_id`,`user_id`),
  KEY `idx_activity_signup_user_id` (`user_id`),
  KEY `idx_activity_signup_status` (`signup_status`),
  CONSTRAINT `fk_activity_signup_activity_id` FOREIGN KEY (`activity_id`) REFERENCES `hostel_activity` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `fk_activity_signup_user_id` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='活动报名表';

-- 8) 客服会话表（轮询版）
CREATE TABLE IF NOT EXISTS `customer_chat_session` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) unsigned NOT NULL COMMENT '用户ID',
  `admin_id` bigint(20) unsigned DEFAULT NULL COMMENT '客服管理员ID',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '会话状态：1-进行中，2-已结束',
  `last_message` varchar(2000) DEFAULT NULL COMMENT '最后一条消息内容',
  `last_message_time` datetime DEFAULT NULL COMMENT '最后消息时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_customer_chat_session_user_id` (`user_id`),
  KEY `idx_customer_chat_session_admin_id` (`admin_id`),
  KEY `idx_customer_chat_session_status` (`status`),
  KEY `idx_customer_chat_session_last_message_time` (`last_message_time`),
  CONSTRAINT `fk_customer_chat_session_user_id` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `fk_customer_chat_session_admin_id` FOREIGN KEY (`admin_id`) REFERENCES `sys_user` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='客服会话表';

-- 8) 客服消息表（轮询版）
CREATE TABLE IF NOT EXISTS `customer_chat_message` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `session_id` bigint(20) unsigned NOT NULL COMMENT '会话ID',
  `sender_id` bigint(20) unsigned NOT NULL COMMENT '发送者ID',
  `sender_role` tinyint(4) NOT NULL COMMENT '发送者角色：1-用户，2-管理员',
  `content` varchar(4000) NOT NULL COMMENT '消息内容',
  `is_read` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否已读：0-未读，1-已读',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_customer_chat_message_session_id` (`session_id`),
  KEY `idx_customer_chat_message_sender_id` (`sender_id`),
  KEY `idx_customer_chat_message_is_read` (`is_read`),
  KEY `idx_customer_chat_message_create_time` (`create_time`),
  CONSTRAINT `fk_customer_chat_message_session_id` FOREIGN KEY (`session_id`) REFERENCES `customer_chat_session` (`id`) ON UPDATE CASCADE,
  CONSTRAINT `fk_customer_chat_message_sender_id` FOREIGN KEY (`sender_id`) REFERENCES `sys_user` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='客服消息表';

-- 初始化数据：首页轮播图
INSERT INTO `home_banner` (`title`, `subtitle`, `image_url`, `link_url`, `sort_no`, `status`, `create_time`, `update_time`, `deleted`) VALUES
('城市青年旅社外观', '地铁步行5分钟，交通便利', 'https://cdn.youngman.example/banner/hostel-exterior.jpg', '/user/home', 1, 1, NOW(), NOW(), 0),
('大堂公共休息区', '24小时自助前台与咖啡吧', 'https://cdn.youngman.example/banner/lobby.jpg', '/user/home', 2, 1, NOW(), NOW(), 0),
('共享厨房与活动区', '开放式厨房、桌游与社交空间', 'https://cdn.youngman.example/banner/public-area.jpg', '/user/home', 3, 1, NOW(), NOW(), 0);

-- 初始化数据：活动信息
INSERT INTO `hostel_activity` (`title`, `cover_image`, `description`, `activity_time`, `location`, `max_people`, `joined_people`, `status`, `recommend`, `sort_no`, `create_time`, `update_time`, `deleted`) VALUES
('周五城市夜骑', 'https://cdn.youngman.example/activity/night-ride.jpg', '由青旅志愿者带队，沿江夜骑约12公里，适合新入住旅客快速熟悉城市。', DATE_ADD(CURDATE(), INTERVAL 5 DAY) + INTERVAL 19 HOUR, '青旅门口集合', 20, 0, 1, 1, 1, NOW(), NOW(), 0),
('周末桌游社交局', 'https://cdn.youngman.example/activity/boardgame.jpg', '在公共活动区开展桌游交流，欢迎单人报名，现场随机组队。', DATE_ADD(CURDATE(), INTERVAL 7 DAY) + INTERVAL 15 HOUR, '一楼公共活动区', 16, 0, 1, 0, 2, NOW(), NOW(), 0);
