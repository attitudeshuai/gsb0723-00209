package com.laundry.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.laundry.entity.OrderProgress;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单进度Mapper接口
 */
@Mapper
public interface OrderProgressMapper extends BaseMapper<OrderProgress> {
}
