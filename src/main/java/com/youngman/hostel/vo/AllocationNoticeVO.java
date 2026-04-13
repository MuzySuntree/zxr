package com.youngman.hostel.vo;

import lombok.Data;

/**
 * 床位分配通知视图
 */
@Data
public class AllocationNoticeVO {

    /** 订单ID */
    private Long orderId;

    /** 订单号 */
    private String orderNo;

    /** 房间ID */
    private Long roomId;

    /** 房间号 */
    private String roomNo;

    /** 房间名称 */
    private String roomName;

    /** 床位ID */
    private Long bedId;

    /** 床位号 */
    private String bedNo;

    /** 当前房间已入住人数 */
    private Integer occupiedCount;

    /** 当前房间男性人数 */
    private Integer maleCount;

    /** 当前房间女性人数 */
    private Integer femaleCount;

    /** 通知消息 */
    private String message;
}
