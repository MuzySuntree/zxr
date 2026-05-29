package com.youngman.hostel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("hostel_activity")
public class HostelActivity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String title;
    private String coverImage;
    private String description;
    private LocalDateTime activityTime;
    private String location;
    private Integer maxPeople;
    private Integer joinedPeople;
    private Integer status;
    private Integer recommend;
    private Integer sortNo;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
