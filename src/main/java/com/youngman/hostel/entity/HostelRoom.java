package com.youngman.hostel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("hostel_room")
public class HostelRoom {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String roomNo;
    private String roomName;
    /** 房间图片URL（管理员上传维护） */
    private String roomImage;
    private Integer roomType;
    private Integer genderType;
    private BigDecimal price;
    private Integer maxBedCount;
    private Integer status;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
