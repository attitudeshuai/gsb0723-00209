package com.laundry.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建订单DTO
 */
@Data
public class OrderCreateDTO {
    
    @NotNull(message = "洗衣服务ID不能为空")
    private Long laundryInfoId;
    
    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量必须大于0")
    private Integer quantity;
    
    private String remark;
    
    private String pickupAddress;
    
    private String deliveryAddress;
}
