<template>
  <div class="announcements-page">
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
          <router-link to="/announcements" class="active">系统公告</router-link>
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
          <h1>系统公告</h1>
          <p>了解最新动态与通知</p>
        </div>

        <div class="announcements-list" v-loading="loading">
          <div
            v-for="item in announcements"
            :key="item.id"
            class="announcement-card"
            @click="showDetail(item)"
          >
            <div class="announcement-header">
              <el-tag :type="item.type === 1 ? 'danger' : 'info'" size="medium">
                {{ item.type === 1 ? '紧急公告' : '普通公告' }}
              </el-tag>
              <span class="time">{{ formatDate(item.createTime) }}</span>
            </div>
            <h3>{{ item.title }}</h3>
            <p>{{ item.content }}</p>
          </div>
        </div>

        <el-empty v-if="!loading && announcements.length === 0" description="暂无公告" />
      </div>
    </div>

    <!-- 公告详情对话框 -->
    <el-dialog
      :title="currentAnnouncement.title"
      :visible.sync="detailDialogVisible"
      width="600px"
    >
      <div class="announcement-detail">
        <div class="meta">
          <el-tag :type="currentAnnouncement.type === 1 ? 'danger' : 'info'" size="small">
            {{ currentAnnouncement.type === 1 ? '紧急公告' : '普通公告' }}
          </el-tag>
          <span class="time">{{ formatDate(currentAnnouncement.createTime) }}</span>
        </div>
        <div class="content">{{ currentAnnouncement.content }}</div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { frontApi } from '@/api'

export default {
  name: 'Announcements',
  data() {
    return {
      announcements: [],
      loading: false,
      detailDialogVisible: false,
      currentAnnouncement: {}
    }
  },
  computed: {
    ...mapGetters(['isLoggedIn'])
  },
  created() {
    this.loadAnnouncements()
  },
  methods: {
    async loadAnnouncements() {
      this.loading = true
      try {
        const res = await frontApi.getAnnouncements()
        this.announcements = res.data
      } catch (e) {
        // 错误已处理
      } finally {
        this.loading = false
      }
    },
    showDetail(item) {
      this.currentAnnouncement = item
      this.detailDialogVisible = true
    },
    formatDate(date) {
      if (!date) return ''
      return new Date(date).toLocaleString()
    }
  }
}
</script>

<style lang="scss" scoped>
.announcements-page {
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
  margin-bottom: 40px;

  h1 {
    font-size: 32px;
    color: #303133;
    margin-bottom: 10px;
  }

  p {
    color: #909399;
  }
}

.announcements-list {
  .announcement-card {
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

    .announcement-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 15px;

      .time {
        color: #909399;
        font-size: 13px;
      }
    }

    h3 {
      font-size: 18px;
      color: #303133;
      margin-bottom: 12px;
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
}

.announcement-detail {
  .meta {
    margin-bottom: 20px;

    .time {
      color: #909399;
      font-size: 13px;
      margin-left: 15px;
    }
  }

  .content {
    color: #606266;
    font-size: 15px;
    line-height: 1.8;
    white-space: pre-wrap;
  }
}
</style>
