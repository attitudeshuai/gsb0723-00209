<template>
  <div class="shops-page">
    <h2 class="page-title">店铺信息管理</h2>

    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input v-model="searchParams.name" placeholder="搜索店铺名称" clearable style="width: 200px" />
      <el-button type="primary" @click="loadData">搜索</el-button>
      <el-button @click="resetSearch">重置</el-button>
      <el-button type="success" @click="openAddDialog" v-if="isShopOwner && !myShop">新增店铺</el-button>
    </div>

    <!-- 数据表格 -->
    <div class="table-container">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="店铺名称" min-width="130">
          <template slot-scope="scope">{{ scope.row.name || '-' }}</template>
        </el-table-column>
        <el-table-column prop="ownerName" label="店家" min-width="100">
          <template slot-scope="scope">{{ scope.row.ownerName || '-' }}</template>
        </el-table-column>
        <el-table-column prop="phone" label="联系电话" min-width="120">
          <template slot-scope="scope">{{ scope.row.phone || '-' }}</template>
        </el-table-column>
        <el-table-column prop="address" label="地址" min-width="180" show-overflow-tooltip>
          <template slot-scope="scope">{{ scope.row.address || '-' }}</template>
        </el-table-column>
        <el-table-column prop="businessHours" label="营业时间" min-width="110">
          <template slot-scope="scope">{{ scope.row.businessHours || '-' }}</template>
        </el-table-column>
        <el-table-column prop="rating" label="评分" width="80">
          <template slot-scope="scope">{{ scope.row.rating || '-' }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
              {{ scope.row.status === 1 ? '营业中' : '已关闭' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="200">
          <template slot-scope="scope">
            <el-button type="text" @click="viewDetail(scope.row)">查看</el-button>
            <el-button type="text" @click="openEditDialog(scope.row)" v-if="canEdit(scope.row)">编辑</el-button>
            <el-button type="text" class="danger-btn" @click="handleDelete(scope.row)" v-if="isAdmin">删除</el-button>
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

    <!-- 新增/编辑对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="店铺名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入店铺名称" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="店铺地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入店铺地址" />
        </el-form-item>
        <el-form-item label="营业时间" prop="businessHours">
          <el-input v-model="form.businessHours" placeholder="如: 08:00-22:00" />
        </el-form-item>
        <el-form-item label="店铺描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入店铺描述" />
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

    <!-- 详情对话框 -->
    <el-dialog title="店铺详情" :visible.sync="detailDialogVisible" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="店铺名称">{{ currentShop.name }}</el-descriptions-item>
        <el-descriptions-item label="店家">{{ currentShop.ownerName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ currentShop.phone }}</el-descriptions-item>
        <el-descriptions-item label="评分">{{ currentShop.rating }}</el-descriptions-item>
        <el-descriptions-item label="营业时间">{{ currentShop.businessHours }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentShop.status === 1 ? 'success' : 'info'">
            {{ currentShop.status === 1 ? '营业中' : '已关闭' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="地址" :span="2">{{ currentShop.address }}</el-descriptions-item>
        <el-descriptions-item label="描述" :span="2">{{ currentShop.description }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { shopApi } from '@/api'

export default {
  name: 'Shops',
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
        name: [{ required: true, message: '请输入店铺名称', trigger: 'blur' }],
        phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }]
      },
      submitting: false,
      detailDialogVisible: false,
      currentShop: {},
      myShop: null
    }
  },
  computed: {
    ...mapGetters(['isAdmin', 'isShopOwner', 'userInfo']),
    dialogTitle() {
      return this.isEditing ? '编辑店铺' : '新增店铺'
    }
  },
  created() {
    this.loadData()
    if (this.isShopOwner) {
      this.loadMyShop()
    }
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const params = {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          name: this.searchParams.name
        }
        if (this.isShopOwner) {
          params.ownerId = this.userInfo.id
        }
        const res = await shopApi.getPage(params)
        this.tableData = res.data.records
        this.total = res.data.total
      } catch (e) {
        // 错误已处理
      } finally {
        this.loading = false
      }
    },
    async loadMyShop() {
      try {
        const res = await shopApi.getMyShop()
        this.myShop = res.data
      } catch (e) {}
    },
    resetSearch() {
      this.searchParams = { name: '' }
      this.pageNum = 1
      this.loadData()
    },
    canEdit(row) {
      return this.isAdmin || (this.isShopOwner && row.ownerId === this.userInfo.id)
    },
    openAddDialog() {
      this.isEditing = false
      this.form = { status: 1 }
      this.dialogVisible = true
    },
    openEditDialog(row) {
      this.isEditing = true
      this.form = { ...row }
      this.dialogVisible = true
    },
    viewDetail(row) {
      this.currentShop = row
      this.detailDialogVisible = true
    },
    submitForm() {
      this.$refs.form.validate(async valid => {
        if (valid) {
          this.submitting = true
          try {
            if (this.isEditing) {
              await shopApi.update(this.form.id, this.form)
              this.$message.success('更新成功')
            } else {
              await shopApi.create(this.form)
              this.$message.success('创建成功')
              this.loadMyShop()
            }
            this.dialogVisible = false
            this.loadData()
          } catch (e) {
            // 错误已处理
          } finally {
            this.submitting = false
          }
        }
      })
    },
    handleDelete(row) {
      this.$confirm('确定要删除该店铺吗？', '提示', { type: 'warning' })
        .then(async () => {
          try {
            await shopApi.delete(row.id)
            this.$message.success('删除成功')
            this.loadData()
          } catch (e) {}
        }).catch(() => {})
    }
  }
}
</script>

<style lang="scss" scoped>
.shops-page {
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
