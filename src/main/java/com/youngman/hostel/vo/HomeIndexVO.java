package com.youngman.hostel.vo;

import com.youngman.hostel.entity.HomeBanner;
import com.youngman.hostel.entity.HostelActivity;
import com.youngman.hostel.entity.MessageBoard;
import lombok.Data;

import java.util.List;

@Data
public class HomeIndexVO {
    private List<HomeBanner> banners;
    private List<HostelActivity> recommendActivities;
    private List<MessageBoard> latestMessages;
}
