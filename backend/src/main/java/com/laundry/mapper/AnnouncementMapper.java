package com.laundry.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.laundry.entity.Announcement;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统公告Mapper接口
 */
@Mapper
public interface AnnouncementMapper extends BaseMapper<Announcement> {
}
