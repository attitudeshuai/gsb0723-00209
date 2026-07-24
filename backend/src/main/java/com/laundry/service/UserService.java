package com.laundry.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.laundry.common.PageResult;
import com.laundry.dto.LoginDTO;
import com.laundry.dto.RegisterDTO;
import com.laundry.dto.UserDTO;
import com.laundry.entity.User;
import com.laundry.exception.BusinessException;
import com.laundry.mapper.UserMapper;
import com.laundry.util.JwtUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户服务
 */
@Service
public class UserService extends ServiceImpl<UserMapper, User> {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 用户登录
     */
    public Map<String, Object> login(LoginDTO dto) {
        User user = this.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, dto.getUsername()));
        
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        if (!user.getPassword().equals(dto.getPassword())) {
            throw new BusinessException("密码错误");
        }
        
        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }
        
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", convertToDTO(user));
        return result;
    }
    
    /**
     * 用户注册
     */
    public void register(RegisterDTO dto) {
        // 检查用户名是否已存在
        long count = this.count(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, dto.getUsername()));
        if (count > 0) {
            throw new BusinessException("用户名已存在");
        }
        
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setNickname(dto.getNickname());
        user.setPhone(dto.getPhone());
        user.setRole(dto.getRole() != null ? dto.getRole() : "customer");
        user.setStatus(1);
        
        this.save(user);
    }
    
    /**
     * 分页查询用户
     */
    public PageResult<UserDTO> page(Integer pageNum, Integer pageSize, String username, String role) {
        Page<User> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(username)) {
            wrapper.like(User::getUsername, username)
                   .or().like(User::getNickname, username);
        }
        if (StringUtils.hasText(role)) {
            wrapper.eq(User::getRole, role);
        }
        wrapper.orderByDesc(User::getCreateTime);
        
        Page<User> result = this.page(page, wrapper);
        
        Page<UserDTO> dtoPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        dtoPage.setRecords(result.getRecords().stream().map(this::convertToDTO).toList());
        
        return PageResult.from(dtoPage);
    }
    
    /**
     * 获取用户详情
     */
    public UserDTO getDetail(Long id) {
        User user = this.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return convertToDTO(user);
    }
    
    /**
     * 更新用户
     */
    public void updateUser(Long id, UserDTO dto) {
        User user = this.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        if (StringUtils.hasText(dto.getNickname())) {
            user.setNickname(dto.getNickname());
        }
        if (StringUtils.hasText(dto.getPhone())) {
            user.setPhone(dto.getPhone());
        }
        if (StringUtils.hasText(dto.getEmail())) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getGender() != null) {
            user.setGender(dto.getGender());
        }
        if (StringUtils.hasText(dto.getAddress())) {
            user.setAddress(dto.getAddress());
        }
        if (StringUtils.hasText(dto.getAvatar())) {
            user.setAvatar(dto.getAvatar());
        }
        
        this.updateById(user);
    }
    
    /**
     * 修改密码
     */
    public void updatePassword(Long id, String oldPassword, String newPassword) {
        User user = this.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        if (!user.getPassword().equals(oldPassword)) {
            throw new BusinessException("原密码错误");
        }
        
        user.setPassword(newPassword);
        this.updateById(user);
    }
    
    /**
     * 修改用户状态
     */
    public void updateStatus(Long id, Integer status) {
        User user = this.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        user.setStatus(status);
        this.updateById(user);
    }
    
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(user, dto);
        dto.setPassword(null);
        return dto;
    }
}
