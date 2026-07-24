package com.laundry.controller;

import com.laundry.common.PageResult;
import com.laundry.common.Result;
import com.laundry.entity.Announcement;
import com.laundry.service.AnnouncementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统公告控制器
 */
@Tag(name = "系统公告管理")
@RestController
@RequestMapping("/announcement")
public class AnnouncementController {
    
    @Autowired
    private AnnouncementService announcementService;
    
    @Operation(summary = "分页查询公告")
    @GetMapping("/page")
    public Result<PageResult<Announcement>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer type) {
        return Result.success(announcementService.page(pageNum, pageSize, title, type));
    }
    
    @Operation(summary = "获取启用的公告列表")
    @GetMapping("/list")
    public Result<List<Announcement>> list() {
        return Result.success(announcementService.listEnabled());
    }
    
    @Operation(summary = "获取公告详情")
    @GetMapping("/{id}")
    public Result<Announcement> getDetail(@PathVariable Long id) {
        return Result.success(announcementService.getDetail(id));
    }
    
    @Operation(summary = "创建公告")
    @PostMapping
    public Result<Void> create(@RequestBody Announcement announcement) {
        announcementService.createAnnouncement(announcement);
        return Result.success();
    }
    
    @Operation(summary = "更新公告")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Announcement announcement) {
        announcementService.updateAnnouncement(id, announcement);
        return Result.success();
    }
    
    @Operation(summary = "更新状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        announcementService.updateStatus(id, status);
        return Result.success();
    }
    
    @Operation(summary = "删除公告")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        announcementService.removeById(id);
        return Result.success();
    }
}
