package com.laundry.controller;

import com.laundry.common.PageResult;
import com.laundry.common.Result;
import com.laundry.dto.OrderCreateDTO;
import com.laundry.entity.OrderInfo;
import com.laundry.entity.OrderProgress;
import com.laundry.service.OrderProgressService;
import com.laundry.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 订单控制器
 */
@Tag(name = "订单管理")
@RestController
@RequestMapping("/order")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private OrderProgressService orderProgressService;
    
    @Operation(summary = "分页查询订单")
    @GetMapping("/page")
    public Result<PageResult<OrderInfo>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) Long shopId,
            @RequestParam(required = false) Integer status) {
        return Result.success(orderService.page(pageNum, pageSize, orderNo, customerId, shopId, status));
    }
    
    @Operation(summary = "获取当前用户的订单")
    @GetMapping("/my")
    public Result<PageResult<OrderInfo>> getMyOrders(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        
        if ("shop_owner".equals(role)) {
            // 店家查看自己店铺的订单
            return Result.success(orderService.page(pageNum, pageSize, null, null, null, status));
        } else {
            // 顾客查看自己的订单
            return Result.success(orderService.page(pageNum, pageSize, null, userId, null, status));
        }
    }
    
    @Operation(summary = "获取订单详情")
    @GetMapping("/{id}")
    public Result<OrderInfo> getDetail(@PathVariable Long id) {
        return Result.success(orderService.getDetail(id));
    }
    
    @Operation(summary = "获取订单进度")
    @GetMapping("/{id}/progress")
    public Result<List<OrderProgress>> getProgress(@PathVariable Long id) {
        return Result.success(orderProgressService.listByOrder(id));
    }
    
    @Operation(summary = "创建订单")
    @PostMapping
    public Result<OrderInfo> create(@Valid @RequestBody OrderCreateDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(orderService.createOrder(dto, userId));
    }
    
    @Operation(summary = "支付订单")
    @PutMapping("/{id}/pay")
    public Result<Void> pay(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        orderService.payOrder(id, userId);
        return Result.success();
    }
    
    @Operation(summary = "更新订单状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(
            @PathVariable Long id,
            @RequestParam Integer status,
            @RequestParam(required = false) String description,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        orderService.updateStatus(id, status, description, userId);
        return Result.success();
    }
    
    @Operation(summary = "取消订单")
    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        orderService.cancelOrder(id, userId);
        return Result.success();
    }
    
    @Operation(summary = "获取订单统计")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics(@RequestParam(required = false) Long shopId) {
        return Result.success(orderService.getStatistics(shopId));
    }
}
