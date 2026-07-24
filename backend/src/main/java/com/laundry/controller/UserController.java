package com.laundry.controller;

import com.laundry.common.PageResult;
import com.laundry.common.Result;
import com.laundry.dto.PasswordDTO;
import com.laundry.dto.UserDTO;
import com.laundry.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public Result<UserDTO> getCurrentUser(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(userService.getDetail(userId));
    }
    
    @Operation(summary = "分页查询用户")
    @GetMapping("/page")
    public Result<PageResult<UserDTO>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String role) {
        return Result.success(userService.page(pageNum, pageSize, username, role));
    }
    
    @Operation(summary = "获取用户详情")
    @GetMapping("/{id}")
    public Result<UserDTO> getDetail(@PathVariable Long id) {
        return Result.success(userService.getDetail(id));
    }
    
    @Operation(summary = "更新用户信息")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody UserDTO dto) {
        userService.updateUser(id, dto);
        return Result.success();
    }
    
    @Operation(summary = "更新当前用户信息")
    @PutMapping("/info")
    public Result<Void> updateCurrentUser(@RequestBody UserDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        userService.updateUser(userId, dto);
        return Result.success();
    }
    
    @Operation(summary = "修改密码")
    @PutMapping("/password")
    public Result<Void> updatePassword(@Valid @RequestBody PasswordDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        userService.updatePassword(userId, dto.getOldPassword(), dto.getNewPassword());
        return Result.success();
    }
    
    @Operation(summary = "修改用户状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.updateStatus(id, status);
        return Result.success();
    }
    
    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.removeById(id);
        return Result.success();
    }
}
