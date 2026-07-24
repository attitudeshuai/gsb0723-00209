<template>
  <div class="order-progress-page">
    <h2 class="page-title">订单进度管理</h2>

    <div class="search-bar">
      <el-input v-model="searchParams.orderId" placeholder="订单ID" clearable style="width: 150px" type="number" />
      <el-button type="primary" @click="loadData">搜索</el-button>
      <el-button @click="resetSearch">重置</el-button>
    </div>

    <div class="table-container">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="orderNo" label="订单编号" width="160" />
        <el-table-column prop="status" label="进度状态" width="120">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)" size="small">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" />
        <el-table-column prop="operatorName" label="操作人" width="120" />
        <el-table-column prop="createTime" label="时间" width="180">
          <template slot-scope="scope">{{ formatDate(scope.row.createTime) }}</template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination background layout="total, prev, pager, next" :total="total" :page-size="pageSize" :current-page.sync="pageNum" @current-change="loadData" />
      </div>
    </div>
  </div>
</template>

<script>
import { orderProgressApi } from '@/api'

export default {
  name: 'OrderProgress',
  data() {
    return {
      tableData: [], loading: false,
      searchParams: { orderId: '' },
      pageNum: 1, pageSize: 10, total: 0
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const params = { pageNum: this.pageNum, pageSize: this.pageSize }
        if (this.searchParams.orderId) params.orderId = this.searchParams.orderId
        const res = await orderProgressApi.getPage(params)
        this.tableData = res.data.records
        this.total = res.data.total
      } catch (e) {} finally { this.loading = false }
    },
    resetSearch() {
      this.searchParams = { orderId: '' }
      this.pageNum = 1
      this.loadData()
    },
    getStatusType(status) {
      return ['info', 'primary', 'warning', '', '', 'info', 'success'][status] || 'info'
    },
    getStatusText(status) {
      return ['已下单', '已取件', '洗涤中', '已烘干', '已整理', '配送中', '已送达'][status] || '未知'
    },
    formatDate(date) {
      return date ? new Date(date).toLocaleString() : ''
    }
  }
}
</script>

<style lang="scss" scoped>
.order-progress-page { .page-title { font-size: 24px; color: #303133; margin-bottom: 20px; } }
.search-bar { background: #fff; padding: 20px; border-radius: 12px; margin-bottom: 20px; display: flex; gap: 15px; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06); }
.table-container { background: #fff; border-radius: 12px; padding: 20px; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06); }
.pagination-container { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>
