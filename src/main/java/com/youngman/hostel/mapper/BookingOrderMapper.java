package com.youngman.hostel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youngman.hostel.entity.BookingOrder;
import com.youngman.hostel.vo.BookingOrderDetailVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface BookingOrderMapper extends BaseMapper<BookingOrder> {

    BookingOrderDetailVO selectOrderDetailById(@Param("orderId") Long orderId);

    BookingOrderDetailVO selectOrderDetailByOrderNo(@Param("orderNo") String orderNo);
}
