-- 为 hostel_room 增加房间图片字段（管理员维护）
ALTER TABLE hostel_room
ADD COLUMN room_image VARCHAR(500) NULL COMMENT '房间图片URL或Base64' AFTER room_name;
