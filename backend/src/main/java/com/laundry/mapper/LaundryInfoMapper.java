package com.laundry.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.laundry.entity.LaundryInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 洗衣信息Mapper接口
 */
@Mapper
public interface LaundryInfoMapper extends BaseMapper<LaundryInfo> {
}
