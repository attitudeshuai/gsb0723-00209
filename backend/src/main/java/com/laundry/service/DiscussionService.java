package com.laundry.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.laundry.common.PageResult;
import com.laundry.entity.Comment;
import com.laundry.entity.Discussion;
import com.laundry.entity.User;
import com.laundry.exception.BusinessException;
import com.laundry.mapper.CommentMapper;
import com.laundry.mapper.DiscussionMapper;
import com.laundry.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 交流区服务
 */
@Service
public class DiscussionService extends ServiceImpl<DiscussionMapper, Discussion> {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private CommentMapper commentMapper;
    
    /**
     * 分页查询帖子
     */
    public PageResult<Discussion> page(Integer pageNum, Integer pageSize, String title, Long userId) {
        Page<Discussion> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Discussion> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(title)) {
            wrapper.like(Discussion::getTitle, title);
        }
        if (userId != null) {
            wrapper.eq(Discussion::getUserId, userId);
        }
        wrapper.eq(Discussion::getStatus, 1);
        wrapper.orderByDesc(Discussion::getCreateTime);
        
        Page<Discussion> result = this.page(page, wrapper);
        
        // 填充用户信息
        result.getRecords().forEach(this::fillUserInfo);
        
        return PageResult.from(result);
    }
    
    /**
     * 获取帖子详情
     */
    public Discussion getDetail(Long id) {
        Discussion discussion = this.getById(id);
        if (discussion == null) {
            throw new BusinessException("帖子不存在");
        }
        
        // 增加浏览量
        discussion.setViewCount(discussion.getViewCount() + 1);
        this.updateById(discussion);
        
        fillUserInfo(discussion);
        return discussion;
    }
    
    /**
     * 发布帖子
     */
    public void createDiscussion(Discussion discussion) {
        discussion.setViewCount(0);
        discussion.setLikeCount(0);
        discussion.setCommentCount(0);
        discussion.setStatus(1);
        this.save(discussion);
    }
    
    /**
     * 更新帖子
     */
    public void updateDiscussion(Long id, Discussion discussion) {
        Discussion existing = this.getById(id);
        if (existing == null) {
            throw new BusinessException("帖子不存在");
        }
        
        discussion.setId(id);
        this.updateById(discussion);
    }
    
    /**
     * 删除帖子
     */
    @Transactional
    public void deleteDiscussion(Long id) {
        Discussion discussion = this.getById(id);
        if (discussion == null) {
            throw new BusinessException("帖子不存在");
        }
        
        // 删除相关评论
        commentMapper.delete(new LambdaQueryWrapper<Comment>()
                .eq(Comment::getDiscussionId, id));
        
        this.removeById(id);
    }
    
    /**
     * 点赞帖子
     */
    public void likeDiscussion(Long id) {
        Discussion discussion = this.getById(id);
        if (discussion == null) {
            throw new BusinessException("帖子不存在");
        }
        
        discussion.setLikeCount(discussion.getLikeCount() + 1);
        this.updateById(discussion);
    }
    
    /**
     * 获取帖子评论
     */
    public List<Comment> getComments(Long discussionId) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getDiscussionId, discussionId);
        wrapper.eq(Comment::getStatus, 1);
        wrapper.orderByAsc(Comment::getCreateTime);
        
        List<Comment> comments = commentMapper.selectList(wrapper);
        comments.forEach(comment -> {
            User user = userMapper.selectById(comment.getUserId());
            if (user != null) {
                comment.setUserName(user.getNickname());
                comment.setUserAvatar(user.getAvatar());
            }
        });
        
        return comments;
    }
    
    /**
     * 添加评论
     */
    @Transactional
    public void addComment(Comment comment) {
        Discussion discussion = this.getById(comment.getDiscussionId());
        if (discussion == null) {
            throw new BusinessException("帖子不存在");
        }
        
        comment.setLikeCount(0);
        comment.setStatus(1);
        commentMapper.insert(comment);

        discussion.setCommentCount((discussion.getCommentCount() == null ? 0 : discussion.getCommentCount()) + 1);
        this.updateById(discussion);
    }
    
    private void fillUserInfo(Discussion discussion) {
        User user = userMapper.selectById(discussion.getUserId());
        if (user != null) {
            discussion.setUserName(user.getNickname());
            discussion.setUserAvatar(user.getAvatar());
        }
    }
}
