package com.youngman.hostel.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentResultVO {

    private Long orderId;

    private String orderNo;

    private Integer orderStatus;

    private LocalDateTime payTime;

    private Integer payType;

    private String payTypeName;

    private BigDecimal paidAmount;

    private String message;
}
