<template>
  <div class="admin-layout">
    <!-- 侧边栏 -->
    <aside class="sidebar" :class="{ collapsed: isCollapsed }">
      <div class="logo">
        <i class="el-icon-s-cooperation"></i>
        <span v-if="!isCollapsed">洗衣店管理</span>
      </div>
      <el-menu
        :default-active="$route.path"
        :collapse="isCollapsed"
        :router="true"
        background-color="#2c3e50"
        text-color="#bdc3c7"
        active-text-color="#fff"
      >
        <el-menu-item index="/admin/dashboard">
          <i class="el-icon-s-home"></i>
          <span slot="title">首页</span>
        </el-menu-item>
        <el-menu-item index="/admin/profile">
          <i class="el-icon-user"></i>
          <span slot="title">个人中心</span>
        </el-menu-item>
        
        <template v-if="isAdmin">
          <el-menu-item index="/admin/customers">
            <i class="el-icon-s-custom"></i>
            <span slot="title">顾客管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/shop-owners">
            <i class="el-icon-s-shop"></i>
            <span slot="title">店家管理</span>
          </el-menu-item>
        </template>
        
        <el-menu-item index="/admin/shops">
          <i class="el-icon-office-building"></i>
          <span slot="title">店铺信息管理</span>
        </el-menu-item>
        
        <template v-if="isAdmin || isShopOwner">
          <el-menu-item index="/admin/clothes-types">
            <i class="el-icon-s-goods"></i>
            <span slot="title">衣服类型管理</span>
          </el-menu-item>
        </template>
        
        <el-menu-item index="/admin/laundry-info">
          <i class="el-icon-s-order"></i>
          <span slot="title">洗衣信息管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/orders">
          <i class="el-icon-tickets"></i>
          <span slot="title">订单信息管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/order-progress">
          <i class="el-icon-time"></i>
          <span slot="title">订单进度管理</span>
        </el-menu-item>
        
        <template v-if="isAdmin">
          <el-menu-item index="/admin/discussions">
            <i class="el-icon-chat-dot-round"></i>
            <span slot="title">交流区管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/announcements">
            <i class="el-icon-s-flag"></i>
            <span slot="title">系统公告管理</span>
          </el-menu-item>
        </template>
        
        <el-menu-item index="/admin/customer-service">
          <i class="el-icon-service"></i>
          <span slot="title">在线客服</span>
        </el-menu-item>
      </el-menu>
    </aside>

    <!-- 主内容区 -->
    <div class="main-container">
      <!-- 顶部栏 -->
      <header class="header">
        <div class="header-left">
          <i
            :class="isCollapsed ? 'el-icon-s-unfold' : 'el-icon-s-fold'"
            @click="isCollapsed = !isCollapsed"
          ></i>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/admin' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ $route.meta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-button type="text" @click="$router.push('/home')">
            <i class="el-icon-s-home"></i> 前台首页
          </el-button>
          <el-dropdown @command="handleCommand">
            <span class="user-dropdown">
              <el-avatar :size="32" icon="el-icon-user-solid"></el-avatar>
              <span>{{ userInfo.nickname || userInfo.username }}</span>
              <i class="el-icon-arrow-down"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="profile">个人中心</el-dropdown-item>
              <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </header>

      <!-- 内容区 -->
      <main class="content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'AdminLayout',
  data() {
    return {
      isCollapsed: false
    }
  },
  computed: {
    ...mapGetters(['userInfo', 'isAdmin', 'isShopOwner', 'isCustomer'])
  },
  methods: {
    handleCommand(command) {
      if (command === 'profile') {
        this.$router.push('/admin/profile')
      } else if (command === 'logout') {
        this.$store.dispatch('logout')
        this.$message.success('已退出登录')
        this.$router.push('/login')
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
}

.sidebar {
  width: 220px;
  background: #2c3e50;
  transition: width 0.3s;
  flex-shrink: 0;

  &.collapsed {
    width: 64px;

    .logo span {
      display: none;
    }
  }

  .logo {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    font-size: 18px;
    font-weight: 700;
    border-bottom: 1px solid #34495e;

    i {
      font-size: 28px;
      margin-right: 8px;
    }
  }

  .el-menu {
    border-right: none;

    .el-menu-item {
      &.is-active {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
      }

      &:hover {
        background: #34495e !important;
      }
    }
  }
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.header {
  height: 60px;
  background: #fff;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  flex-shrink: 0;

  .header-left {
    display: flex;
    align-items: center;
    gap: 20px;

    > i {
      font-size: 20px;
      cursor: pointer;
      color: #606266;

      &:hover {
        color: #667eea;
      }
    }
  }

  .header-right {
    display: flex;
    align-items: center;
    gap: 20px;

    .user-dropdown {
      display: flex;
      align-items: center;
      gap: 10px;
      cursor: pointer;

      span {
        color: #303133;
      }
    }
  }
}

.content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background: #f5f7fa;
}
</style>
