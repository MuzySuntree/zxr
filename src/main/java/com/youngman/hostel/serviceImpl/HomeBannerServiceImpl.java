package com.youngman.hostel.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youngman.hostel.entity.HomeBanner;
import com.youngman.hostel.mapper.HomeBannerMapper;
import com.youngman.hostel.service.HomeBannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeBannerServiceImpl implements HomeBannerService {
    private final HomeBannerMapper homeBannerMapper;
    @Override
    public List<HomeBanner> listEnabled() {
        return homeBannerMapper.selectList(new LambdaQueryWrapper<HomeBanner>()
                .eq(HomeBanner::getStatus,1).eq(HomeBanner::getDeleted,0).orderByAsc(HomeBanner::getSortNo));
    }
    @Override
    public List<HomeBanner> listAll() { return homeBannerMapper.selectList(new LambdaQueryWrapper<HomeBanner>().eq(HomeBanner::getDeleted,0).orderByAsc(HomeBanner::getSortNo)); }
    @Override
    public Long save(HomeBanner banner) {
        if (banner==null || banner.getTitle()==null || banner.getImageUrl()==null) throw new IllegalArgumentException("轮播图参数不完整");
        banner.setCreateTime(LocalDateTime.now()); banner.setUpdateTime(LocalDateTime.now()); if (banner.getStatus()==null) banner.setStatus(1); if (banner.getSortNo()==null) banner.setSortNo(0); banner.setDeleted(0);
        if (homeBannerMapper.insert(banner)<=0) throw new RuntimeException("新增轮播图失败"); return banner.getId(); }
    @Override
    public boolean update(HomeBanner banner) { if (banner==null||banner.getId()==null) throw new IllegalArgumentException("轮播图ID不能为空"); banner.setUpdateTime(LocalDateTime.now()); return homeBannerMapper.updateById(banner)>0; }
    @Override
    public boolean delete(Long id) { if (id==null) throw new IllegalArgumentException("id不能为空"); HomeBanner b=new HomeBanner(); b.setId(id); b.setDeleted(1); b.setUpdateTime(LocalDateTime.now()); return homeBannerMapper.updateById(b)>0; }
}
