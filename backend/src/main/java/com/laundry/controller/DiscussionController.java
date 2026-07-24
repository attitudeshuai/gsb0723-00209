package com.laundry.controller;

import com.laundry.common.PageResult;
import com.laundry.common.Result;
import com.laundry.entity.Comment;
import com.laundry.entity.Discussion;
import com.laundry.service.DiscussionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 交流区控制器
 */
@Tag(name = "交流区管理")
@RestController
@RequestMapping("/discussion")
public class DiscussionController {
    
    @Autowired
    private DiscussionService discussionService;
    
    @Operation(summary = "分页查询帖子")
    @GetMapping("/page")
    public Result<PageResult<Discussion>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Long userId) {
        return Result.success(discussionService.page(pageNum, pageSize, title, userId));
    }
    
    @Operation(summary = "获取帖子详情")
    @GetMapping("/{id}")
    public Result<Discussion> getDetail(@PathVariable Long id) {
        return Result.success(discussionService.getDetail(id));
    }
    
    @Operation(summary = "获取帖子评论")
    @GetMapping("/{id}/comments")
    public Result<List<Comment>> getComments(@PathVariable Long id) {
        return Result.success(discussionService.getComments(id));
    }
    
    @Operation(summary = "发布帖子")
    @PostMapping
    public Result<Void> create(@RequestBody Discussion discussion, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        discussion.setUserId(userId);
        discussionService.createDiscussion(discussion);
        return Result.success();
    }
    
    @Operation(summary = "更新帖子")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Discussion discussion) {
        discussionService.updateDiscussion(id, discussion);
        return Result.success();
    }
    
    @Operation(summary = "删除帖子")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        discussionService.deleteDiscussion(id);
        return Result.success();
    }
    
    @Operation(summary = "点赞帖子")
    @PostMapping("/{id}/like")
    public Result<Void> like(@PathVariable Long id) {
        discussionService.likeDiscussion(id);
        return Result.success();
    }
    
    @Operation(summary = "添加评论")
    @PostMapping("/{id}/comment")
    public Result<Void> addComment(@PathVariable Long id, @RequestBody Comment comment, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        comment.setDiscussionId(id);
        comment.setUserId(userId);
        discussionService.addComment(comment);
        return Result.success();
    }
}
