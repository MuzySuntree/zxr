package com.youngman.hostel.controller;

import com.youngman.hostel.common.ApiResponse;
import com.youngman.hostel.dto.CreateBookingOrderDTO;
import com.youngman.hostel.service.BookingOrderService;
import com.youngman.hostel.vo.AllocationNoticeVO;
import com.youngman.hostel.vo.BookingOrderDetailVO;
import com.youngman.hostel.vo.PaymentResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 订单接口
 */
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class BookingOrderController {

    private final BookingOrderService bookingOrderService;

    /** 创建待支付订单 */
    @PostMapping("/create")
    public ApiResponse<Long> createOrder(@RequestBody CreateBookingOrderDTO dto) {
        try {
            return ApiResponse.success("订单创建成功", bookingOrderService.createPendingOrder(dto));
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    /** 查询订单详情 */
    @GetMapping("/{orderId}")
    public ApiResponse<BookingOrderDetailVO> getOrderById(@PathVariable Long orderId) {
        try {
            return ApiResponse.success(bookingOrderService.getOrderDetailById(orderId));
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    /** 查询订单列表 */
    @GetMapping("/list")
    public ApiResponse<List<BookingOrderDetailVO>> listOrders() {
        try {
            return ApiResponse.success(bookingOrderService.listOrders());
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    /** 取消订单 */
    @PutMapping("/cancel/{orderId}")
    public ApiResponse<Boolean> cancelOrder(@PathVariable Long orderId) {
        try {
            return ApiResponse.success(bookingOrderService.cancelOrder(orderId));
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    /** 删除订单 */
    @DeleteMapping("/{orderId}")
    public ApiResponse<Boolean> deleteOrder(@PathVariable Long orderId) {
        try {
            return ApiResponse.success(bookingOrderService.deleteOrder(orderId));
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    /** 检查某时间段内是否有可用床位 */
    @GetMapping("/check-available")
    public ApiResponse<Boolean> checkAvailableBeds(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkInDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkOutDate,
            @RequestParam(required = false) Integer userGender,
            @RequestParam(required = false) Long userId) {
        try {
            if (userId != null) {
                return ApiResponse.success(bookingOrderService.hasAvailableBedsByUserId(checkInDate, checkOutDate, userId));
            }
            return ApiResponse.success(bookingOrderService.hasAvailableBeds(checkInDate, checkOutDate, userGender));
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    /**
     * 模拟支付。
     * 说明：当前 serviceImpl 中 simulatePay 已内含“支付后自动分配床位”的流程触发。
     */
    @PostMapping("/pay/{orderId}")
    public ApiResponse<PaymentResultVO> simulatePay(@PathVariable Long orderId) {
        try {
            return ApiResponse.success(bookingOrderService.simulatePay(orderId));
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    /**
     * 显式触发分配（可用于重试或手动触发）。
     * 若订单已在支付流程中完成分配，此接口会按 service 规则返回状态异常。
     */
    @PostMapping("/allocate/{orderId}")
    public ApiResponse<AllocationNoticeVO> allocateBed(@PathVariable Long orderId) {
        try {
            return ApiResponse.success(bookingOrderService.allocateBedAfterPayment(orderId));
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    /** 查询订单当前分配结果 */
    @GetMapping("/allocation/{orderId}")
    public ApiResponse<AllocationNoticeVO> getAllocationResult(@PathVariable Long orderId) {
        try {
            return ApiResponse.success(bookingOrderService.getAllocationResult(orderId));
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }
}
