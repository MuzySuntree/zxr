-- 青年旅社管理系统数据库脚本
-- 技术栈：Spring Boot + MyBatis-Plus + MySQL 8
-- 执行前请确认 MySQL 版本 >= 8.0

-- 1) 创建数据库
CREATE DATABASE IF NOT EXISTS `youngman`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_0900_ai_ci;

-- 2) 使用数据库
USE `youngman`;

-- 3) 删除旧表（注意外键依赖顺序）
DROP TABLE IF EXISTS `notification_record`;
DROP TABLE IF EXISTS `booking_order`;
DROP TABLE IF EXISTS `bed`;
DROP TABLE IF EXISTS `room`;
DROP TABLE IF EXISTS `sys_user`;

-- 4) 创建表

-- 用户表
CREATE TABLE `sys_user` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名，系统唯一',
  `password` VARCHAR(255) NOT NULL COMMENT '登录密码（建议存储加密哈希）',
  `real_name` VARCHAR(50) NOT NULL COMMENT '真实姓名',
  `gender` TINYINT NOT NULL COMMENT '性别：1-男，2-女，0-未知',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `role` TINYINT NOT NULL DEFAULT 2 COMMENT '角色：1-管理员，2-普通用户',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '账号状态：1-启用，0-禁用',
  `last_login_time` DATETIME DEFAULT NULL COMMENT '最后登录时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_user_username` (`username`),
  UNIQUE KEY `uk_sys_user_phone` (`phone`),
  KEY `idx_sys_user_role` (`role`),
  KEY `idx_sys_user_gender` (`gender`),
  KEY `idx_sys_user_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户表';

-- 房间表
CREATE TABLE `room` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `room_no` VARCHAR(30) NOT NULL COMMENT '房间编号，如A101',
  `room_name` VARCHAR(100) NOT NULL COMMENT '房间名称',
  `room_type` TINYINT NOT NULL COMMENT '房型：1-四人间，2-六人间，3-八人间，9-其他',
  `gender_type` TINYINT NOT NULL COMMENT '房间性别限制：1-男，2-女，3-混合',
  `price` DECIMAL(10,2) NOT NULL COMMENT '床位单价（每晚）',
  `max_bed_count` INT NOT NULL COMMENT '最大床位数（冗余字段，便于展示和校验）',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '房间状态：1-可用，0-停用',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '房间描述',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_room_room_no` (`room_no`),
  KEY `idx_room_gender_type` (`gender_type`),
  KEY `idx_room_status` (`status`),
  KEY `idx_room_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='房间表';

-- 床位表
CREATE TABLE `bed` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `bed_no` VARCHAR(30) NOT NULL COMMENT '床位编号，如A101-01',
  `room_id` BIGINT UNSIGNED NOT NULL COMMENT '所属房间ID',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '床位状态：1-可用，0-停用',
  `sort_no` INT NOT NULL DEFAULT 0 COMMENT '排序号（用于前端展示）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_bed_bed_no` (`bed_no`),
  KEY `idx_bed_room_id` (`room_id`),
  KEY `idx_bed_status` (`status`),
  KEY `idx_bed_deleted` (`deleted`),
  CONSTRAINT `fk_bed_room_id` FOREIGN KEY (`room_id`) REFERENCES `room` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='床位表';

-- 订单表（避免使用保留字 order）
CREATE TABLE `booking_order` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_no` VARCHAR(50) NOT NULL COMMENT '订单号，系统唯一',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '下单用户ID',
  `room_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '分配房间ID（支付前可为空）',
  `bed_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '分配床位ID（支付前可为空）',
  `check_in_date` DATETIME NOT NULL COMMENT '入住日期时间',
  `check_out_date` DATETIME NOT NULL COMMENT '退房日期时间',
  `order_status` TINYINT NOT NULL COMMENT '订单状态：1-待支付，2-已支付，3-已分配，4-已入住，5-已完成，6-已取消',
  `amount` DECIMAL(10,2) NOT NULL COMMENT '订单金额',
  `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间（模拟支付）',
  `assign_time` DATETIME DEFAULT NULL COMMENT '分配床位时间',
  `cancel_time` DATETIME DEFAULT NULL COMMENT '取消时间',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_booking_order_order_no` (`order_no`),
  KEY `idx_booking_order_user_id` (`user_id`),
  KEY `idx_booking_order_room_id` (`room_id`),
  KEY `idx_booking_order_bed_id` (`bed_id`),
  KEY `idx_booking_order_status` (`order_status`),
  KEY `idx_booking_order_check_in` (`check_in_date`),
  KEY `idx_booking_order_check_out` (`check_out_date`),
  KEY `idx_booking_order_check_range` (`check_in_date`, `check_out_date`),
  KEY `idx_booking_order_bed_status_range` (`bed_id`, `order_status`, `check_in_date`, `check_out_date`),
  KEY `idx_booking_order_room_status_range` (`room_id`, `order_status`, `check_in_date`, `check_out_date`),
  KEY `idx_booking_order_deleted` (`deleted`),
  CONSTRAINT `fk_booking_order_user_id` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT `fk_booking_order_room_id` FOREIGN KEY (`room_id`) REFERENCES `room` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT `fk_booking_order_bed_id` FOREIGN KEY (`bed_id`) REFERENCES `bed` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT `chk_booking_order_time` CHECK (`check_out_date` > `check_in_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订房订单表';

-- 通知记录表（轻量设计，用于站内通知）
CREATE TABLE `notification_record` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '接收通知用户ID',
  `order_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '关联订单ID，可为空',
  `title` VARCHAR(100) NOT NULL COMMENT '通知标题',
  `content` VARCHAR(2000) NOT NULL COMMENT '通知内容（可包含分配结果）',
  `is_read` TINYINT NOT NULL DEFAULT 0 COMMENT '是否已读：0-未读，1-已读',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_notification_user_id` (`user_id`),
  KEY `idx_notification_order_id` (`order_id`),
  KEY `idx_notification_is_read` (`is_read`),
  KEY `idx_notification_create_time` (`create_time`),
  CONSTRAINT `fk_notification_user_id` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON UPDATE CASCADE ON DELETE RESTRICT,
  CONSTRAINT `fk_notification_order_id` FOREIGN KEY (`order_id`) REFERENCES `booking_order` (`id`) ON UPDATE CASCADE ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='通知记录表';

-- 5) 索引语句（上面的 CREATE TABLE 已包含全部核心索引）
-- 如需单独维护索引，可按需 ALTER TABLE ADD INDEX。

-- 6) 外键说明
-- 本方案采用“适度强外键”：核心关联（用户-订单、房间-床位、订单-床位/房间、通知-用户/订单）保留外键，
-- 以确保毕业设计阶段数据一致性；删除业务数据时建议使用逻辑删除（deleted），避免物理删除触发外键冲突。

-- 7) 测试数据

-- 用户（2个管理员/普通用户）
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `gender`, `phone`, `role`, `status`)
VALUES
('admin', '$2a$10$abcdefghijklmnopqrstuv', '系统管理员', 1, '13800000001', 1, 1),
('alice', '$2a$10$uvwxyzabcdefghijklmnopqr', 'Alice', 2, '13800000002', 2, 1),
('bob',   '$2a$10$qwertyuiopasdfghjklzxcv', 'Bob',   1, '13800000003', 2, 1);

-- 房间（3个）
INSERT INTO `room` (`room_no`, `room_name`, `room_type`, `gender_type`, `price`, `max_bed_count`, `status`, `description`)
VALUES
('A101', '阳光男生四人间', 1, 1, 89.00, 4, 1, '靠窗，通风好'),
('A201', '海风女生六人间', 2, 2, 79.00, 6, 1, '安静楼层'),
('B301', '城市混住八人间', 3, 3, 69.00, 8, 1, '公共区域较大');

-- 每个房间若干床位
INSERT INTO `bed` (`bed_no`, `room_id`, `status`, `sort_no`)
VALUES
('A101-01', 1, 1, 1), ('A101-02', 1, 1, 2), ('A101-03', 1, 1, 3), ('A101-04', 1, 1, 4),
('A201-01', 2, 1, 1), ('A201-02', 2, 1, 2), ('A201-03', 2, 1, 3), ('A201-04', 2, 1, 4), ('A201-05', 2, 1, 5), ('A201-06', 2, 1, 6),
('B301-01', 3, 1, 1), ('B301-02', 3, 1, 2), ('B301-03', 3, 1, 3), ('B301-04', 3, 1, 4), ('B301-05', 3, 1, 5), ('B301-06', 3, 1, 6), ('B301-07', 3, 1, 7), ('B301-08', 3, 1, 8);

-- 订单测试数据
INSERT INTO `booking_order`
(`order_no`, `user_id`, `room_id`, `bed_id`, `check_in_date`, `check_out_date`, `order_status`, `amount`, `pay_time`, `assign_time`, `remark`)
VALUES
-- alice 下单，待支付（未分配）
('BO202604130001', 2, NULL, NULL, '2026-04-20 14:00:00', '2026-04-22 12:00:00', 1, 178.00, NULL, NULL, '首次下单待支付'),
-- bob 已支付且已分配到男生间
('BO202604130002', 3, 1, 1, '2026-04-20 14:00:00', '2026-04-23 12:00:00', 3, 267.00, '2026-04-13 10:00:00', '2026-04-13 10:01:00', '已自动分配床位'),
-- alice 已支付，待分配（用于测试支付后分配流程）
('BO202604130003', 2, NULL, NULL, '2026-04-21 14:00:00', '2026-04-24 12:00:00', 2, 237.00, '2026-04-13 11:00:00', NULL, '支付完成待分配'),
-- bob 已取消
('BO202604130004', 3, NULL, NULL, '2026-04-25 14:00:00', '2026-04-26 12:00:00', 6, 89.00, NULL, NULL, '用户取消');

-- 通知测试数据（包含分配结果信息）
INSERT INTO `notification_record` (`user_id`, `order_id`, `title`, `content`, `is_read`)
VALUES
(3, 2, '床位分配成功通知',
 '您的订单 BO202604130002 已分配成功：房间 A101（阳光男生四人间），床位 A101-01；当前房间已入住人数 1，性别统计：男 1，女 0。',
 0);

-- ------------------------------
-- 关键查询示例（可选）
-- ------------------------------

-- A. 判断某床位在目标时间段是否冲突（冲突返回记录）
-- 目标时间段 [start_time, end_time) 与已占用区间重叠条件：
-- existing.check_in_date < end_time AND existing.check_out_date > start_time
-- 且订单状态属于会占床的状态（2已支付、3已分配、4已入住、5已完成*可按业务调整）
/*
SELECT bo.id, bo.order_no
FROM booking_order bo
WHERE bo.bed_id = 1
  AND bo.deleted = 0
  AND bo.order_status IN (2,3,4,5)
  AND bo.check_in_date < '2026-04-22 12:00:00'
  AND bo.check_out_date > '2026-04-20 14:00:00';
*/

-- B. 查询目标时间段可分配床位（示例：男性用户）
/*
SELECT b.id AS bed_id, b.bed_no, r.id AS room_id, r.room_no, r.room_name, r.gender_type, r.price
FROM bed b
JOIN room r ON r.id = b.room_id
WHERE b.deleted = 0
  AND b.status = 1
  AND r.deleted = 0
  AND r.status = 1
  AND r.gender_type IN (1,3)
  AND NOT EXISTS (
      SELECT 1
      FROM booking_order bo
      WHERE bo.bed_id = b.id
        AND bo.deleted = 0
        AND bo.order_status IN (2,3,4,5)
        AND bo.check_in_date < '2026-04-22 12:00:00'
        AND bo.check_out_date > '2026-04-20 14:00:00'
  )
ORDER BY r.price ASC, r.id ASC, b.sort_no ASC;
*/

-- C. 分配后通知所需统计：某房间当前入住人数与性别统计
/*
SELECT
  bo.room_id,
  COUNT(*) AS occupied_count,
  SUM(CASE WHEN u.gender = 1 THEN 1 ELSE 0 END) AS male_count,
  SUM(CASE WHEN u.gender = 2 THEN 1 ELSE 0 END) AS female_count
FROM booking_order bo
JOIN sys_user u ON u.id = bo.user_id
WHERE bo.room_id = 1
  AND bo.deleted = 0
  AND bo.order_status IN (3,4)
  AND bo.check_in_date < NOW()
  AND bo.check_out_date > NOW();
*/
