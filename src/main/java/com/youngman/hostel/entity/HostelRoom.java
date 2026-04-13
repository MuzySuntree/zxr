package com.youngman.hostel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 青年旅社房间实体
 */
@Data
@TableName("hostel_room")
public class HostelRoom {

    /** 主键ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 房间编号 */
    private String roomNo;

    /** 房间名称 */
    private String roomName;

    /** 房型：1-四人间，2-六人间，3-八人间，9-其他 */
    private Integer roomType;

    /** 性别限制：1-男，2-女，3-混合 */
    private Integer genderType;

    /** 床位单价 */
    private BigDecimal price;

    /** 最大床位数 */
    private Integer maxBedCount;

    /** 状态：1-可用，0-停用 */
    private Integer status;

    /** 描述 */
    private String description;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 逻辑删除：0-未删除，1-已删除 */
    @TableLogic
    private Integer deleted;
}
