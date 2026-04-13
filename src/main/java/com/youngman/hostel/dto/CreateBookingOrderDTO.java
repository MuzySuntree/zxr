package com.youngman.hostel.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 创建订房订单参数
 */
@Data
public class CreateBookingOrderDTO {

    /** 下单用户ID */
    private Long userId;

    /** 入住日期 */
    private LocalDate checkInDate;

    /** 退房日期 */
    private LocalDate checkOutDate;

    /** 用户性别，可选（为空时可由 userId 反查） */
    private Integer userGender;

    /** 备注 */
    private String remark;
}
