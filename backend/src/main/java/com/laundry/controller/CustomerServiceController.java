package com.laundry.controller;

import com.laundry.common.PageResult;
import com.laundry.common.Result;
import com.laundry.entity.CustomerService;
import com.laundry.service.CustomerServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 在线客服控制器
 */
@Tag(name = "在线客服")
@RestController
@RequestMapping("/customer-service")
public class CustomerServiceController {
    
    @Autowired
    private CustomerServiceService customerServiceService;
    
    @Operation(summary = "分页查询消息")
    @GetMapping("/page")
    public Result<PageResult<CustomerService>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long userId) {
        return Result.success(customerServiceService.page(pageNum, pageSize, userId));
    }
    
    @Operation(summary = "获取当前用户的聊天记录")
    @GetMapping("/my")
    public Result<List<CustomerService>> getMyMessages(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(customerServiceService.getUserMessages(userId));
    }
    
    @Operation(summary = "获取用户的聊天记录")
    @GetMapping("/user/{userId}")
    public Result<List<CustomerService>> getUserMessages(@PathVariable Long userId) {
        return Result.success(customerServiceService.getUserMessages(userId));
    }
    
    @Operation(summary = "发送消息")
    @PostMapping
    public Result<Void> send(@RequestBody CustomerService message, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        message.setUserId(userId);
        message.setType(0);
        customerServiceService.sendMessage(message);
        return Result.success();
    }
    
    @Operation(summary = "客服回复")
    @PostMapping("/reply")
    public Result<Void> reply(@RequestParam Long userId, @RequestParam String content, HttpServletRequest request) {
        Long adminId = (Long) request.getAttribute("userId");
        customerServiceService.replyMessage(userId, content, adminId);
        return Result.success();
    }
    
    @Operation(summary = "标记已读")
    @PutMapping("/read/{userId}")
    public Result<Void> markAsRead(@PathVariable Long userId) {
        customerServiceService.markAsRead(userId);
        return Result.success();
    }
    
    @Operation(summary = "获取未读消息数")
    @GetMapping("/unread")
    public Result<Long> getUnreadCount(@RequestParam(required = false) Long userId) {
        return Result.success(customerServiceService.getUnreadCount(userId));
    }
}
