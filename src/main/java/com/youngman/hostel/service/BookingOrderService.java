package com.youngman.hostel.service;

import com.youngman.hostel.dto.CreateBookingOrderDTO;
import com.youngman.hostel.vo.AllocationNoticeVO;
import com.youngman.hostel.vo.BookingOrderDetailVO;
import com.youngman.hostel.vo.PaymentResultVO;

import java.time.LocalDate;
import java.util.List;

/**
 * 订单业务接口
 */
public interface BookingOrderService {

    /**
     * 创建订单（默认创建为待支付订单）
     *
     * 说明：createOrder 与 createPendingOrder 语义一致，统一保留 createPendingOrder，
     * 这里提供 createOrder 作为兼容入口。
     */
    Long createOrder(CreateBookingOrderDTO dto);

    /**
     * 创建待支付订单（状态：1-待支付）
     */
    Long createPendingOrder(CreateBookingOrderDTO dto);

    /**
     * 根据订单ID查询订单详情
     */
    BookingOrderDetailVO getOrderDetailById(Long orderId);

    /**
     * 根据订单号查询订单详情
     */
    BookingOrderDetailVO getOrderDetailByOrderNo(String orderNo);

    /**
     * 查询订单列表
     */
    List<BookingOrderDetailVO> listOrders();

    /**
     * 取消订单
     */
    boolean cancelOrder(Long orderId);

    /**
     * 删除订单（逻辑删除）
     */
    boolean deleteOrder(Long orderId);

    /**
     * 检查指定时间段是否存在可分配床位
     */
    boolean hasAvailableBeds(LocalDate checkInDate, LocalDate checkOutDate, Integer userGender);

    /**
     * 模拟支付：订单状态待支付(1) -> 已支付(2)
     */
    PaymentResultVO simulatePay(Long orderId);

    /**
     * 支付成功后自动分配床位：已支付(2) -> 已分配(3)
     */
    AllocationNoticeVO allocateBedAfterPayment(Long orderId);

    /**
     * 查询订单当前分配结果
     */
    AllocationNoticeVO getAllocationResult(Long orderId);
}
