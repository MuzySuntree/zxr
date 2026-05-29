package com.youngman.hostel.service;
import com.youngman.hostel.entity.HostelActivity;
import java.util.List;
public interface HostelActivityService {
    List<HostelActivity> listRecommend();
    List<HostelActivity> listAll();
    Long save(HostelActivity activity);
    boolean update(HostelActivity activity);
    boolean delete(Long id);
    boolean setRecommend(Long id, Integer recommend);
}
