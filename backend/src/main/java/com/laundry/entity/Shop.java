package com.laundry.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 店铺信息实体类
 */
@Data
@TableName("shop")
public class Shop {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long ownerId;
    
    private String name;
    
    private String logo;
    
    private String description;
    
    private String address;
    
    private String phone;
    
    private String businessHours;
    
    private BigDecimal rating;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    // 非数据库字段
    @TableField(exist = false)
    private String ownerName;
}
