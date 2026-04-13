package com.youngman.hostel.vo;

import lombok.Data;

/**
 * 房间入住与性别统计
 */
@Data
public class RoomOccupancyGenderVO {

    private Long roomId;
    private String roomNo;
    private String roomName;
    private Integer occupiedCount;
    private Integer maleCount;
    private Integer femaleCount;
}
