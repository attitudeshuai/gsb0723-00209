package com.laundry.controller;

import com.laundry.common.PageResult;
import com.laundry.common.Result;
import com.laundry.entity.LaundryInfo;
import com.laundry.service.LaundryInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 洗衣信息控制器
 */
@Tag(name = "洗衣信息管理")
@RestController
@RequestMapping("/laundry-info")
public class LaundryInfoController {
    
    @Autowired
    private LaundryInfoService laundryInfoService;
    
    @Operation(summary = "分页查询洗衣信息")
    @GetMapping("/page")
    public Result<PageResult<LaundryInfo>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long shopId,
            @RequestParam(required = false) Long clothesTypeId) {
        return Result.success(laundryInfoService.page(pageNum, pageSize, name, shopId, clothesTypeId));
    }
    
    @Operation(summary = "获取店铺的洗衣服务列表")
    @GetMapping("/list")
    public Result<List<LaundryInfo>> listByShop(@RequestParam Long shopId) {
        return Result.success(laundryInfoService.listByShop(shopId));
    }
    
    @Operation(summary = "获取洗衣信息详情")
    @GetMapping("/{id}")
    public Result<LaundryInfo> getDetail(@PathVariable Long id) {
        return Result.success(laundryInfoService.getDetail(id));
    }
    
    @Operation(summary = "创建洗衣信息")
    @PostMapping
    public Result<Void> create(@RequestBody LaundryInfo info) {
        laundryInfoService.createInfo(info);
        return Result.success();
    }
    
    @Operation(summary = "更新洗衣信息")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody LaundryInfo info) {
        laundryInfoService.updateInfo(id, info);
        return Result.success();
    }
    
    @Operation(summary = "更新状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        laundryInfoService.updateStatus(id, status);
        return Result.success();
    }
    
    @Operation(summary = "删除洗衣信息")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        laundryInfoService.removeById(id);
        return Result.success();
    }
}
