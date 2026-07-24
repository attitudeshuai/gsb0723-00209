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
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 订单服务
 */
@Service
public class OrderService extends ServiceImpl<OrderInfoMapper, OrderInfo> {
    
    private static final DateTimeFormatter ORDER_NO_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    
    /** 进程内订单号序列，配合毫秒时间戳保证并发唯一 */
    private static final AtomicInteger ORDER_SEQ = new AtomicInteger(0);
    
    /** 订单号冲突时的最大重试次数 */
    private static final int MAX_ORDER_NO_RETRY = 5;
    
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
        // 以实际售价 price 计费，original_price 仅为展示用的划线原价
        order.setTotalPrice(laundryInfo.getPrice().multiply(new BigDecimal(dto.getQuantity())));
        order.setStatus(0);
        order.setRemark(dto.getRemark());
        order.setPickupAddress(dto.getPickupAddress());
        order.setDeliveryAddress(dto.getDeliveryAddress());
        
        saveWithUniqueOrderNo(order);
        
        // 添加订单进度
        addProgress(order.getId(), 0, "订单已创建", customerId);
        
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
        
        addProgress(id, 1, "订单已支付，等待取件", operatorId);
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
        if (status == null) {
            throw new BusinessException("订单状态不正确");
        }
        // 校验状态流转是否合法，禁止越级跳转
        if (!canTransfer(order.getStatus(), status)) {
            throw new BusinessException("订单状态不允许从" + statusText(order.getStatus())
                    + "变更为" + statusText(status));
        }
        
        order.setStatus(status);
        if (status == 3) {
            order.setCompleteTime(LocalDateTime.now());
        }
        this.updateById(order);
        
        // description 缺省时按目标状态生成有意义的描述，避免进度记录只剩状态码
        String progressDesc = StringUtils.hasText(description) ? description : statusText(status);
        addProgress(id, status, progressDesc, operatorId);
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
        // 待支付(0)、已支付但店家尚未开始处理(1)均可取消；洗涤中(2)及以后不可取消
        if (order.getStatus() == 4) {
            throw new BusinessException("订单已取消");
        }
        if (order.getStatus() >= 2) {
            throw new BusinessException("订单已在处理中，无法取消");
        }
        
        order.setStatus(4);
        this.updateById(order);
        
        addProgress(id, 4, "订单已取消", operatorId);
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
        String dateStr = LocalDateTime.now().format(ORDER_NO_FORMATTER);
        // 进程内原子递增序列，避免同一毫秒内并发生成相同订单号
        int seq = ORDER_SEQ.getAndIncrement() & 0x7FFFFFFF;
        return "ORD" + dateStr + String.format("%04d", seq % 10000);
    }
    
    /**
     * 保存订单并保证订单号唯一。
     * 生成器已带毫秒时间戳+进程内原子序列，基本可避免单实例冲突；
     * 多实例场景下若仍命中数据库 uk_order_no 唯一约束，则重新生成后重试，
     * 从而避免高并发下单时偶发的订单创建失败。
     */
    private void saveWithUniqueOrderNo(OrderInfo order) {
        for (int i = 0; i < MAX_ORDER_NO_RETRY; i++) {
            order.setOrderNo(generateOrderNo());
            try {
                this.save(order);
                return;
            } catch (DuplicateKeyException e) {
                // 订单号冲突，重新生成后重试
            }
        }
        throw new BusinessException("订单创建失败，请重试");
    }
    
    /**
     * 校验订单状态流转是否合法，禁止越级跳转。
     * 状态码：0-待支付 1-已支付 2-洗涤中 3-已完成 4-已取消
     */
    private boolean canTransfer(Integer current, Integer target) {
        if (current == null || target == null || current.equals(target)) {
            return false;
        }
        switch (current) {
            case 0:
                return target == 1 || target == 4;
            case 1:
                return target == 2 || target == 4;
            case 2:
                return target == 3;
            default:
                // 已完成(3)、已取消(4) 为终态，不允许再流转
                return false;
        }
    }
    
    /**
     * 订单状态码对应的文字描述
     */
    private String statusText(Integer status) {
        if (status == null) {
            return "未知状态";
        }
        switch (status) {
            case 0:
                return "待支付";
            case 1:
                return "已支付";
            case 2:
                return "洗涤中";
            case 3:
                return "已完成";
            case 4:
                return "已取消";
            default:
                return "未知状态";
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
