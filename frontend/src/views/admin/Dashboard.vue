<template>
  <div class="dashboard-page">
    <h2 class="page-title">数据概览</h2>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
          <i class="el-icon-tickets"></i>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ statistics.totalOrders || 0 }}</span>
          <span class="stat-label">总订单数</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
          <i class="el-icon-time"></i>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ statistics.pendingOrders || 0 }}</span>
          <span class="stat-label">待处理订单</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
          <i class="el-icon-loading"></i>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ statistics.processingOrders || 0 }}</span>
          <span class="stat-label">进行中订单</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon" style="background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);">
          <i class="el-icon-circle-check"></i>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ statistics.completedOrders || 0 }}</span>
          <span class="stat-label">已完成订单</span>
        </div>
      </div>
    </div>

    <!-- 快捷操作 -->
    <div class="section">
      <h3 class="section-title">快捷操作</h3>
      <div class="quick-actions">
        <div class="action-card" @click="$router.push('/admin/orders')">
          <i class="el-icon-tickets"></i>
          <span>订单管理</span>
        </div>
        <div class="action-card" @click="$router.push('/admin/shops')">
          <i class="el-icon-s-shop"></i>
          <span>店铺管理</span>
        </div>
        <div class="action-card" @click="$router.push('/admin/laundry-info')">
          <i class="el-icon-s-goods"></i>
          <span>洗衣服务</span>
        </div>
        <div class="action-card" @click="$router.push('/admin/customer-service')">
          <i class="el-icon-service"></i>
          <span>在线客服</span>
        </div>
      </div>
    </div>

    <!-- 最近订单 -->
    <div class="section">
      <h3 class="section-title">最近订单</h3>
      <div class="table-container">
        <el-table :data="recentOrders" v-loading="loadingOrders" stripe>
          <el-table-column prop="orderNo" label="订单编号" width="180" />
          <el-table-column prop="customerName" label="顾客" width="120" />
          <el-table-column prop="shopName" label="店铺" width="150" />
          <el-table-column prop="laundryName" label="服务" />
          <el-table-column prop="totalPrice" label="金额" width="100">
            <template slot-scope="scope">
              ¥{{ scope.row.totalPrice }}
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template slot-scope="scope">
              <el-tag :type="getStatusType(scope.row.status)" size="small">
                {{ getStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="下单时间" width="180">
            <template slot-scope="scope">
              {{ formatDate(scope.row.createTime) }}
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<script>
import { orderApi } from '@/api'
import { mapGetters } from 'vuex'

export default {
  name: 'Dashboard',
  data() {
    return {
      statistics: {},
      recentOrders: [],
      loadingOrders: false
    }
  },
  computed: {
    ...mapGetters(['isAdmin', 'isShopOwner', 'userInfo'])
  },
  created() {
    this.loadStatistics()
    this.loadRecentOrders()
  },
  methods: {
    async loadStatistics() {
      try {
        const res = await orderApi.getStatistics()
        this.statistics = res.data
      } catch (e) {
        // 错误已处理
      }
    },
    async loadRecentOrders() {
      this.loadingOrders = true
      try {
        const params = { pageNum: 1, pageSize: 5 }
        const res = this.isAdmin 
          ? await orderApi.getPage(params)
          : await orderApi.getMyOrders(params)
        this.recentOrders = res.data.records
      } catch (e) {
        // 错误已处理
      } finally {
        this.loadingOrders = false
      }
    },
    getStatusType(status) {
      const types = ['warning', 'primary', 'info', 'success', 'danger']
      return types[status] || 'info'
    },
    getStatusText(status) {
      const texts = ['待支付', '已支付', '洗涤中', '已完成', '已取消']
      return texts[status] || '未知'
    },
    formatDate(date) {
      if (!date) return ''
      return new Date(date).toLocaleString()
    }
  }
}
</script>

<style lang="scss" scoped>
.dashboard-page {
  .page-title {
    font-size: 24px;
    color: #303133;
    margin-bottom: 30px;
  }
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 30px;

  @media (max-width: 1200px) {
    grid-template-columns: repeat(2, 1fr);
  }

  @media (max-width: 576px) {
    grid-template-columns: 1fr;
  }
}

.stat-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
  }

  .stat-icon {
    width: 60px;
    height: 60px;
    border-radius: 16px;
    display: flex;
    align-items: center;
    justify-content: center;

    i {
      font-size: 28px;
      color: #fff;
    }
  }

  .stat-info {
    .stat-value {
      display: block;
      font-size: 32px;
      font-weight: 700;
      color: #303133;
    }

    .stat-label {
      color: #909399;
      font-size: 14px;
    }
  }
}

.section {
  margin-bottom: 30px;

  .section-title {
    font-size: 18px;
    color: #303133;
    margin-bottom: 20px;
    padding-left: 12px;
    border-left: 4px solid #667eea;
  }
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;

  @media (max-width: 992px) {
    grid-template-columns: repeat(2, 1fr);
  }

  @media (max-width: 576px) {
    grid-template-columns: 1fr;
  }

  .action-card {
    background: #fff;
    border-radius: 16px;
    padding: 30px;
    text-align: center;
    cursor: pointer;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    transition: all 0.3s;

    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);

      i {
        color: #667eea;
      }
    }

    i {
      font-size: 40px;
      color: #909399;
      margin-bottom: 15px;
      transition: color 0.3s;
    }

    span {
      display: block;
      font-size: 16px;
      color: #303133;
      font-weight: 500;
    }
  }
}

.table-container {
  background: #fff;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}
</style>
