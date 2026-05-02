package com.youngman.hostel.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.youngman.hostel.entity.HostelActivity;
import com.youngman.hostel.mapper.HostelActivityMapper;
import com.youngman.hostel.service.HostelActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HostelActivityServiceImpl implements HostelActivityService {
    private final HostelActivityMapper hostelActivityMapper;
    @Override
    public List<HostelActivity> listRecommend() {
        return hostelActivityMapper.selectList(new LambdaQueryWrapper<HostelActivity>()
                .eq(HostelActivity::getStatus,1).eq(HostelActivity::getRecommend,1).eq(HostelActivity::getDeleted,0)
                .orderByAsc(HostelActivity::getSortNo).orderByAsc(HostelActivity::getActivityTime));
    }
    @Override
    public List<HostelActivity> listAll() { return hostelActivityMapper.selectList(new LambdaQueryWrapper<HostelActivity>().eq(HostelActivity::getDeleted,0).orderByAsc(HostelActivity::getSortNo).orderByDesc(HostelActivity::getId)); }
    @Override
    public Long save(HostelActivity a) { if(a==null||a.getTitle()==null) throw new IllegalArgumentException("活动参数不完整"); a.setCreateTime(LocalDateTime.now()); a.setUpdateTime(LocalDateTime.now()); if(a.getStatus()==null)a.setStatus(0); if(a.getRecommend()==null)a.setRecommend(0); if(a.getSortNo()==null)a.setSortNo(0); if(a.getJoinedPeople()==null)a.setJoinedPeople(0); a.setDeleted(0); if(hostelActivityMapper.insert(a)<=0) throw new RuntimeException("新增活动失败"); return a.getId(); }
    @Override
    public boolean update(HostelActivity a) { if(a==null||a.getId()==null) throw new IllegalArgumentException("活动ID不能为空"); a.setUpdateTime(LocalDateTime.now()); return hostelActivityMapper.updateById(a)>0; }
    @Override
    public boolean delete(Long id) { if(id==null) throw new IllegalArgumentException("id不能为空"); HostelActivity a=new HostelActivity(); a.setId(id); a.setDeleted(1); a.setUpdateTime(LocalDateTime.now()); return hostelActivityMapper.updateById(a)>0; }
    @Override
    public boolean setRecommend(Long id,Integer recommend){ if(id==null) throw new IllegalArgumentException("id不能为空"); if(recommend==null||(recommend!=0&&recommend!=1)) throw new IllegalArgumentException("recommend仅支持0或1"); return hostelActivityMapper.update(null,new LambdaUpdateWrapper<HostelActivity>().eq(HostelActivity::getId,id).eq(HostelActivity::getDeleted,0).set(HostelActivity::getRecommend,recommend).set(HostelActivity::getUpdateTime,LocalDateTime.now()))>0; }
}
