package com.youngman.hostel.service;

import com.youngman.hostel.vo.AllocationNoticeVO;
import com.youngman.hostel.vo.HostelBedAvailableVO;

import java.util.List;

/**
 * 床位自动分配业务接口
 */
public interface BedAllocationService {

    /**
     * 为指定已支付订单分配床位并返回分配通知结果
     */
    AllocationNoticeVO allocateBedForOrder(Long orderId);

    /**
     * 从候选床位中选择最优床位
     * （可优先选择已有入住人的房间，提高房间利用率）
     */
    HostelBedAvailableVO chooseBestBed(List<HostelBedAvailableVO> candidateBeds);

    /**
     * 构建分配结果通知数据
     */
    AllocationNoticeVO buildAllocationNotice(Long orderId, Long roomId, Long bedId);
}
