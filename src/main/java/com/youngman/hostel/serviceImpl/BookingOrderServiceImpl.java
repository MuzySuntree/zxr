package com.youngman.hostel.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.youngman.hostel.dto.CreateBookingOrderDTO;
import com.youngman.hostel.entity.BookingNotice;
import com.youngman.hostel.entity.BookingOrder;
import com.youngman.hostel.entity.SysUser;
import com.youngman.hostel.mapper.BookingOrderMapper;
import com.youngman.hostel.mapper.SysUserMapper;
import com.youngman.hostel.service.BedAllocationService;
import com.youngman.hostel.service.BookingNoticeService;
import com.youngman.hostel.service.BookingOrderService;
import com.youngman.hostel.service.HostelBedService;
import com.youngman.hostel.vo.AllocationNoticeVO;
import com.youngman.hostel.vo.BookingOrderDetailVO;
import com.youngman.hostel.vo.HostelBedAvailableVO;
import com.youngman.hostel.vo.PaymentResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * 订单业务实现
 */
@Service
@RequiredArgsConstructor
public class BookingOrderServiceImpl implements BookingOrderService {

    private static final int ORDER_STATUS_PENDING_PAY = 1;
    private static final int ORDER_STATUS_PAID = 2;
    private static final int ORDER_STATUS_ALLOCATED = 3;
    private static final int ORDER_STATUS_COMPLETED = 5;
    private static final int ORDER_STATUS_CANCELED = 6;

    private final BookingOrderMapper bookingOrderMapper;
    private final SysUserMapper sysUserMapper;
    private final HostelBedService hostelBedService;
    private final BedAllocationService bedAllocationService;
    private final BookingNoticeService bookingNoticeService;

    @Override
    public Long createOrder(CreateBookingOrderDTO dto) {
        return createPendingOrder(dto);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPendingOrder(CreateBookingOrderDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("创建订单参数不能为空");
        }
        if (dto.getUserId() == null) {
            throw new IllegalArgumentException("userId不能为空");
        }
        validateDateRange(dto.getCheckInDate(), dto.getCheckOutDate());

        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getId, dto.getUserId())
                .eq(SysUser::getDeleted, 0)
                .last("limit 1"));
        if (user == null) {
            throw new RuntimeException("用户不存在, userId=" + dto.getUserId());
        }

        Integer gender = dto.getUserGender() == null ? user.getGender() : dto.getUserGender();
        List<HostelBedAvailableVO> beds = hostelBedService.getAvailableBedsByUserGender(
                dto.getCheckInDate(), dto.getCheckOutDate(), gender);

        if (beds == null || beds.isEmpty()) {
            throw new RuntimeException("当前时间段无可分配床位，无法创建订单");
        }

        BigDecimal amount = beds.get(0).getPrice();
        if (amount == null) {
            amount = BigDecimal.ZERO;
        }

        BookingOrder order = new BookingOrder();
        order.setOrderNo(generateOrderNo());
        order.setUserId(dto.getUserId());
        order.setRoomId(null);
        order.setBedId(null);
        order.setCheckInDate(dto.getCheckInDate());
        order.setCheckOutDate(dto.getCheckOutDate());
        order.setOrderStatus(ORDER_STATUS_PENDING_PAY);
        order.setAmount(amount);
        order.setRemark(dto.getRemark());
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        order.setDeleted(0);

        int rows = bookingOrderMapper.insert(order);
        if (rows <= 0 || order.getId() == null) {
            throw new RuntimeException("创建待支付订单失败");
        }
        return order.getId();
    }

    @Override
    public BookingOrderDetailVO getOrderDetailById(Long orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("orderId不能为空");
        }
        BookingOrderDetailVO detail = bookingOrderMapper.selectOrderDetailById(orderId);
        if (detail == null) {
            throw new RuntimeException("订单不存在, orderId=" + orderId);
        }
        return detail;
    }

    @Override
    public BookingOrderDetailVO getOrderDetailByOrderNo(String orderNo) {
        if (orderNo == null || orderNo.trim().isEmpty()) {
            throw new IllegalArgumentException("orderNo不能为空");
        }
        BookingOrderDetailVO detail = bookingOrderMapper.selectOrderDetailByOrderNo(orderNo);
        if (detail == null) {
            throw new RuntimeException("订单不存在, orderNo=" + orderNo);
        }
        return detail;
    }

    @Override
    public List<BookingOrderDetailVO> listOrders() {
        List<BookingOrder> orders = bookingOrderMapper.selectList(new LambdaQueryWrapper<BookingOrder>()
                .eq(BookingOrder::getDeleted, 0)
                .orderByDesc(BookingOrder::getId));

        return orders.stream()
                .map(BookingOrder::getId)
                .map(bookingOrderMapper::selectOrderDetailById)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelOrder(Long orderId) {
        BookingOrder order = getOrderEntity(orderId);
        Integer status = order.getOrderStatus();
        if (Integer.valueOf(ORDER_STATUS_COMPLETED).equals(status) || Integer.valueOf(ORDER_STATUS_CANCELED).equals(status)) {
            throw new RuntimeException("已完成或已取消订单不允许重复取消");
        }
        if (!(Integer.valueOf(ORDER_STATUS_PENDING_PAY).equals(status)
                || Integer.valueOf(ORDER_STATUS_PAID).equals(status)
                || Integer.valueOf(ORDER_STATUS_ALLOCATED).equals(status))) {
            throw new RuntimeException("当前状态不允许取消订单");
        }

        return bookingOrderMapper.update(null, new LambdaUpdateWrapper<BookingOrder>()
                .eq(BookingOrder::getId, orderId)
                .eq(BookingOrder::getDeleted, 0)
                .set(BookingOrder::getOrderStatus, ORDER_STATUS_CANCELED)
                .set(BookingOrder::getUpdateTime, LocalDateTime.now())) > 0;
    }

    @Override
    public boolean deleteOrder(Long orderId) {
        getOrderEntity(orderId);
        return bookingOrderMapper.update(null, new LambdaUpdateWrapper<BookingOrder>()
                .eq(BookingOrder::getId, orderId)
                .eq(BookingOrder::getDeleted, 0)
                .set(BookingOrder::getDeleted, 1)
                .set(BookingOrder::getUpdateTime, LocalDateTime.now())) > 0;
    }

    @Override
    public boolean hasAvailableBeds(LocalDate checkInDate, LocalDate checkOutDate, Integer userGender) {
        validateDateRange(checkInDate, checkOutDate);
        List<HostelBedAvailableVO> beds = hostelBedService.getAvailableBedsByUserGender(checkInDate, checkOutDate, userGender);
        return beds != null && !beds.isEmpty();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentResultVO simulatePay(Long orderId) {
        BookingOrder order = getOrderEntity(orderId);
        if (!Integer.valueOf(ORDER_STATUS_PENDING_PAY).equals(order.getOrderStatus())) {
            throw new RuntimeException("只有待支付订单才能执行支付");
        }

        order.setOrderStatus(ORDER_STATUS_PAID);
        order.setPayTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        int rows = bookingOrderMapper.updateById(order);
        if (rows <= 0) {
            throw new RuntimeException("更新订单支付状态失败");
        }

        // 按业务要求：支付成功后自动分配床位
        allocateBedAfterPayment(orderId);

        PaymentResultVO vo = new PaymentResultVO();
        vo.setOrderId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setOrderStatus(ORDER_STATUS_PAID);
        vo.setPayTime(order.getPayTime());
        vo.setMessage("支付成功，系统已触发自动分配床位流程");
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AllocationNoticeVO allocateBedAfterPayment(Long orderId) {
        BookingOrder order = getOrderEntity(orderId);
        if (!Integer.valueOf(ORDER_STATUS_PAID).equals(order.getOrderStatus())) {
            throw new RuntimeException("只有已支付订单才允许分配床位");
        }

        AllocationNoticeVO allocation = bedAllocationService.allocateBedForOrder(orderId);

        BookingNotice notice = new BookingNotice();
        notice.setUserId(order.getUserId());
        notice.setOrderId(order.getId());
        notice.setTitle("床位分配通知");
        notice.setContent(buildNoticeContent(allocation));
        notice.setIsRead(0);
        bookingNoticeService.createNotice(notice);

        return allocation;
    }

    @Override
    public AllocationNoticeVO getAllocationResult(Long orderId) {
        BookingOrder order = getOrderEntity(orderId);
        if (order.getRoomId() == null || order.getBedId() == null) {
            throw new RuntimeException("当前订单尚未分配床位");
        }
        return bedAllocationService.buildAllocationNotice(orderId, order.getRoomId(), order.getBedId());
    }

    private BookingOrder getOrderEntity(Long orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("orderId不能为空");
        }
        BookingOrder order = bookingOrderMapper.selectById(orderId);
        if (order == null || Integer.valueOf(1).equals(order.getDeleted())) {
            throw new RuntimeException("订单不存在, orderId=" + orderId);
        }
        return order;
    }

    private void validateDateRange(LocalDate checkInDate, LocalDate checkOutDate) {
        if (checkInDate == null || checkOutDate == null) {
            throw new IllegalArgumentException("入住和退房日期不能为空");
        }
        if (!checkInDate.isBefore(checkOutDate)) {
            throw new IllegalArgumentException("入住日期必须早于退房日期");
        }
    }

    private String generateOrderNo() {
        String timePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
        int random = ThreadLocalRandom.current().nextInt(100, 1000);
        return "BO" + timePart + random;
    }

    private String buildNoticeContent(AllocationNoticeVO vo) {
        return String.format(
                "您的订单已成功分配至%s房间 %s床位。当前该房间已有%d人入住，其中男%d人，女%d人。",
                vo.getRoomNo(),
                vo.getBedNo(),
                vo.getOccupiedCount() == null ? 0 : vo.getOccupiedCount(),
                vo.getMaleCount() == null ? 0 : vo.getMaleCount(),
                vo.getFemaleCount() == null ? 0 : vo.getFemaleCount()
        );
    }
}
