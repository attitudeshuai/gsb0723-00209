package com.laundry.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.laundry.common.PageResult;
import com.laundry.dto.OrderCreateDTO;
import com.laundry.entity.*;
import com.laundry.exception.BusinessException;
import com.laundry.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 订单服务
 */
@Service
public class OrderService extends ServiceImpl<OrderInfoMapper, OrderInfo> {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private ShopMapper shopMapper;
    
    @Autowired
    private LaundryInfoMapper laundryInfoMapper;
    
    @Autowired
    private OrderProgressMapper orderProgressMapper;
    
    /**
     * 分页查询订单
     */
    public PageResult<OrderInfo> page(Integer pageNum, Integer pageSize, String orderNo, 
            Long customerId, Long shopId, Integer status) {
        Page<OrderInfo> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<OrderInfo> wrapper = new LambdaQueryWrapper<>();
        
        if (orderNo != null && !orderNo.isEmpty()) {
            wrapper.like(OrderInfo::getOrderNo, orderNo);
        }
        if (customerId != null) {
            wrapper.eq(OrderInfo::getCustomerId, customerId);
        }
        if (shopId != null) {
            wrapper.eq(OrderInfo::getShopId, shopId);
        }
        if (status != null) {
            wrapper.eq(OrderInfo::getStatus, status);
        }
        wrapper.orderByDesc(OrderInfo::getCreateTime);
        
        Page<OrderInfo> result = this.page(page, wrapper);
        
        // 填充关联信息
        result.getRecords().forEach(this::fillInfo);
        
        return PageResult.from(result);
    }
    
    /**
     * 获取订单详情
     */
    public OrderInfo getDetail(Long id) {
        OrderInfo order = this.getById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        fillInfo(order);
        return order;
    }
    
    /**
     * 创建订单
     */
    @Transactional
    public OrderInfo createOrder(OrderCreateDTO dto, Long customerId) {
        LaundryInfo laundryInfo = laundryInfoMapper.selectById(dto.getLaundryInfoId());
        if (laundryInfo == null) {
            throw new BusinessException("洗衣服务不存在");
        }
        
        OrderInfo order = new OrderInfo();
        order.setCustomerId(customerId);
        order.setShopId(laundryInfo.getShopId());
        order.setLaundryInfoId(dto.getLaundryInfoId());
        order.setQuantity(dto.getQuantity());
        order.setTotalPrice(laundryInfo.getPrice().multiply(new BigDecimal(dto.getQuantity())));
        order.setStatus(0);
        order.setRemark(dto.getRemark());
        order.setPickupAddress(dto.getPickupAddress());
        order.setDeliveryAddress(dto.getDeliveryAddress());

        int maxRetry = 3;
        for (int attempt = 0; attempt < maxRetry; attempt++) {
            order.setOrderNo(generateOrderNo());
            try {
                this.save(order);
                break;
            } catch (DuplicateKeyException e) {
                if (attempt == maxRetry - 1) {
                    throw new BusinessException("订单号生成失败，请稍后重试");
                }
            }
        }
        
        // 添加订单进度
        addProgress(order.getId(), 0, getStatusDescription(0), customerId);
        
        return order;
    }
    
    /**
     * 支付订单
     */
    @Transactional
    public void payOrder(Long id, Long operatorId) {
        OrderInfo order = this.getById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException("订单状态不正确");
        }
        
        order.setStatus(1);
        order.setPaymentTime(LocalDateTime.now());
        this.updateById(order);
        
        addProgress(id, 1, getStatusDescription(1), operatorId);
    }
    
    /**
     * 更新订单状态
     */
    @Transactional
    public void updateStatus(Long id, Integer status, String description, Long operatorId) {
        OrderInfo order = this.getById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        
        if (!isValidTransition(order.getStatus(), status)) {
            throw new BusinessException("订单状态不允许从「" + getStatusDescription(order.getStatus())
                    + "」变更为「" + getStatusDescription(status) + "」");
        }
        
        order.setStatus(status);
        if (status == 3) {
            order.setCompleteTime(LocalDateTime.now());
        }
        this.updateById(order);
        
        String desc = (description != null && !description.isEmpty()) ? description : getStatusDescription(status);
        addProgress(id, status, desc, operatorId);
    }
    
    /**
     * 取消订单
     */
    @Transactional
    public void cancelOrder(Long id, Long operatorId) {
        OrderInfo order = this.getById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (order.getStatus() != 0 && order.getStatus() != 1) {
            throw new BusinessException("当前订单状态无法取消");
        }
        
        order.setStatus(4);
        this.updateById(order);
        
        addProgress(id, 4, getStatusDescription(4), operatorId);
    }
    
    /**
     * 获取统计数据
     */
    public Map<String, Object> getStatistics(Long shopId) {
        Map<String, Object> stats = new HashMap<>();
        
        LambdaQueryWrapper<OrderInfo> wrapper = new LambdaQueryWrapper<>();
        if (shopId != null) {
            wrapper.eq(OrderInfo::getShopId, shopId);
        }
        
        // 总订单数
        stats.put("totalOrders", this.count(wrapper));
        
        // 待处理订单
        wrapper.clear();
        if (shopId != null) wrapper.eq(OrderInfo::getShopId, shopId);
        wrapper.in(OrderInfo::getStatus, 0, 1);
        stats.put("pendingOrders", this.count(wrapper));
        
        // 进行中订单
        wrapper.clear();
        if (shopId != null) wrapper.eq(OrderInfo::getShopId, shopId);
        wrapper.eq(OrderInfo::getStatus, 2);
        stats.put("processingOrders", this.count(wrapper));
        
        // 已完成订单
        wrapper.clear();
        if (shopId != null) wrapper.eq(OrderInfo::getShopId, shopId);
        wrapper.eq(OrderInfo::getStatus, 3);
        stats.put("completedOrders", this.count(wrapper));
        
        return stats;
    }
    
    private void addProgress(Long orderId, Integer status, String description, Long operatorId) {
        OrderProgress progress = new OrderProgress();
        progress.setOrderId(orderId);
        progress.setStatus(status);
        progress.setDescription(description);
        progress.setOperatorId(operatorId);
        orderProgressMapper.insert(progress);
    }
    
    private String generateOrderNo() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = ThreadLocalRandom.current().nextInt(100000, 1000000);
        return "ORD" + dateStr + String.format("%06d", random);
    }

    private boolean isValidTransition(Integer currentStatus, Integer targetStatus) {
        if (currentStatus == null || targetStatus == null) {
            return false;
        }
        switch (currentStatus) {
            case 0:
                return targetStatus == 1 || targetStatus == 4;
            case 1:
                return targetStatus == 2 || targetStatus == 4;
            case 2:
                return targetStatus == 3;
            default:
                return false;
        }
    }

    private String getStatusDescription(Integer status) {
        if (status == null) {
            return "未知状态";
        }
        switch (status) {
            case 0: return "订单已创建";
            case 1: return "订单已支付，等待取件";
            case 2: return "订单洗涤中";
            case 3: return "订单已完成";
            case 4: return "订单已取消";
            default: return "状态更新";
        }
    }
    
    private void fillInfo(OrderInfo order) {
        User customer = userMapper.selectById(order.getCustomerId());
        if (customer != null) {
            order.setCustomerName(customer.getNickname());
        }
        
        Shop shop = shopMapper.selectById(order.getShopId());
        if (shop != null) {
            order.setShopName(shop.getName());
        }
        
        LaundryInfo laundry = laundryInfoMapper.selectById(order.getLaundryInfoId());
        if (laundry != null) {
            order.setLaundryName(laundry.getName());
        }
    }
}
