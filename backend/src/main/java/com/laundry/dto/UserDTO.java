package com.laundry.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户信息DTO
 */
@Data
public class UserDTO {
    
    private Long id;
    
    private String username;
    
    private String password;
    
    private String nickname;
    
    private String avatar;
    
    private String phone;
    
    private String email;
    
    private Integer gender;
    
    private String role;
    
    private String address;
    
    private Integer status;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}
