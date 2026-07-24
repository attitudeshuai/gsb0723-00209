<template>
  <div class="laundry-info-page">
    <h2 class="page-title">洗衣信息管理</h2>

    <div class="search-bar">
      <el-input v-model="searchParams.name" placeholder="搜索服务名称" clearable style="width: 200px" />
      <el-button type="primary" @click="loadData">搜索</el-button>
      <el-button @click="resetSearch">重置</el-button>
      <el-button type="success" @click="openAddDialog" v-if="isShopOwner">新增服务</el-button>
    </div>

    <div class="table-container">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="服务名称" min-width="150">
          <template slot-scope="scope">{{ scope.row.name || '-' }}</template>
        </el-table-column>
        <el-table-column prop="shopName" label="所属店铺" min-width="130">
          <template slot-scope="scope">{{ scope.row.shopName || '-' }}</template>
        </el-table-column>
        <el-table-column prop="clothesTypeName" label="衣服类型" min-width="100">
          <template slot-scope="scope">{{ scope.row.clothesTypeName || '-' }}</template>
        </el-table-column>
        <el-table-column prop="price" label="价格" width="100">
          <template slot-scope="scope">{{ scope.row.price != null ? '¥' + scope.row.price : '-' }}</template>
        </el-table-column>
        <el-table-column prop="washMethod" label="洗涤方式" min-width="90">
          <template slot-scope="scope">{{ scope.row.washMethod || '-' }}</template>
        </el-table-column>
        <el-table-column prop="deliveryTime" label="交付时间" min-width="90">
          <template slot-scope="scope">{{ scope.row.deliveryTime || '-' }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 1 ? 'success' : 'info'" size="small">
              {{ scope.row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="150">
          <template slot-scope="scope">
            <el-button type="text" @click="openEditDialog(scope.row)" v-if="canEdit(scope.row)">编辑</el-button>
            <el-button type="text" class="danger-btn" @click="handleDelete(scope.row)" v-if="canEdit(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination background layout="total, prev, pager, next" :total="total" :page-size="pageSize" :current-page.sync="pageNum" @current-change="loadData" />
      </div>
    </div>

    <el-dialog :title="isEditing ? '编辑服务' : '新增服务'" :visible.sync="dialogVisible" width="600px">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="服务名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入服务名称" />
        </el-form-item>
        <el-form-item label="所属店铺" prop="shopId" v-if="!isEditing">
          <el-select v-model="form.shopId" placeholder="请选择店铺" style="width: 100%" disabled>
            <el-option v-if="myShop" :label="myShop.name" :value="myShop.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="衣服类型" prop="clothesTypeId">
          <el-select v-model="form.clothesTypeId" placeholder="请选择类型" style="width: 100%">
            <el-option v-for="item in clothesTypes" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="原价">
          <el-input-number v-model="form.originalPrice" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="洗涤方式">
          <el-input v-model="form.washMethod" placeholder="如: 机洗、干洗" />
        </el-form-item>
        <el-form-item label="交付时间">
          <el-input v-model="form.deliveryTime" placeholder="如: 1-2天" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" />
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
import { laundryInfoApi, shopApi, clothesTypeApi } from '@/api'

export default {
  name: 'LaundryInfo',
  data() {
    return {
      tableData: [], loading: false, searchParams: { name: '' },
      pageNum: 1, pageSize: 10, total: 0,
      dialogVisible: false, isEditing: false, form: {},
      rules: {
        name: [{ required: true, message: '请输入服务名称', trigger: 'blur' }],
        clothesTypeId: [{ required: true, message: '请选择衣服类型', trigger: 'change' }],
        price: [{ required: true, message: '请输入价格', trigger: 'blur' }]
      },
      submitting: false, clothesTypes: [], myShop: null
    }
  },
  computed: {
    ...mapGetters(['isAdmin', 'isShopOwner', 'userInfo'])
  },
  created() {
    this.loadData()
    this.loadClothesTypes()
    if (this.isShopOwner) this.loadMyShop()
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const params = { pageNum: this.pageNum, pageSize: this.pageSize, name: this.searchParams.name }
        if (this.isShopOwner && this.myShop) params.shopId = this.myShop.id
        const res = await laundryInfoApi.getPage(params)
        this.tableData = res.data.records
        this.total = res.data.total
      } catch (e) {} finally { this.loading = false }
    },
    async loadClothesTypes() {
      try {
        const res = await clothesTypeApi.getList()
        this.clothesTypes = res.data
      } catch (e) {}
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
      return this.isAdmin || (this.isShopOwner && this.myShop && row.shopId === this.myShop.id)
    },
    openAddDialog() {
      this.isEditing = false
      this.form = { shopId: this.myShop?.id, price: 0, status: 1 }
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
              await laundryInfoApi.update(this.form.id, this.form)
              this.$message.success('更新成功')
            } else {
              await laundryInfoApi.create(this.form)
              this.$message.success('创建成功')
            }
            this.dialogVisible = false
            this.loadData()
          } catch (e) {} finally { this.submitting = false }
        }
      })
    },
    handleDelete(row) {
      this.$confirm('确定要删除该服务吗？', '提示', { type: 'warning' })
        .then(async () => {
          try {
            await laundryInfoApi.delete(row.id)
            this.$message.success('删除成功')
            this.loadData()
          } catch (e) {}
        }).catch(() => {})
    }
  }
}
</script>

<style lang="scss" scoped>
.laundry-info-page { .page-title { font-size: 24px; color: #303133; margin-bottom: 20px; } }
.search-bar { background: #fff; padding: 20px; border-radius: 12px; margin-bottom: 20px; display: flex; gap: 15px; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06); }
.table-container { background: #fff; border-radius: 12px; padding: 20px; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06); }
.pagination-container { margin-top: 20px; display: flex; justify-content: flex-end; }
.danger-btn { color: #f56c6c !important; }
</style>
