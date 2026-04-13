package com.youngman.hostel.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 订单详情
 */
@Data
public class BookingOrderDetailVO {

    private Long orderId;
    private String orderNo;
    private Long userId;
    private String username;
    private Long roomId;
    private String roomNo;
    private Long bedId;
    private String bedNo;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer orderStatus;
    private BigDecimal amount;
    private LocalDateTime payTime;
    private LocalDateTime assignTime;
    private String remark;
}
