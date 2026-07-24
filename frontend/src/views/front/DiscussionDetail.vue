<template>
  <div class="discussion-detail-page">
    <!-- 导航栏 -->
    <nav class="navbar">
      <div class="nav-container">
        <div class="logo" @click="$router.push('/home')">
          <i class="el-icon-s-cooperation"></i>
          <span>洗衣店管理系统</span>
        </div>
        <div class="nav-links">
          <router-link to="/home">首页</router-link>
          <router-link to="/shops">店铺信息</router-link>
          <router-link to="/discussions">交流区</router-link>
          <router-link to="/announcements">系统公告</router-link>
        </div>
      </div>
    </nav>

    <div class="page-content" v-loading="loading">
      <div class="content-container">
        <div class="back-btn" @click="$router.go(-1)">
          <i class="el-icon-arrow-left"></i> 返回列表
        </div>

        <!-- 帖子详情 -->
        <div class="discussion-card" v-if="discussion">
          <div class="discussion-header">
            <el-avatar :size="50" icon="el-icon-user-solid"></el-avatar>
            <div class="user-info">
              <span class="username">{{ discussion.userName || '匿名用户' }}</span>
              <span class="time">{{ formatDate(discussion.createTime) }}</span>
            </div>
          </div>
          <h1 class="title">{{ discussion.title }}</h1>
          <div class="content">{{ discussion.content }}</div>
          <div class="stats">
            <span><i class="el-icon-view"></i> {{ discussion.viewCount }} 浏览</span>
            <span><i class="el-icon-star-off"></i> {{ discussion.likeCount }} 点赞</span>
            <span><i class="el-icon-chat-dot-round"></i> {{ discussion.commentCount }} 评论</span>
          </div>
          <div class="actions">
            <el-button type="primary" plain @click="handleLike">
              <i class="el-icon-star-off"></i> 点赞
            </el-button>
          </div>
        </div>

        <!-- 评论区 -->
        <div class="comments-section">
          <h2>评论 ({{ comments.length }})</h2>
          
          <!-- 发表评论 -->
          <div class="comment-form" v-if="isLoggedIn">
            <el-input
              v-model="commentContent"
              type="textarea"
              :rows="3"
              placeholder="说说你的看法..."
            />
            <el-button type="primary" @click="submitComment" :loading="submitting">发表评论</el-button>
          </div>
          <div class="login-tip" v-else>
            <el-button type="text" @click="$router.push('/login')">登录</el-button>
            后参与评论
          </div>

          <!-- 评论列表 -->
          <div class="comments-list" v-loading="loadingComments">
            <div v-for="comment in comments" :key="comment.id" class="comment-item">
              <el-avatar :size="40" icon="el-icon-user-solid"></el-avatar>
              <div class="comment-content">
                <div class="comment-header">
                  <span class="username">{{ comment.userName || '匿名用户' }}</span>
                  <span class="time">{{ formatDate(comment.createTime) }}</span>
                </div>
                <p>{{ comment.content }}</p>
              </div>
            </div>
            <el-empty v-if="!loadingComments && comments.length === 0" description="暂无评论" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { frontApi, discussionApi } from '@/api'

export default {
  name: 'DiscussionDetail',
  data() {
    return {
      discussion: null,
      comments: [],
      loading: false,
      loadingComments: false,
      commentContent: '',
      submitting: false
    }
  },
  computed: {
    ...mapGetters(['isLoggedIn'])
  },
  created() {
    this.loadDiscussion()
    this.loadComments()
  },
  methods: {
    async loadDiscussion() {
      const id = this.$route.params.id
      this.loading = true
      try {
        const res = await frontApi.getDiscussionDetail(id)
        this.discussion = res.data
      } catch (e) {
        // 错误已处理
      } finally {
        this.loading = false
      }
    },
    async loadComments() {
      const id = this.$route.params.id
      this.loadingComments = true
      try {
        const res = await frontApi.getDiscussionComments(id)
        this.comments = res.data
      } catch (e) {
        // 错误已处理
      } finally {
        this.loadingComments = false
      }
    },
    async handleLike() {
      if (!this.isLoggedIn) {
        this.$message.warning('请先登录')
        return
      }
      try {
        await discussionApi.like(this.$route.params.id)
        this.$message.success('点赞成功')
        this.discussion.likeCount++
      } catch (e) {
        // 错误已处理
      }
    },
    async submitComment() {
      if (!this.commentContent.trim()) {
        this.$message.warning('请输入评论内容')
        return
      }
      this.submitting = true
      try {
        await discussionApi.addComment(this.$route.params.id, {
          content: this.commentContent
        })
        this.$message.success('评论成功')
        this.commentContent = ''
        this.loadComments()
        this.discussion.commentCount++
      } catch (e) {
        // 错误已处理
      } finally {
        this.submitting = false
      }
    },
    formatDate(date) {
      if (!date) return ''
      return new Date(date).toLocaleString()
    }
  }
}
</script>

<style lang="scss" scoped>
.discussion-detail-page {
  min-height: 100vh;
  background: #f5f7fa;
}

.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 60px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  z-index: 1000;

  .nav-container {
    max-width: 1200px;
    margin: 0 auto;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 20px;
  }

  .logo {
    display: flex;
    align-items: center;
    font-size: 20px;
    font-weight: 700;
    color: #667eea;
    cursor: pointer;

    i {
      font-size: 28px;
      margin-right: 8px;
    }
  }

  .nav-links {
    display: flex;
    gap: 30px;

    a {
      color: #606266;
      text-decoration: none;
      font-weight: 500;

      &:hover, &.active {
        color: #667eea;
      }
    }
  }
}

.page-content {
  padding-top: 80px;
  padding-bottom: 40px;
}

.content-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 20px;
}

.back-btn {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  color: #667eea;
  cursor: pointer;
  margin-bottom: 20px;
  font-weight: 500;

  &:hover {
    text-decoration: underline;
  }
}

.discussion-card {
  background: #fff;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  margin-bottom: 30px;

  .discussion-header {
    display: flex;
    align-items: center;
    gap: 15px;
    margin-bottom: 20px;

    .user-info {
      .username {
        font-weight: 600;
        color: #303133;
        display: block;
        font-size: 16px;
      }

      .time {
        font-size: 13px;
        color: #909399;
      }
    }
  }

  .title {
    font-size: 24px;
    color: #303133;
    margin-bottom: 20px;
  }

  .content {
    color: #606266;
    font-size: 15px;
    line-height: 1.8;
    white-space: pre-wrap;
    margin-bottom: 20px;
  }

  .stats {
    display: flex;
    gap: 30px;
    padding-top: 20px;
    border-top: 1px solid #eee;
    color: #909399;
    font-size: 14px;

    span i {
      margin-right: 5px;
    }
  }

  .actions {
    margin-top: 20px;
  }
}

.comments-section {
  background: #fff;
  border-radius: 16px;
  padding: 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);

  h2 {
    font-size: 18px;
    color: #303133;
    margin-bottom: 20px;
  }

  .comment-form {
    margin-bottom: 30px;

    .el-button {
      margin-top: 10px;
    }
  }

  .login-tip {
    color: #909399;
    margin-bottom: 30px;
  }

  .comments-list {
    .comment-item {
      display: flex;
      gap: 15px;
      padding: 20px 0;
      border-bottom: 1px solid #eee;

      &:last-child {
        border-bottom: none;
      }

      .comment-content {
        flex: 1;

        .comment-header {
          margin-bottom: 10px;

          .username {
            font-weight: 600;
            color: #303133;
            margin-right: 15px;
          }

          .time {
            font-size: 12px;
            color: #909399;
          }
        }

        p {
          color: #606266;
          font-size: 14px;
          line-height: 1.6;
        }
      }
    }
  }
}
</style>
