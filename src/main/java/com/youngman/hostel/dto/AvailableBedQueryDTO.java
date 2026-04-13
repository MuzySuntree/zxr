package com.youngman.hostel.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 可用床位查询参数
 */
@Data
public class AvailableBedQueryDTO {

    /** 入住日期 */
    private LocalDate checkInDate;

    /** 退房日期 */
    private LocalDate checkOutDate;

    /** 用户性别：1-男，2-女 */
    private Integer userGender;

    /** 可选：指定房间ID */
    private Long roomId;
}
