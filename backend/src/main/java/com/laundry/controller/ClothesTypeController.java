package com.laundry.controller;

import com.laundry.common.PageResult;
import com.laundry.common.Result;
import com.laundry.entity.ClothesType;
import com.laundry.service.ClothesTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 衣服类型控制器
 */
@Tag(name = "衣服类型管理")
@RestController
@RequestMapping("/clothes-type")
public class ClothesTypeController {
    
    @Autowired
    private ClothesTypeService clothesTypeService;
    
    @Operation(summary = "分页查询衣服类型")
    @GetMapping("/page")
    public Result<PageResult<ClothesType>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long shopId) {
        return Result.success(clothesTypeService.page(pageNum, pageSize, name, shopId));
    }
    
    @Operation(summary = "获取所有衣服类型")
    @GetMapping("/list")
    public Result<List<ClothesType>> list(@RequestParam(required = false) Long shopId) {
        return Result.success(clothesTypeService.listAll(shopId));
    }
    
    @Operation(summary = "获取衣服类型详情")
    @GetMapping("/{id}")
    public Result<ClothesType> getDetail(@PathVariable Long id) {
        return Result.success(clothesTypeService.getDetail(id));
    }
    
    @Operation(summary = "创建衣服类型")
    @PostMapping
    public Result<Void> create(@RequestBody ClothesType type) {
        clothesTypeService.createType(type);
        return Result.success();
    }
    
    @Operation(summary = "更新衣服类型")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody ClothesType type) {
        clothesTypeService.updateType(id, type);
        return Result.success();
    }
    
    @Operation(summary = "更新状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        clothesTypeService.updateStatus(id, status);
        return Result.success();
    }
    
    @Operation(summary = "删除衣服类型")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        clothesTypeService.removeById(id);
        return Result.success();
    }
}
