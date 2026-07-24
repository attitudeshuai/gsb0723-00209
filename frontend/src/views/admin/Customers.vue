<template>
  <div class="customers-page">
    <h2 class="page-title">顾客管理</h2>

    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input
        v-model="searchParams.username"
        placeholder="搜索用户名/昵称"
        clearable
        style="width: 200px"
      />
      <el-button type="primary" @click="loadData">搜索</el-button>
      <el-button @click="resetSearch">重置</el-button>
    </div>

    <!-- 数据表格 -->
    <div class="table-container">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" min-width="120">
          <template slot-scope="scope">
            {{ scope.row.username || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="nickname" label="昵称" min-width="100">
          <template slot-scope="scope">
            {{ scope.row.nickname || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" min-width="120">
          <template slot-scope="scope">
            {{ scope.row.phone || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="email" label="邮箱" min-width="160">
          <template slot-scope="scope">
            {{ scope.row.email || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="gender" label="性别" width="80">
          <template slot-scope="scope">
            {{ getGenderText(scope.row.gender) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-switch
              v-model="scope.row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="注册时间" width="180">
          <template slot-scope="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="150">
          <template slot-scope="scope">
            <el-button type="text" @click="viewDetail(scope.row)">查看</el-button>
            <el-button type="text" class="danger-btn" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          background
          layout="total, prev, pager, next"
          :total="total"
          :page-size="pageSize"
          :current-page.sync="pageNum"
          @current-change="loadData"
        />
      </div>
    </div>

    <!-- 详情对话框 -->
    <el-dialog title="顾客详情" :visible.sync="detailDialogVisible" width="500px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户名">{{ currentUser.username }}</el-descriptions-item>
        <el-descriptions-item label="昵称">{{ currentUser.nickname }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ currentUser.phone }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ currentUser.email }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ getGenderText(currentUser.gender) }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentUser.status === 1 ? 'success' : 'danger'">
            {{ currentUser.status === 1 ? '正常' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="地址" :span="2">{{ currentUser.address || '未设置' }}</el-descriptions-item>
        <el-descriptions-item label="注册时间" :span="2">{{ formatDate(currentUser.createTime) }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
import { userApi } from '@/api'

export default {
  name: 'Customers',
  data() {
    return {
      tableData: [],
      loading: false,
      searchParams: { username: '' },
      pageNum: 1,
      pageSize: 10,
      total: 0,
      detailDialogVisible: false,
      currentUser: {}
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const res = await userApi.getPage({
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          username: this.searchParams.username,
          role: 'customer'
        })
        this.tableData = res.data.records
        this.total = res.data.total
      } catch (e) {
        // 错误已处理
      } finally {
        this.loading = false
      }
    },
    resetSearch() {
      this.searchParams = { username: '' }
      this.pageNum = 1
      this.loadData()
    },
    viewDetail(row) {
      this.currentUser = row
      this.detailDialogVisible = true
    },
    async handleStatusChange(row) {
      try {
        await userApi.updateStatus(row.id, row.status)
        this.$message.success('状态更新成功')
      } catch (e) {
        row.status = row.status === 1 ? 0 : 1
      }
    },
    handleDelete(row) {
      this.$confirm('确定要删除该顾客吗？', '提示', {
        type: 'warning'
      }).then(async () => {
        try {
          await userApi.delete(row.id)
          this.$message.success('删除成功')
          this.loadData()
        } catch (e) {
          // 错误已处理
        }
      }).catch(() => {})
    },
    getGenderText(gender) {
      return { 0: '未知', 1: '男', 2: '女' }[gender] || '未知'
    },
    formatDate(date) {
      return date ? new Date(date).toLocaleString() : ''
    }
  }
}
</script>

<style lang="scss" scoped>
.customers-page {
  .page-title {
    font-size: 24px;
    color: #303133;
    margin-bottom: 20px;
  }
}

.search-bar {
  background: #fff;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 20px;
  display: flex;
  gap: 15px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.table-container {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.danger-btn {
  color: #f56c6c !important;
}
</style>
