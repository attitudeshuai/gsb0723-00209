package com.laundry.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 订单进度实体类
 */
@Data
@TableName("order_progress")
public class OrderProgress {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long orderId;
    
    private Integer status;
    
    private String description;
    
    private Long operatorId;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    // 非数据库字段
    @TableField(exist = false)
    private String operatorName;
    
    @TableField(exist = false)
    private String orderNo;
}
