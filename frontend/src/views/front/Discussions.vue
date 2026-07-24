<template>
  <div class="discussions-page">
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
          <router-link to="/discussions" class="active">交流区</router-link>
          <router-link to="/announcements">系统公告</router-link>
        </div>
        <div class="nav-actions">
          <template v-if="isLoggedIn">
            <el-button type="text" @click="$router.push('/admin')">后台管理</el-button>
          </template>
          <template v-else>
            <el-button type="text" @click="$router.push('/login')">登录</el-button>
          </template>
        </div>
      </div>
    </nav>

    <div class="page-content">
      <div class="content-container">
        <div class="page-header">
          <h1>交流区</h1>
          <p>分享洗衣心得，交流使用体验</p>
        </div>

        <!-- 搜索和发帖 -->
        <div class="action-bar">
          <el-input
            v-model="searchTitle"
            placeholder="搜索帖子"
            prefix-icon="el-icon-search"
            clearable
            style="width: 300px"
            @keyup.enter.native="loadDiscussions"
            @clear="loadDiscussions"
          />
          <el-button type="primary" icon="el-icon-edit" @click="openPostDialog">发布帖子</el-button>
        </div>

        <!-- 帖子列表 -->
        <div class="discussions-list" v-loading="loading">
          <div
            v-for="item in discussions"
            :key="item.id"
            class="discussion-card"
            @click="$router.push(`/discussion/${item.id}`)"
          >
            <div class="discussion-header">
              <el-avatar :size="40" icon="el-icon-user-solid"></el-avatar>
              <div class="user-info">
                <span class="username">{{ item.userName || '匿名用户' }}</span>
                <span class="time">{{ formatDate(item.createTime) }}</span>
              </div>
            </div>
            <div class="discussion-body">
              <h3>{{ item.title }}</h3>
              <p>{{ item.content }}</p>
            </div>
            <div class="discussion-footer">
              <span class="stat">
                <i class="el-icon-view"></i> {{ item.viewCount }}
              </span>
              <span class="stat">
                <i class="el-icon-star-off"></i> {{ item.likeCount }}
              </span>
              <span class="stat">
                <i class="el-icon-chat-dot-round"></i> {{ item.commentCount }}
              </span>
            </div>
          </div>
        </div>

        <el-empty v-if="!loading && discussions.length === 0" description="暂无帖子" />

        <!-- 分页 -->
        <div class="pagination-wrapper" v-if="total > 0">
          <el-pagination
            background
            layout="prev, pager, next"
            :total="total"
            :page-size="pageSize"
            :current-page.sync="pageNum"
            @current-change="loadDiscussions"
          />
        </div>
      </div>
    </div>

    <!-- 发帖对话框 -->
    <el-dialog title="发布帖子" :visible.sync="postDialogVisible" width="600px">
      <el-form ref="postForm" :model="postForm" :rules="postRules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="postForm.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input
            v-model="postForm.content"
            type="textarea"
            :rows="6"
            placeholder="请输入内容"
          />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="postDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitPost">发布</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { frontApi, discussionApi } from '@/api'

export default {
  name: 'Discussions',
  data() {
    return {
      discussions: [],
      loading: false,
      searchTitle: '',
      pageNum: 1,
      pageSize: 10,
      total: 0,
      postDialogVisible: false,
      postForm: {
        title: '',
        content: ''
      },
      postRules: {
        title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
        content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
      },
      submitting: false
    }
  },
  computed: {
    ...mapGetters(['isLoggedIn'])
  },
  created() {
    this.loadDiscussions()
  },
  methods: {
    async loadDiscussions() {
      this.loading = true
      try {
        const res = await frontApi.getDiscussions({
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          title: this.searchTitle
        })
        this.discussions = res.data.records
        this.total = res.data.total
      } catch (e) {
        // 错误已处理
      } finally {
        this.loading = false
      }
    },
    openPostDialog() {
      if (!this.isLoggedIn) {
        this.$message.warning('请先登录')
        this.$router.push('/login')
        return
      }
      this.postForm = { title: '', content: '' }
      this.postDialogVisible = true
    },
    submitPost() {
      this.$refs.postForm.validate(async valid => {
        if (valid) {
          this.submitting = true
          try {
            await discussionApi.create(this.postForm)
            this.$message.success('发布成功')
            this.postDialogVisible = false
            this.loadDiscussions()
          } catch (e) {
            // 错误已处理
          } finally {
            this.submitting = false
          }
        }
      })
    },
    formatDate(date) {
      if (!date) return ''
      return new Date(date).toLocaleString()
    }
  }
}
</script>

<style lang="scss" scoped>
.discussions-page {
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

.page-header {
  text-align: center;
  margin-bottom: 30px;

  h1 {
    font-size: 32px;
    color: #303133;
    margin-bottom: 10px;
  }

  p {
    color: #909399;
  }
}

.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.discussions-list {
  .discussion-card {
    background: #fff;
    border-radius: 16px;
    padding: 24px;
    margin-bottom: 20px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      transform: translateY(-3px);
      box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
    }

    .discussion-header {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 15px;

      .user-info {
        .username {
          font-weight: 600;
          color: #303133;
          display: block;
        }

        .time {
          font-size: 12px;
          color: #909399;
        }
      }
    }

    .discussion-body {
      h3 {
        font-size: 18px;
        color: #303133;
        margin-bottom: 10px;
      }

      p {
        color: #606266;
        font-size: 14px;
        line-height: 1.6;
        display: -webkit-box;
        -webkit-line-clamp: 3;
        -webkit-box-orient: vertical;
        overflow: hidden;
      }
    }

    .discussion-footer {
      margin-top: 15px;
      padding-top: 15px;
      border-top: 1px solid #eee;
      display: flex;
      gap: 30px;

      .stat {
        color: #909399;
        font-size: 14px;

        i {
          margin-right: 5px;
        }
      }
    }
  }
}

.pagination-wrapper {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}
</style>
