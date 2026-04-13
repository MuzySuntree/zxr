package com.youngman.hostel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 订房订单实体
 */
@Data
@TableName("booking_order")
public class BookingOrder {

    /** 主键ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 订单号 */
    private String orderNo;

    /** 用户ID */
    private Long userId;

    /** 分配房间ID（未分配可为空） */
    private Long roomId;

    /** 分配床位ID（未分配可为空） */
    private Long bedId;

    /** 入住日期 */
    private LocalDate checkInDate;

    /** 退房日期 */
    private LocalDate checkOutDate;

    /** 订单状态：1-待支付，2-已支付，3-已分配，4-已入住，5-已完成，6-已取消 */
    private Integer orderStatus;

    /** 订单金额 */
    private BigDecimal amount;

    /** 支付时间 */
    private LocalDateTime payTime;

    /** 分配时间 */
    private LocalDateTime assignTime;

    /** 取消时间 */
    private LocalDateTime cancelTime;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 逻辑删除：0-未删除，1-已删除 */
    @TableLogic
    private Integer deleted;
}
