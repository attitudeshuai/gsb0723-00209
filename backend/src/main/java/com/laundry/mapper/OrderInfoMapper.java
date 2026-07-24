package com.laundry.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.laundry.entity.OrderInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单信息Mapper接口
 */
@Mapper
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {
}
