package com.laundry.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 在线客服消息实体类
 */
@Data
@TableName("customer_service")
public class CustomerService {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Long adminId;
    
    private String content;
    
    private Integer type;
    
    private Integer isRead;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    // 非数据库字段
    @TableField(exist = false)
    private String userName;
    
    @TableField(exist = false)
    private String adminName;
}
