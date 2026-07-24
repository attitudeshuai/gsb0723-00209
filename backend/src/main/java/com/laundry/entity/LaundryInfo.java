package com.laundry.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 洗衣信息实体类
 */
@Data
@TableName("laundry_info")
public class LaundryInfo {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long shopId;
    
    private Long clothesTypeId;
    
    private String name;
    
    private String image;
    
    private BigDecimal price;
    
    private BigDecimal originalPrice;
    
    private String description;
    
    private String washMethod;
    
    private String deliveryTime;
    
    private Integer sort;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    // 非数据库字段
    @TableField(exist = false)
    private String shopName;
    
    @TableField(exist = false)
    private String clothesTypeName;
}
