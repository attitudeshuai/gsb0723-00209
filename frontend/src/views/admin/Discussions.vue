<template>
  <div class="discussions-page">
    <h2 class="page-title">交流区管理</h2>

    <div class="search-bar">
      <el-input v-model="searchParams.title" placeholder="搜索帖子标题" clearable style="width: 200px" />
      <el-button type="primary" @click="loadData">搜索</el-button>
      <el-button @click="resetSearch">重置</el-button>
    </div>

    <div class="table-container">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="180" show-overflow-tooltip>
          <template slot-scope="scope">{{ scope.row.title || '-' }}</template>
        </el-table-column>
        <el-table-column prop="userName" label="发布者" min-width="100">
          <template slot-scope="scope">{{ scope.row.userName || '-' }}</template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览" width="80">
          <template slot-scope="scope">{{ scope.row.viewCount != null ? scope.row.viewCount : '-' }}</template>
        </el-table-column>
        <el-table-column prop="likeCount" label="点赞" width="80">
          <template slot-scope="scope">{{ scope.row.likeCount != null ? scope.row.likeCount : '-' }}</template>
        </el-table-column>
        <el-table-column prop="commentCount" label="评论" width="80">
          <template slot-scope="scope">{{ scope.row.commentCount != null ? scope.row.commentCount : '-' }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'" size="small">
              {{ scope.row.status === 1 ? '显示' : '隐藏' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="160">
          <template slot-scope="scope">{{ formatDate(scope.row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="150">
          <template slot-scope="scope">
            <el-button type="text" @click="viewDetail(scope.row)">查看</el-button>
            <el-button type="text" class="danger-btn" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination background layout="total, prev, pager, next" :total="total" :page-size="pageSize" :current-page.sync="pageNum" @current-change="loadData" />
      </div>
    </div>

    <el-dialog title="帖子详情" :visible.sync="detailDialogVisible" width="600px">
      <h3>{{ currentItem.title }}</h3>
      <p style="color: #909399; margin: 10px 0;">
        {{ currentItem.userName }} · {{ formatDate(currentItem.createTime) }}
      </p>
      <div style="line-height: 1.8; white-space: pre-wrap;">{{ currentItem.content }}</div>
    </el-dialog>
  </div>
</template>

<script>
import { discussionApi } from '@/api'

export default {
  name: 'AdminDiscussions',
  data() {
    return {
      tableData: [], loading: false, searchParams: { title: '' },
      pageNum: 1, pageSize: 10, total: 0,
      detailDialogVisible: false, currentItem: {}
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const res = await discussionApi.getPage({
          pageNum: this.pageNum, pageSize: this.pageSize, title: this.searchParams.title
        })
        this.tableData = res.data.records
        this.total = res.data.total
      } catch (e) {} finally { this.loading = false }
    },
    resetSearch() {
      this.searchParams = { title: '' }
      this.pageNum = 1
      this.loadData()
    },
    viewDetail(row) {
      this.currentItem = row
      this.detailDialogVisible = true
    },
    handleDelete(row) {
      this.$confirm('确定要删除该帖子吗？', '提示', { type: 'warning' })
        .then(async () => {
          try {
            await discussionApi.delete(row.id)
            this.$message.success('删除成功')
            this.loadData()
          } catch (e) {}
        }).catch(() => {})
    },
    formatDate(date) {
      return date ? new Date(date).toLocaleString() : ''
    }
  }
}
</script>

<style lang="scss" scoped>
.discussions-page { .page-title { font-size: 24px; color: #303133; margin-bottom: 20px; } }
.search-bar { background: #fff; padding: 20px; border-radius: 12px; margin-bottom: 20px; display: flex; gap: 15px; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06); }
.table-container { background: #fff; border-radius: 12px; padding: 20px; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06); }
.pagination-container { margin-top: 20px; display: flex; justify-content: flex-end; }
.danger-btn { color: #f56c6c !important; }
</style>
