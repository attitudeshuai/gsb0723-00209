package com.laundry.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.laundry.common.PageResult;
import com.laundry.entity.ClothesType;
import com.laundry.exception.BusinessException;
import com.laundry.mapper.ClothesTypeMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 衣服类型服务
 */
@Service
public class ClothesTypeService extends ServiceImpl<ClothesTypeMapper, ClothesType> {
    
    /**
     * 分页查询衣服类型
     */
    public PageResult<ClothesType> page(Integer pageNum, Integer pageSize, String name, Long shopId) {
        Page<ClothesType> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ClothesType> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(name)) {
            wrapper.like(ClothesType::getName, name);
        }
        if (shopId != null) {
            wrapper.and(w -> w.eq(ClothesType::getShopId, shopId).or().isNull(ClothesType::getShopId));
        }
        wrapper.orderByAsc(ClothesType::getSort);
        
        return PageResult.from(this.page(page, wrapper));
    }
    
    /**
     * 获取所有衣服类型
     */
    public List<ClothesType> listAll(Long shopId) {
        LambdaQueryWrapper<ClothesType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ClothesType::getStatus, 1);
        if (shopId != null) {
            wrapper.and(w -> w.eq(ClothesType::getShopId, shopId).or().isNull(ClothesType::getShopId));
        } else {
            wrapper.isNull(ClothesType::getShopId);
        }
        wrapper.orderByAsc(ClothesType::getSort);
        return this.list(wrapper);
    }
    
    /**
     * 获取衣服类型详情
     */
    public ClothesType getDetail(Long id) {
        ClothesType type = this.getById(id);
        if (type == null) {
            throw new BusinessException("衣服类型不存在");
        }
        return type;
    }
    
    /**
     * 创建衣服类型
     */
    public void createType(ClothesType type) {
        type.setStatus(1);
        this.save(type);
    }
    
    /**
     * 更新衣服类型
     */
    public void updateType(Long id, ClothesType type) {
        ClothesType existing = this.getById(id);
        if (existing == null) {
            throw new BusinessException("衣服类型不存在");
        }
        
        type.setId(id);
        this.updateById(type);
    }
    
    /**
     * 更新状态
     */
    public void updateStatus(Long id, Integer status) {
        ClothesType type = this.getById(id);
        if (type == null) {
            throw new BusinessException("衣服类型不存在");
        }
        
        type.setStatus(status);
        this.updateById(type);
    }
}
