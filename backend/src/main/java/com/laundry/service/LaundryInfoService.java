package com.laundry.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.laundry.common.PageResult;
import com.laundry.entity.ClothesType;
import com.laundry.entity.LaundryInfo;
import com.laundry.entity.Shop;
import com.laundry.exception.BusinessException;
import com.laundry.mapper.ClothesTypeMapper;
import com.laundry.mapper.LaundryInfoMapper;
import com.laundry.mapper.ShopMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 洗衣信息服务
 */
@Service
public class LaundryInfoService extends ServiceImpl<LaundryInfoMapper, LaundryInfo> {
    
    @Autowired
    private ShopMapper shopMapper;
    
    @Autowired
    private ClothesTypeMapper clothesTypeMapper;
    
    /**
     * 分页查询洗衣信息
     */
    public PageResult<LaundryInfo> page(Integer pageNum, Integer pageSize, String name, Long shopId, Long clothesTypeId) {
        Page<LaundryInfo> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<LaundryInfo> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(name)) {
            wrapper.like(LaundryInfo::getName, name);
        }
        if (shopId != null) {
            wrapper.eq(LaundryInfo::getShopId, shopId);
        }
        if (clothesTypeId != null) {
            wrapper.eq(LaundryInfo::getClothesTypeId, clothesTypeId);
        }
        wrapper.orderByAsc(LaundryInfo::getSort);
        
        Page<LaundryInfo> result = this.page(page, wrapper);
        
        // 填充关联信息
        result.getRecords().forEach(this::fillInfo);
        
        return PageResult.from(result);
    }
    
    /**
     * 获取店铺的洗衣服务列表
     */
    public List<LaundryInfo> listByShop(Long shopId) {
        LambdaQueryWrapper<LaundryInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LaundryInfo::getShopId, shopId);
        wrapper.eq(LaundryInfo::getStatus, 1);
        wrapper.orderByAsc(LaundryInfo::getSort);
        
        List<LaundryInfo> list = this.list(wrapper);
        list.forEach(this::fillInfo);
        return list;
    }
    
    /**
     * 获取洗衣信息详情
     */
    public LaundryInfo getDetail(Long id) {
        LaundryInfo info = this.getById(id);
        if (info == null) {
            throw new BusinessException("洗衣信息不存在");
        }
        fillInfo(info);
        return info;
    }
    
    /**
     * 创建洗衣信息
     */
    public void createInfo(LaundryInfo info) {
        info.setStatus(1);
        this.save(info);
    }
    
    /**
     * 更新洗衣信息
     */
    public void updateInfo(Long id, LaundryInfo info) {
        LaundryInfo existing = this.getById(id);
        if (existing == null) {
            throw new BusinessException("洗衣信息不存在");
        }
        
        info.setId(id);
        this.updateById(info);
    }
    
    /**
     * 更新状态
     */
    public void updateStatus(Long id, Integer status) {
        LaundryInfo info = this.getById(id);
        if (info == null) {
            throw new BusinessException("洗衣信息不存在");
        }
        
        info.setStatus(status);
        this.updateById(info);
    }
    
    private void fillInfo(LaundryInfo info) {
        Shop shop = shopMapper.selectById(info.getShopId());
        if (shop != null) {
            info.setShopName(shop.getName());
        }
        
        ClothesType type = clothesTypeMapper.selectById(info.getClothesTypeId());
        if (type != null) {
            info.setClothesTypeName(type.getName());
        }
    }
}
