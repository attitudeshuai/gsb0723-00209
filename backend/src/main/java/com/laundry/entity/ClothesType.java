package com.laundry.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 衣服类型实体类
 */
@Data
@TableName("clothes_type")
public class ClothesType {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long shopId;
    
    private String name;
    
    private String icon;
    
    private BigDecimal price;
    
    private String description;
    
    private Integer sort;
    
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
