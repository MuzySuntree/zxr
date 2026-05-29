package com.youngman.hostel.service;
import com.youngman.hostel.entity.HomeBanner;
import java.util.List;
public interface HomeBannerService {
    List<HomeBanner> listEnabled();
    List<HomeBanner> listAll();
    Long save(HomeBanner banner);
    boolean update(HomeBanner banner);
    boolean delete(Long id);
}
