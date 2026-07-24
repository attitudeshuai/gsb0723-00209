package com.laundry.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.laundry.common.PageResult;
import com.laundry.entity.Announcement;
import com.laundry.exception.BusinessException;
import com.laundry.mapper.AnnouncementMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 系统公告服务
 */
@Service
public class AnnouncementService extends ServiceImpl<AnnouncementMapper, Announcement> {
    
    /**
     * 分页查询公告
     */
    public PageResult<Announcement> page(Integer pageNum, Integer pageSize, String title, Integer type) {
        Page<Announcement> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(title)) {
            wrapper.like(Announcement::getTitle, title);
        }
        if (type != null) {
            wrapper.eq(Announcement::getType, type);
        }
        wrapper.orderByDesc(Announcement::getCreateTime);
        
        return PageResult.from(this.page(page, wrapper));
    }
    
    /**
     * 获取启用的公告列表
     */
    public List<Announcement> listEnabled() {
        LambdaQueryWrapper<Announcement> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Announcement::getStatus, 1);
        wrapper.orderByDesc(Announcement::getType);
        wrapper.orderByDesc(Announcement::getCreateTime);
        return this.list(wrapper);
    }
    
    /**
     * 获取公告详情
     */
    public Announcement getDetail(Long id) {
        Announcement announcement = this.getById(id);
        if (announcement == null) {
            throw new BusinessException("公告不存在");
        }
        return announcement;
    }
    
    /**
     * 创建公告
     */
    public void createAnnouncement(Announcement announcement) {
        announcement.setStatus(1);
        this.save(announcement);
    }
    
    /**
     * 更新公告
     */
    public void updateAnnouncement(Long id, Announcement announcement) {
        Announcement existing = this.getById(id);
        if (existing == null) {
            throw new BusinessException("公告不存在");
        }
        
        announcement.setId(id);
        this.updateById(announcement);
    }
    
    /**
     * 更新状态
     */
    public void updateStatus(Long id, Integer status) {
        Announcement announcement = this.getById(id);
        if (announcement == null) {
            throw new BusinessException("公告不存在");
        }
        
        announcement.setStatus(status);
        this.updateById(announcement);
    }
}
