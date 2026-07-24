package com.laundry.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.laundry.common.PageResult;
import com.laundry.entity.OrderInfo;
import com.laundry.entity.OrderProgress;
import com.laundry.entity.User;
import com.laundry.mapper.OrderInfoMapper;
import com.laundry.mapper.OrderProgressMapper;
import com.laundry.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 订单进度服务
 */
@Service
public class OrderProgressService extends ServiceImpl<OrderProgressMapper, OrderProgress> {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private OrderInfoMapper orderInfoMapper;
    
    /**
     * 分页查询订单进度
     */
    public PageResult<OrderProgress> page(Integer pageNum, Integer pageSize, Long orderId) {
        Page<OrderProgress> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<OrderProgress> wrapper = new LambdaQueryWrapper<>();
        
        if (orderId != null) {
            wrapper.eq(OrderProgress::getOrderId, orderId);
        }
        wrapper.orderByDesc(OrderProgress::getCreateTime);
        
        Page<OrderProgress> result = this.page(page, wrapper);
        
        // 填充关联信息
        result.getRecords().forEach(this::fillInfo);
        
        return PageResult.from(result);
    }
    
    /**
     * 获取订单的进度列表
     */
    public List<OrderProgress> listByOrder(Long orderId) {
        LambdaQueryWrapper<OrderProgress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderProgress::getOrderId, orderId);
        wrapper.orderByAsc(OrderProgress::getCreateTime);
        
        List<OrderProgress> list = this.list(wrapper);
        list.forEach(this::fillInfo);
        return list;
    }
    
    /**
     * 添加订单进度
     */
    public void addProgress(OrderProgress progress) {
        if (progress.getDescription() == null || progress.getDescription().isEmpty()) {
            progress.setDescription(getProgressDescription(progress.getStatus()));
        }
        this.save(progress);
    }

    private String getProgressDescription(Integer status) {
        if (status == null) {
            return "进度更新";
        }
        switch (status) {
            case 0: return "已下单";
            case 1: return "已取件";
            case 2: return "洗涤中";
            case 3: return "已烘干";
            case 4: return "已整理";
            case 5: return "配送中";
            case 6: return "已送达";
            default: return "进度更新";
        }
    }
    
    private void fillInfo(OrderProgress progress) {
        if (progress.getOperatorId() != null) {
            User operator = userMapper.selectById(progress.getOperatorId());
            if (operator != null) {
                progress.setOperatorName(operator.getNickname());
            }
        }
        
        OrderInfo order = orderInfoMapper.selectById(progress.getOrderId());
        if (order != null) {
            progress.setOrderNo(order.getOrderNo());
        }
    }
}
