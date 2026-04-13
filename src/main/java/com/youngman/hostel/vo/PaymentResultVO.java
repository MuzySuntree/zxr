package com.youngman.hostel.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 模拟支付结果
 */
@Data
public class PaymentResultVO {

    /** 订单ID */
    private Long orderId;

    /** 订单号 */
    private String orderNo;

    /** 订单状态：2-已支付 */
    private Integer orderStatus;

    /** 支付时间 */
    private LocalDateTime payTime;

    /** 结果说明 */
    private String message;
}
