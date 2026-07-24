<template>
  <div class="clothes-types-page">
    <h2 class="page-title">衣服类型管理</h2>

    <div class="search-bar">
      <el-input v-model="searchParams.name" placeholder="搜索类型名称" clearable style="width: 200px" />
      <el-button type="primary" @click="loadData">搜索</el-button>
      <el-button @click="resetSearch">重置</el-button>
      <el-button type="success" @click="openAddDialog" v-if="isAdmin">新增类型</el-button>
    </div>

    <div class="table-container">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="类型名称" min-width="130">
          <template slot-scope="scope">{{ scope.row.name || '-' }}</template>
        </el-table-column>
        <el-table-column prop="price" label="基础价格" width="120">
          <template slot-scope="scope">{{ scope.row.price != null ? '¥' + scope.row.price : '-' }}</template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="180" show-overflow-tooltip>
          <template slot-scope="scope">{{ scope.row.description || '-' }}</template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80">
          <template slot-scope="scope">{{ scope.row.sort != null ? scope.row.sort : '-' }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
              {{ scope.row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="150" v-if="isAdmin">
          <template slot-scope="scope">
            <el-button type="text" @click="openEditDialog(scope.row)">编辑</el-button>
            <el-button type="text" class="danger-btn" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

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

    <el-dialog :title="isEditing ? '编辑类型' : '新增类型'" :visible.sync="dialogVisible" width="500px">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="类型名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入类型名称" />
        </el-form-item>
        <el-form-item label="基础价格" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
        <el-form-item label="状态" v-if="isEditing">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
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
import { mapGetters } from 'vuex'
import { clothesTypeApi } from '@/api'

export default {
  name: 'ClothesTypes',
  data() {
    return {
      tableData: [],
      loading: false,
      searchParams: { name: '' },
      pageNum: 1,
      pageSize: 10,
      total: 0,
      dialogVisible: false,
      isEditing: false,
      form: {},
      rules: {
        name: [{ required: true, message: '请输入类型名称', trigger: 'blur' }],
        price: [{ required: true, message: '请输入基础价格', trigger: 'blur' }]
      },
      submitting: false
    }
  },
  computed: {
    ...mapGetters(['isAdmin'])
  },
  created() {
    this.loadData()
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const res = await clothesTypeApi.getPage({
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          name: this.searchParams.name
        })
        this.tableData = res.data.records
        this.total = res.data.total
      } catch (e) {} finally {
        this.loading = false
      }
    },
    resetSearch() {
      this.searchParams = { name: '' }
      this.pageNum = 1
      this.loadData()
    },
    openAddDialog() {
      this.isEditing = false
      this.form = { price: 0, sort: 0, status: 1 }
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
              await clothesTypeApi.update(this.form.id, this.form)
              this.$message.success('更新成功')
            } else {
              await clothesTypeApi.create(this.form)
              this.$message.success('创建成功')
            }
            this.dialogVisible = false
            this.loadData()
          } catch (e) {} finally {
            this.submitting = false
          }
        }
      })
    },
    handleDelete(row) {
      this.$confirm('确定要删除该类型吗？', '提示', { type: 'warning' })
        .then(async () => {
          try {
            await clothesTypeApi.delete(row.id)
            this.$message.success('删除成功')
            this.loadData()
          } catch (e) {}
        }).catch(() => {})
    }
  }
}
</script>

<style lang="scss" scoped>
.clothes-types-page {
  .page-title { font-size: 24px; color: #303133; margin-bottom: 20px; }
}
.search-bar {
  background: #fff; padding: 20px; border-radius: 12px; margin-bottom: 20px;
  display: flex; gap: 15px; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}
.table-container {
  background: #fff; border-radius: 12px; padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}
.pagination-container { margin-top: 20px; display: flex; justify-content: flex-end; }
.danger-btn { color: #f56c6c !important; }
</style>
