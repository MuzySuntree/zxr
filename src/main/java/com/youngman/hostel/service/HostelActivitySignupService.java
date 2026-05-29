package com.youngman.hostel.service;

import com.youngman.hostel.entity.HostelActivity;
import com.youngman.hostel.entity.HostelActivitySignup;

import java.util.List;

public interface HostelActivitySignupService {
    boolean signup(Long activityId, Long userId);
    boolean cancelSignup(Long activityId, Long userId);
    List<HostelActivitySignup> listByActivityId(Long activityId);
    List<HostelActivity> listUserSignedActivities(Long userId);
}
