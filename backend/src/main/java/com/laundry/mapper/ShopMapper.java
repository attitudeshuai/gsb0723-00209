package com.laundry.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.laundry.entity.Shop;
import org.apache.ibatis.annotations.Mapper;

/**
 * 店铺Mapper接口
 */
@Mapper
public interface ShopMapper extends BaseMapper<Shop> {
}
