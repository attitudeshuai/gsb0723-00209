package com.laundry.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.laundry.common.PageResult;
import com.laundry.entity.Shop;
import com.laundry.entity.User;
import com.laundry.exception.BusinessException;
import com.laundry.mapper.ShopMapper;
import com.laundry.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 店铺服务
 */
@Service
public class ShopService extends ServiceImpl<ShopMapper, Shop> {
    
    @Autowired
    private UserMapper userMapper;
    
    /**
     * 分页查询店铺
     */
    public PageResult<Shop> page(Integer pageNum, Integer pageSize, String name, Long ownerId) {
        Page<Shop> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Shop> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(name)) {
            wrapper.like(Shop::getName, name);
        }
        if (ownerId != null) {
            wrapper.eq(Shop::getOwnerId, ownerId);
        }
        wrapper.orderByDesc(Shop::getCreateTime);
        
        Page<Shop> result = this.page(page, wrapper);
        
        // 填充店家名称
        result.getRecords().forEach(shop -> {
            User owner = userMapper.selectById(shop.getOwnerId());
            if (owner != null) {
                shop.setOwnerName(owner.getNickname());
            }
        });
        
        return PageResult.from(result);
    }
    
    /**
     * 获取店铺详情
     */
    public Shop getDetail(Long id) {
        Shop shop = this.getById(id);
        if (shop == null) {
            throw new BusinessException("店铺不存在");
        }
        
        User owner = userMapper.selectById(shop.getOwnerId());
        if (owner != null) {
            shop.setOwnerName(owner.getNickname());
        }
        
        return shop;
    }
    
    /**
     * 根据店家ID获取店铺
     */
    public Shop getByOwnerId(Long ownerId) {
        return this.getOne(new LambdaQueryWrapper<Shop>()
                .eq(Shop::getOwnerId, ownerId));
    }
    
    /**
     * 创建店铺
     */
    public void createShop(Shop shop) {
        // 检查该店家是否已有店铺
        long count = this.count(new LambdaQueryWrapper<Shop>()
                .eq(Shop::getOwnerId, shop.getOwnerId()));
        if (count > 0) {
            throw new BusinessException("该店家已有店铺");
        }
        
        shop.setStatus(1);
        this.save(shop);
    }
    
    /**
     * 更新店铺
     */
    public void updateShop(Long id, Shop shop) {
        Shop existing = this.getById(id);
        if (existing == null) {
            throw new BusinessException("店铺不存在");
        }
        
        shop.setId(id);
        // 店铺归属不可通过编辑接口修改，强制沿用原 ownerId，防止归属被篡改
        shop.setOwnerId(existing.getOwnerId());
        this.updateById(shop);
    }
    
    /**
     * 更新店铺状态
     */
    public void updateStatus(Long id, Integer status) {
        Shop shop = this.getById(id);
        if (shop == null) {
            throw new BusinessException("店铺不存在");
        }
        
        shop.setStatus(status);
        this.updateById(shop);
    }
}
