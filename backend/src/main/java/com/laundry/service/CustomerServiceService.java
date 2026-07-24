package com.laundry.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.laundry.common.PageResult;
import com.laundry.entity.CustomerService;
import com.laundry.entity.User;
import com.laundry.mapper.CustomerServiceMapper;
import com.laundry.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 在线客服服务
 */
@Service
public class CustomerServiceService extends ServiceImpl<CustomerServiceMapper, CustomerService> {
    
    @Autowired
    private UserMapper userMapper;
    
    /**
     * 分页查询消息
     */
    public PageResult<CustomerService> page(Integer pageNum, Integer pageSize, Long userId) {
        Page<CustomerService> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<CustomerService> wrapper = new LambdaQueryWrapper<>();
        
        if (userId != null) {
            wrapper.eq(CustomerService::getUserId, userId);
        }
        wrapper.orderByDesc(CustomerService::getCreateTime);
        
        Page<CustomerService> result = this.page(page, wrapper);
        
        // 填充用户信息
        result.getRecords().forEach(this::fillUserInfo);
        
        return PageResult.from(result);
    }
    
    /**
     * 获取用户的聊天记录
     */
    public List<CustomerService> getUserMessages(Long userId) {
        LambdaQueryWrapper<CustomerService> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomerService::getUserId, userId);
        wrapper.orderByAsc(CustomerService::getCreateTime);
        
        List<CustomerService> messages = this.list(wrapper);
        messages.forEach(this::fillUserInfo);
        return messages;
    }
    
    /**
     * 发送消息
     */
    public void sendMessage(CustomerService message) {
        message.setIsRead(0);
        this.save(message);
    }
    
    /**
     * 客服回复
     */
    public void replyMessage(Long userId, String content, Long adminId) {
        CustomerService message = new CustomerService();
        message.setUserId(userId);
        message.setAdminId(adminId);
        message.setContent(content);
        message.setType(1);
        message.setIsRead(0);
        this.save(message);
    }
    
    /**
     * 标记消息已读
     */
    public void markAsRead(Long userId) {
        LambdaQueryWrapper<CustomerService> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CustomerService::getUserId, userId);
        wrapper.eq(CustomerService::getIsRead, 0);
        
        List<CustomerService> messages = this.list(wrapper);
        messages.forEach(msg -> {
            msg.setIsRead(1);
            this.updateById(msg);
        });
    }
    
    /**
     * 获取未读消息数
     */
    public long getUnreadCount(Long userId) {
        LambdaQueryWrapper<CustomerService> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(CustomerService::getUserId, userId);
        }
        wrapper.eq(CustomerService::getIsRead, 0);
        wrapper.eq(CustomerService::getType, 0);
        return this.count(wrapper);
    }
    
    private void fillUserInfo(CustomerService message) {
        User user = userMapper.selectById(message.getUserId());
        if (user != null) {
            message.setUserName(user.getNickname());
        }
        
        if (message.getAdminId() != null) {
            User admin = userMapper.selectById(message.getAdminId());
            if (admin != null) {
                message.setAdminName(admin.getNickname());
            }
        }
    }
}
