package com.laundry.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.laundry.entity.ClothesType;
import org.apache.ibatis.annotations.Mapper;

/**
 * 衣服类型Mapper接口
 */
@Mapper
public interface ClothesTypeMapper extends BaseMapper<ClothesType> {
}
