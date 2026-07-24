<template>
  <div class="announcements-page">
    <h2 class="page-title">系统公告管理</h2>

    <div class="search-bar">
      <el-input v-model="searchParams.title" placeholder="搜索公告标题" clearable style="width: 200px" />
      <el-button type="primary" @click="loadData">搜索</el-button>
      <el-button @click="resetSearch">重置</el-button>
      <el-button type="success" @click="openAddDialog">新增公告</el-button>
    </div>

    <div class="table-container">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip>
          <template slot-scope="scope">{{ scope.row.title || '-' }}</template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.type === 1 ? 'danger' : 'info'" size="small">
              {{ scope.row.type === 1 ? '紧急' : '普通' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template slot-scope="scope">
            <el-switch v-model="scope.row.status" :active-value="1" :inactive-value="0" @change="handleStatusChange(scope.row)" />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="发布时间" width="160">
          <template slot-scope="scope">{{ formatDate(scope.row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="150">
          <template slot-scope="scope">
            <el-button type="text" @click="openEditDialog(scope.row)">编辑</el-button>
            <el-button type="text" class="danger-btn" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination background layout="total, prev, pager, next" :total="total" :page-size="pageSize" :current-page.sync="pageNum" @current-change="loadData" />
      </div>
    </div>

    <el-dialog :title="isEditing ? '编辑公告' : '新增公告'" :visible.sync="dialogVisible" width="600px">
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio :label="0">普通公告</el-radio>
            <el-radio :label="1">紧急公告</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="6" placeholder="请输入公告内容" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { announcementApi } from '@/api'

export default {
  name: 'AdminAnnouncements',
  data() {
    return {
      tableData: [], loading: false, searchParams: { title: '' },
      pageNum: 1, pageSize: 10, total: 0,
      dialogVisible: false, isEditing: false, form: {},
      rules: {
        title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
        content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
      },
      submitting: false
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const res = await announcementApi.getPage({
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
    openAddDialog() {
      this.isEditing = false
      this.form = { type: 0, status: 1 }
      this.dialogVisible = true
    },
    openEditDialog(row) {
      this.isEditing = true
      this.form = { ...row }
      this.dialogVisible = true
    },
    submitForm() {
      this.$refs.form.validate(async valid => {
        if (valid) {
          this.submitting = true
          try {
            if (this.isEditing) {
              await announcementApi.update(this.form.id, this.form)
              this.$message.success('更新成功')
            } else {
              await announcementApi.create(this.form)
              this.$message.success('发布成功')
            }
            this.dialogVisible = false
            this.loadData()
          } catch (e) {} finally { this.submitting = false }
        }
      })
    },
    async handleStatusChange(row) {
      try {
        await announcementApi.updateStatus(row.id, row.status)
        this.$message.success('状态更新成功')
      } catch (e) { row.status = row.status === 1 ? 0 : 1 }
    },
    handleDelete(row) {
      this.$confirm('确定要删除该公告吗？', '提示', { type: 'warning' })
        .then(async () => {
          try {
            await announcementApi.delete(row.id)
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
.announcements-page { .page-title { font-size: 24px; color: #303133; margin-bottom: 20px; } }
.search-bar { background: #fff; padding: 20px; border-radius: 12px; margin-bottom: 20px; display: flex; gap: 15px; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06); }
.table-container { background: #fff; border-radius: 12px; padding: 20px; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06); }
.pagination-container { margin-top: 20px; display: flex; justify-content: flex-end; }
.danger-btn { color: #f56c6c !important; }
</style>
