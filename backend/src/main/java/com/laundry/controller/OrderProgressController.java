package com.laundry.controller;

import com.laundry.common.PageResult;
import com.laundry.common.Result;
import com.laundry.entity.OrderProgress;
import com.laundry.service.OrderProgressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 订单进度控制器
 */
@Tag(name = "订单进度管理")
@RestController
@RequestMapping("/order-progress")
public class OrderProgressController {
    
    @Autowired
    private OrderProgressService orderProgressService;
    
    @Operation(summary = "分页查询订单进度")
    @GetMapping("/page")
    public Result<PageResult<OrderProgress>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long orderId) {
        return Result.success(orderProgressService.page(pageNum, pageSize, orderId));
    }
    
    @Operation(summary = "添加订单进度")
    @PostMapping
    public Result<Void> add(@RequestBody OrderProgress progress, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        progress.setOperatorId(userId);
        orderProgressService.addProgress(progress);
        return Result.success();
    }
}
