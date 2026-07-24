package com.laundry.controller;

import com.laundry.common.PageResult;
import com.laundry.common.Result;
import com.laundry.entity.Shop;
import com.laundry.service.ShopService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 店铺控制器
 */
@Tag(name = "店铺管理")
@RestController
@RequestMapping("/shop")
public class ShopController {
    
    @Autowired
    private ShopService shopService;
    
    @Operation(summary = "分页查询店铺")
    @GetMapping("/page")
    public Result<PageResult<Shop>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long ownerId) {
        return Result.success(shopService.page(pageNum, pageSize, name, ownerId));
    }
    
    @Operation(summary = "获取店铺详情")
    @GetMapping("/{id}")
    public Result<Shop> getDetail(@PathVariable Long id) {
        return Result.success(shopService.getDetail(id));
    }
    
    @Operation(summary = "获取当前店家的店铺")
    @GetMapping("/my")
    public Result<Shop> getMyShop(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(shopService.getByOwnerId(userId));
    }
    
    @Operation(summary = "创建店铺")
    @PostMapping
    public Result<Void> create(@RequestBody Shop shop, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        shop.setOwnerId(userId);
        shopService.createShop(shop);
        return Result.success();
    }
    
    @Operation(summary = "更新店铺")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Shop shop) {
        shopService.updateShop(id, shop);
        return Result.success();
    }
    
    @Operation(summary = "更新店铺状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        shopService.updateStatus(id, status);
        return Result.success();
    }
    
    @Operation(summary = "删除店铺")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        shopService.removeById(id);
        return Result.success();
    }
}
