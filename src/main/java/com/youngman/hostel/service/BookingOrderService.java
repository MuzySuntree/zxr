package com.youngman.hostel.service;

import com.youngman.hostel.dto.CreateBookingOrderDTO;
import com.youngman.hostel.vo.AllocationNoticeVO;
import com.youngman.hostel.vo.BookingOrderDetailVO;
import com.youngman.hostel.vo.PaymentResultVO;

import java.time.LocalDate;
import java.util.List;

public interface BookingOrderService {

    Long createOrder(CreateBookingOrderDTO dto);

    Long createPendingOrder(CreateBookingOrderDTO dto);

    BookingOrderDetailVO getOrderDetailById(Long orderId);

    BookingOrderDetailVO getOrderDetailByOrderNo(String orderNo);

    List<BookingOrderDetailVO> listOrders();

    boolean cancelOrder(Long orderId);

    boolean deleteOrder(Long orderId);

    boolean hasAvailableBeds(LocalDate checkInDate, LocalDate checkOutDate, Integer userGender);

    boolean hasAvailableBedsByUserId(LocalDate checkInDate, LocalDate checkOutDate, Long userId);

    PaymentResultVO simulatePay(Long orderId);

    AllocationNoticeVO allocateBedAfterPayment(Long orderId);

    AllocationNoticeVO getAllocationResult(Long orderId);
}
