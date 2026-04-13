package com.youngman.hostel.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 可用床位信息
 */
@Data
public class HostelBedAvailableVO {

    private Long bedId;
    private String bedNo;
    private Long roomId;
    private String roomNo;
    private String roomName;
    private Integer roomType;
    private Integer genderType;
    private BigDecimal price;
}
