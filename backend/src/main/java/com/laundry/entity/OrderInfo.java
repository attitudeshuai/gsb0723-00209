package com.laundry.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单信息实体类
 */
@Data
@TableName("order_info")
public class OrderInfo {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String orderNo;
    
    private Long customerId;
    
    private Long shopId;
    
    private Long laundryInfoId;
    
    private Integer quantity;
    
    private BigDecimal totalPrice;
    
    private Integer status;
    
    private String remark;
    
    private String pickupAddress;
    
    private String deliveryAddress;
    
    private LocalDateTime pickupTime;
    
    private LocalDateTime deliveryTime;
    
    private LocalDateTime paymentTime;
    
    private LocalDateTime completeTime;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    // 非数据库字段
    @TableField(exist = false)
    private String customerName;
    
    @TableField(exist = false)
    private String shopName;
    
    @TableField(exist = false)
    private String laundryName;
}
