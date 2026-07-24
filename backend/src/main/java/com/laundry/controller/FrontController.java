package com.laundry.controller;

import com.laundry.common.PageResult;
import com.laundry.common.Result;
import com.laundry.entity.*;
import com.laundry.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 前台控制器（无需登录）
 */
@Tag(name = "前台接口")
@RestController
@RequestMapping("/front")
public class FrontController {
    
    @Autowired
    private ShopService shopService;
    
    @Autowired
    private LaundryInfoService laundryInfoService;
    
    @Autowired
    private AnnouncementService announcementService;
    
    @Autowired
    private DiscussionService discussionService;
    
    @Autowired
    private ClothesTypeService clothesTypeService;
    
    @Operation(summary = "获取店铺列表")
    @GetMapping("/shops")
    public Result<PageResult<Shop>> getShops(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name) {
        return Result.success(shopService.page(pageNum, pageSize, name, null));
    }
    
    @Operation(summary = "获取店铺详情")
    @GetMapping("/shops/{id}")
    public Result<Shop> getShopDetail(@PathVariable Long id) {
        return Result.success(shopService.getDetail(id));
    }
    
    @Operation(summary = "获取店铺的洗衣服务")
    @GetMapping("/shops/{shopId}/laundry")
    public Result<List<LaundryInfo>> getShopLaundry(@PathVariable Long shopId) {
        return Result.success(laundryInfoService.listByShop(shopId));
    }
    
    @Operation(summary = "获取洗衣服务详情")
    @GetMapping("/laundry/{id}")
    public Result<LaundryInfo> getLaundryDetail(@PathVariable Long id) {
        return Result.success(laundryInfoService.getDetail(id));
    }
    
    @Operation(summary = "获取衣服类型列表")
    @GetMapping("/clothes-types")
    public Result<List<ClothesType>> getClothesTypes() {
        return Result.success(clothesTypeService.listAll(null));
    }
    
    @Operation(summary = "获取公告列表")
    @GetMapping("/announcements")
    public Result<List<Announcement>> getAnnouncements() {
        return Result.success(announcementService.listEnabled());
    }
    
    @Operation(summary = "获取公告详情")
    @GetMapping("/announcements/{id}")
    public Result<Announcement> getAnnouncementDetail(@PathVariable Long id) {
        return Result.success(announcementService.getDetail(id));
    }
    
    @Operation(summary = "获取交流区帖子列表")
    @GetMapping("/discussions")
    public Result<PageResult<Discussion>> getDiscussions(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String title) {
        return Result.success(discussionService.page(pageNum, pageSize, title, null));
    }
    
    @Operation(summary = "获取帖子详情")
    @GetMapping("/discussions/{id}")
    public Result<Discussion> getDiscussionDetail(@PathVariable Long id) {
        return Result.success(discussionService.getDetail(id));
    }
    
    @Operation(summary = "获取帖子评论")
    @GetMapping("/discussions/{id}/comments")
    public Result<List<Comment>> getDiscussionComments(@PathVariable Long id) {
        return Result.success(discussionService.getComments(id));
    }
}
