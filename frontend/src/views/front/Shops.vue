<template>
  <div class="shops-page">
    <!-- 导航栏 -->
    <nav class="navbar">
      <div class="nav-container">
        <div class="logo" @click="$router.push('/home')">
          <i class="el-icon-s-cooperation"></i>
          <span>洗衣店管理系统</span>
        </div>
        <div class="nav-links">
          <router-link to="/home">首页</router-link>
          <router-link to="/shops" class="active">店铺信息</router-link>
          <router-link to="/discussions">交流区</router-link>
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
          <h1>店铺信息</h1>
          <p>为您推荐优质洗衣店铺</p>
        </div>

        <!-- 搜索栏 -->
        <div class="search-section">
          <el-input
            v-model="searchName"
            placeholder="搜索店铺名称"
            prefix-icon="el-icon-search"
            clearable
            @keyup.enter.native="loadShops"
            @clear="loadShops"
          />
          <el-button type="primary" @click="loadShops">搜索</el-button>
        </div>

        <!-- 店铺列表 -->
        <div class="shops-grid" v-loading="loading">
          <div
            v-for="shop in shops"
            :key="shop.id"
            class="shop-card"
            @click="$router.push(`/shop/${shop.id}`)"
          >
            <div class="shop-header">
              <div class="shop-logo">
                <i class="el-icon-s-shop"></i>
              </div>
              <el-tag v-if="shop.status === 1" type="success" size="small">营业中</el-tag>
              <el-tag v-else type="info" size="small">已关闭</el-tag>
            </div>
            <div class="shop-body">
              <h3>{{ shop.name }}</h3>
              <p class="description">{{ shop.description || '暂无描述' }}</p>
              <div class="meta-info">
                <div class="meta-item">
                  <i class="el-icon-star-on"></i>
                  <span>{{ shop.rating || 5.0 }}</span>
                </div>
                <div class="meta-item">
                  <i class="el-icon-phone"></i>
                  <span>{{ shop.phone || '暂无' }}</span>
                </div>
              </div>
              <div class="address">
                <i class="el-icon-location"></i>
                <span>{{ shop.address || '暂无地址' }}</span>
              </div>
              <div class="hours">
                <i class="el-icon-time"></i>
                <span>{{ shop.businessHours || '暂无营业时间' }}</span>
              </div>
            </div>
            <div class="shop-footer">
              <el-button type="primary" size="small" round>查看详情</el-button>
            </div>
          </div>
        </div>

        <el-empty v-if="!loading && shops.length === 0" description="暂无店铺信息" />

        <!-- 分页 -->
        <div class="pagination-wrapper" v-if="total > 0">
          <el-pagination
            background
            layout="prev, pager, next"
            :total="total"
            :page-size="pageSize"
            :current-page.sync="pageNum"
            @current-change="loadShops"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { frontApi } from '@/api'

export default {
  name: 'Shops',
  data() {
    return {
      shops: [],
      loading: false,
      searchName: '',
      pageNum: 1,
      pageSize: 8,
      total: 0
    }
  },
  computed: {
    ...mapGetters(['isLoggedIn'])
  },
  created() {
    this.loadShops()
  },
  methods: {
    async loadShops() {
      this.loading = true
      try {
        const res = await frontApi.getShops({
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          name: this.searchName
        })
        this.shops = res.data.records
        this.total = res.data.total
      } catch (e) {
        // 错误已处理
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.shops-page {
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
  max-width: 1200px;
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

.search-section {
  display: flex;
  gap: 15px;
  margin-bottom: 30px;
  max-width: 500px;
  margin-left: auto;
  margin-right: auto;

  .el-input {
    flex: 1;
  }
}

.shops-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;

  @media (max-width: 1200px) {
    grid-template-columns: repeat(3, 1fr);
  }

  @media (max-width: 900px) {
    grid-template-columns: repeat(2, 1fr);
  }

  @media (max-width: 576px) {
    grid-template-columns: 1fr;
  }
}

.shop-card {
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: all 0.3s;

  &:hover {
    transform: translateY(-8px);
    box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
  }

  .shop-header {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    padding: 24px;
    display: flex;
    justify-content: space-between;
    align-items: flex-start;

    .shop-logo {
      width: 60px;
      height: 60px;
      background: rgba(255, 255, 255, 0.2);
      border-radius: 16px;
      display: flex;
      align-items: center;
      justify-content: center;

      i {
        font-size: 32px;
        color: #fff;
      }
    }
  }

  .shop-body {
    padding: 20px;

    h3 {
      font-size: 18px;
      color: #303133;
      margin-bottom: 10px;
    }

    .description {
      color: #909399;
      font-size: 14px;
      margin-bottom: 15px;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
      min-height: 42px;
    }

    .meta-info {
      display: flex;
      gap: 20px;
      margin-bottom: 12px;

      .meta-item {
        display: flex;
        align-items: center;
        gap: 5px;
        font-size: 14px;

        i {
          color: #f5a623;
        }
      }
    }

    .address, .hours {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 13px;
      color: #606266;
      margin-bottom: 8px;

      i {
        color: #909399;
      }
    }
  }

  .shop-footer {
    padding: 0 20px 20px;
    text-align: center;

    .el-button {
      width: 100%;
    }
  }
}

.pagination-wrapper {
  margin-top: 40px;
  display: flex;
  justify-content: center;
}
</style>
