<template>
  <div class="home-page">
    <!-- 导航栏 -->
    <nav class="navbar">
      <div class="nav-container">
        <div class="logo">
          <i class="el-icon-s-cooperation"></i>
          <span>洗衣店管理系统</span>
        </div>
        <div class="nav-links">
          <router-link to="/home" class="active">首页</router-link>
          <router-link to="/shops">店铺信息</router-link>
          <router-link to="/discussions">交流区</router-link>
          <router-link to="/announcements">系统公告</router-link>
        </div>
        <div class="nav-actions">
          <template v-if="isLoggedIn">
            <el-dropdown @command="handleCommand">
              <span class="user-info">
                <el-avatar :size="32" icon="el-icon-user-solid"></el-avatar>
                <span>{{ userInfo.nickname || userInfo.username }}</span>
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="admin">后台管理</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </template>
          <template v-else>
            <el-button type="text" @click="$router.push('/login')">登录</el-button>
            <el-button type="primary" size="small" round @click="$router.push('/register')">注册</el-button>
          </template>
        </div>
      </div>
    </nav>

    <!-- 轮播图区域 -->
    <section class="hero-section">
      <div class="hero-content">
        <h1>专业洗衣服务</h1>
        <p>让您的衣物焕然一新，享受便捷生活</p>
        <div class="hero-buttons">
          <el-button type="primary" size="large" round @click="$router.push('/shops')">
            查看店铺
          </el-button>
          <el-button size="large" round plain @click="$router.push('/register')">
            立即注册
          </el-button>
        </div>
      </div>
      <div class="hero-image">
        <div class="floating-card card-1">
          <i class="el-icon-s-order"></i>
          <span>在线下单</span>
        </div>
        <div class="floating-card card-2">
          <i class="el-icon-truck"></i>
          <span>上门取件</span>
        </div>
        <div class="floating-card card-3">
          <i class="el-icon-star-on"></i>
          <span>品质保证</span>
        </div>
      </div>
    </section>

    <!-- 特色服务 -->
    <section class="features-section">
      <div class="section-container">
        <h2 class="section-title">我们的优势</h2>
        <div class="features-grid">
          <div class="feature-card">
            <div class="feature-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
              <i class="el-icon-time"></i>
            </div>
            <h3>快速便捷</h3>
            <p>在线下单，快速响应，节省您的宝贵时间</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon" style="background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);">
              <i class="el-icon-medal"></i>
            </div>
            <h3>品质保证</h3>
            <p>专业设备，优质洗涤剂，呵护每一件衣物</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
              <i class="el-icon-location"></i>
            </div>
            <h3>上门服务</h3>
            <p>免费上门取送，足不出户享受服务</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
              <i class="el-icon-money"></i>
            </div>
            <h3>价格透明</h3>
            <p>明码标价，无隐藏收费，放心消费</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 热门店铺 -->
    <section class="shops-section">
      <div class="section-container">
        <h2 class="section-title">热门店铺</h2>
        <div class="shops-grid" v-loading="loadingShops">
          <div
            v-for="shop in shops"
            :key="shop.id"
            class="shop-card"
            @click="$router.push(`/shop/${shop.id}`)"
          >
            <div class="shop-logo">
              <i class="el-icon-s-shop"></i>
            </div>
            <div class="shop-info">
              <h3>{{ shop.name }}</h3>
              <p class="shop-desc">{{ shop.description || '暂无描述' }}</p>
              <div class="shop-meta">
                <span class="rating">
                  <i class="el-icon-star-on"></i>
                  {{ shop.rating }}
                </span>
                <span class="address">
                  <i class="el-icon-location"></i>
                  {{ shop.address || '暂无地址' }}
                </span>
              </div>
            </div>
          </div>
        </div>
        <div class="section-footer">
          <el-button type="text" @click="$router.push('/shops')">
            查看更多店铺 <i class="el-icon-arrow-right"></i>
          </el-button>
        </div>
      </div>
    </section>

    <!-- 最新公告 -->
    <section class="announcements-section">
      <div class="section-container">
        <h2 class="section-title">最新公告</h2>
        <div class="announcements-list" v-loading="loadingAnnouncements">
          <div
            v-for="item in announcements"
            :key="item.id"
            class="announcement-item"
          >
            <el-tag :type="item.type === 1 ? 'danger' : 'info'" size="small">
              {{ item.type === 1 ? '紧急' : '普通' }}
            </el-tag>
            <span class="title">{{ item.title }}</span>
            <span class="time">{{ formatDate(item.createTime) }}</span>
          </div>
          <el-empty v-if="announcements.length === 0" description="暂无公告" />
        </div>
      </div>
    </section>

    <!-- 页脚 -->
    <footer class="footer">
      <div class="footer-container">
        <div class="footer-info">
          <h3>洗衣店管理系统</h3>
          <p>专业、便捷、高效的洗衣服务平台</p>
        </div>
        <div class="footer-links">
          <h4>快速链接</h4>
          <router-link to="/shops">店铺信息</router-link>
          <router-link to="/discussions">交流区</router-link>
          <router-link to="/announcements">系统公告</router-link>
        </div>
        <div class="footer-contact">
          <h4>联系我们</h4>
          <p><i class="el-icon-phone"></i> 400-888-8888</p>
          <p><i class="el-icon-message"></i> service@laundry.com</p>
        </div>
      </div>
      <div class="footer-bottom">
        <p>© 2024 洗衣店管理系统 All Rights Reserved</p>
      </div>
    </footer>

    <!-- 在线客服浮窗 -->
    <div class="customer-service-btn" @click="openCustomerService">
      <i class="el-icon-service"></i>
    </div>

    <!-- 客服对话框 -->
    <el-dialog
      title="在线客服"
      :visible.sync="showServiceDialog"
      width="400px"
      :modal-append-to-body="false"
    >
      <div class="service-dialog">
        <div class="messages-container">
          <div
            v-for="(msg, index) in messages"
            :key="index"
            :class="['message', msg.type === 0 ? 'user' : 'admin']"
          >
            <div class="message-content">{{ msg.content }}</div>
            <div class="message-time">{{ formatDate(msg.createTime) }}</div>
          </div>
          <el-empty v-if="messages.length === 0" description="暂无消息" />
        </div>
        <div class="input-container">
          <el-input
            v-model="messageInput"
            placeholder="请输入消息..."
            @keyup.enter.native="sendMessage"
          />
          <el-button type="primary" @click="sendMessage">发送</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { frontApi, customerServiceApi } from '@/api'

export default {
  name: 'Home',
  data() {
    return {
      shops: [],
      announcements: [],
      loadingShops: false,
      loadingAnnouncements: false,
      showServiceDialog: false,
      messages: [],
      messageInput: ''
    }
  },
  computed: {
    ...mapGetters(['isLoggedIn', 'userInfo'])
  },
  created() {
    this.loadShops()
    this.loadAnnouncements()
  },
  methods: {
    async loadShops() {
      this.loadingShops = true
      try {
        const res = await frontApi.getShops({ pageNum: 1, pageSize: 4 })
        this.shops = res.data.records
      } catch (e) {
        // 错误已处理
      } finally {
        this.loadingShops = false
      }
    },
    async loadAnnouncements() {
      this.loadingAnnouncements = true
      try {
        const res = await frontApi.getAnnouncements()
        this.announcements = res.data.slice(0, 5)
      } catch (e) {
        // 错误已处理
      } finally {
        this.loadingAnnouncements = false
      }
    },
    async openCustomerService() {
      if (!this.isLoggedIn) {
        this.$message.warning('请先登录')
        this.$router.push('/login')
        return
      }
      this.showServiceDialog = true
      await this.loadMessages()
    },
    async loadMessages() {
      try {
        const res = await customerServiceApi.getMyMessages()
        this.messages = res.data
      } catch (e) {
        // 错误已处理
      }
    },
    async sendMessage() {
      if (!this.messageInput.trim()) return
      try {
        await customerServiceApi.send({ content: this.messageInput })
        this.messageInput = ''
        await this.loadMessages()
      } catch (e) {
        // 错误已处理
      }
    },
    handleCommand(command) {
      if (command === 'admin') {
        this.$router.push('/admin')
      } else if (command === 'logout') {
        this.$store.dispatch('logout')
        this.$message.success('已退出登录')
      }
    },
    formatDate(date) {
      if (!date) return ''
      return new Date(date).toLocaleDateString()
    }
  }
}
</script>

<style lang="scss" scoped>
.home-page {
  min-height: 100vh;
  background: #fff;
}

// 导航栏
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
      transition: color 0.3s;

      &:hover, &.active {
        color: #667eea;
      }
    }
  }

  .nav-actions {
    display: flex;
    align-items: center;
    gap: 15px;

    .user-info {
      display: flex;
      align-items: center;
      gap: 8px;
      cursor: pointer;
    }
  }
}

// 英雄区域
.hero-section {
  padding: 120px 20px 80px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 500px;
  position: relative;
  overflow: hidden;

  .hero-content {
    max-width: 500px;
    color: #fff;
    z-index: 1;

    h1 {
      font-size: 48px;
      font-weight: 700;
      margin-bottom: 20px;
    }

    p {
      font-size: 18px;
      opacity: 0.9;
      margin-bottom: 30px;
    }

    .hero-buttons {
      display: flex;
      gap: 15px;
    }
  }

  .hero-image {
    position: relative;
    width: 400px;
    height: 400px;

    .floating-card {
      position: absolute;
      background: rgba(255, 255, 255, 0.95);
      padding: 20px 30px;
      border-radius: 16px;
      box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
      display: flex;
      align-items: center;
      gap: 12px;
      animation: float 3s ease-in-out infinite;

      i {
        font-size: 24px;
        color: #667eea;
      }

      span {
        font-weight: 600;
        color: #303133;
      }

      &.card-1 {
        top: 50px;
        left: 0;
      }

      &.card-2 {
        top: 150px;
        right: 0;
        animation-delay: 0.5s;
      }

      &.card-3 {
        bottom: 50px;
        left: 50px;
        animation-delay: 1s;
      }
    }
  }
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

// 通用区域样式
.section-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.section-title {
  text-align: center;
  font-size: 32px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 40px;
  position: relative;

  &::after {
    content: '';
    position: absolute;
    bottom: -10px;
    left: 50%;
    transform: translateX(-50%);
    width: 60px;
    height: 4px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 2px;
  }
}

// 特色服务
.features-section {
  padding: 80px 20px;
  background: #f5f7fa;

  .features-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 30px;

    @media (max-width: 992px) {
      grid-template-columns: repeat(2, 1fr);
    }

    @media (max-width: 576px) {
      grid-template-columns: 1fr;
    }
  }

  .feature-card {
    background: #fff;
    padding: 40px 30px;
    border-radius: 16px;
    text-align: center;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    transition: all 0.3s;

    &:hover {
      transform: translateY(-10px);
      box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
    }

    .feature-icon {
      width: 70px;
      height: 70px;
      border-radius: 20px;
      display: flex;
      align-items: center;
      justify-content: center;
      margin: 0 auto 20px;

      i {
        font-size: 32px;
        color: #fff;
      }
    }

    h3 {
      font-size: 18px;
      color: #303133;
      margin-bottom: 10px;
    }

    p {
      color: #909399;
      font-size: 14px;
      line-height: 1.6;
    }
  }
}

// 热门店铺
.shops-section {
  padding: 80px 20px;

  .shops-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 30px;

    @media (max-width: 768px) {
      grid-template-columns: 1fr;
    }
  }

  .shop-card {
    display: flex;
    background: #fff;
    border-radius: 16px;
    padding: 24px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
    }

    .shop-logo {
      width: 80px;
      height: 80px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border-radius: 16px;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 20px;
      flex-shrink: 0;

      i {
        font-size: 36px;
        color: #fff;
      }
    }

    .shop-info {
      flex: 1;

      h3 {
        font-size: 18px;
        color: #303133;
        margin-bottom: 8px;
      }

      .shop-desc {
        color: #909399;
        font-size: 14px;
        margin-bottom: 12px;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
      }

      .shop-meta {
        display: flex;
        gap: 20px;
        font-size: 13px;
        color: #606266;

        .rating {
          color: #f5a623;

          i {
            margin-right: 4px;
          }
        }
      }
    }
  }

  .section-footer {
    text-align: center;
    margin-top: 30px;
  }
}

// 公告区域
.announcements-section {
  padding: 80px 20px;
  background: #f5f7fa;

  .announcements-list {
    max-width: 800px;
    margin: 0 auto;
    background: #fff;
    border-radius: 16px;
    padding: 20px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  }

  .announcement-item {
    display: flex;
    align-items: center;
    padding: 15px 0;
    border-bottom: 1px solid #eee;

    &:last-child {
      border-bottom: none;
    }

    .el-tag {
      margin-right: 15px;
    }

    .title {
      flex: 1;
      color: #303133;
    }

    .time {
      color: #909399;
      font-size: 13px;
    }
  }
}

// 页脚
.footer {
  background: #2c3e50;
  color: #fff;
  padding: 60px 20px 20px;

  .footer-container {
    max-width: 1200px;
    margin: 0 auto;
    display: grid;
    grid-template-columns: 2fr 1fr 1fr;
    gap: 60px;

    @media (max-width: 768px) {
      grid-template-columns: 1fr;
      gap: 30px;
    }
  }

  h3 {
    font-size: 20px;
    margin-bottom: 15px;
  }

  h4 {
    font-size: 16px;
    margin-bottom: 15px;
  }

  p {
    color: rgba(255, 255, 255, 0.7);
    margin-bottom: 10px;
  }

  .footer-links {
    a {
      display: block;
      color: rgba(255, 255, 255, 0.7);
      text-decoration: none;
      margin-bottom: 10px;

      &:hover {
        color: #fff;
      }
    }
  }

  .footer-bottom {
    text-align: center;
    padding-top: 30px;
    margin-top: 40px;
    border-top: 1px solid rgba(255, 255, 255, 0.1);

    p {
      font-size: 13px;
    }
  }
}

// 客服按钮
.customer-service-btn {
  position: fixed;
  right: 30px;
  bottom: 30px;
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.4);
  transition: all 0.3s;
  z-index: 999;

  i {
    font-size: 24px;
    color: #fff;
  }

  &:hover {
    transform: scale(1.1);
  }
}

// 客服对话框
.service-dialog {
  .messages-container {
    height: 300px;
    overflow-y: auto;
    padding: 10px;
    background: #f5f7fa;
    border-radius: 8px;
    margin-bottom: 15px;

    .message {
      margin-bottom: 15px;

      &.user {
        text-align: right;

        .message-content {
          background: #667eea;
          color: #fff;
        }
      }

      &.admin {
        text-align: left;

        .message-content {
          background: #fff;
        }
      }

      .message-content {
        display: inline-block;
        padding: 10px 15px;
        border-radius: 12px;
        max-width: 80%;
      }

      .message-time {
        font-size: 12px;
        color: #909399;
        margin-top: 5px;
      }
    }
  }

  .input-container {
    display: flex;
    gap: 10px;
  }
}

@media (max-width: 768px) {
  .navbar {
    .nav-links {
      display: none;
    }
  }

  .hero-section {
    flex-direction: column;
    text-align: center;

    .hero-content h1 {
      font-size: 32px;
    }

    .hero-image {
      display: none;
    }
  }
}
</style>
