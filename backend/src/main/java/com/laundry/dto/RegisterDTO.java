package com.laundry.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 注册请求DTO
 */
@Data
public class RegisterDTO {
    
    @NotBlank(message = "用户名不能为空")
    private String username;
    
    @NotBlank(message = "密码不能为空")
    private String password;
    
    private String nickname;
    
    private String phone;
    
    private String role;
}
