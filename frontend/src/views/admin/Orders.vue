<template>
  <div class="orders-page">
    <h2 class="page-title">订单信息管理</h2>

    <div class="search-bar">
      <el-input v-model="searchParams.orderNo" placeholder="搜索订单编号" clearable style="width: 180px" />
      <el-select v-model="searchParams.status" placeholder="订单状态" clearable style="width: 120px">
        <el-option label="待支付" :value="0" />
        <el-option label="已支付" :value="1" />
        <el-option label="洗涤中" :value="2" />
        <el-option label="已完成" :value="3" />
        <el-option label="已取消" :value="4" />
      </el-select>
      <el-button type="primary" @click="loadData">搜索</el-button>
      <el-button @click="resetSearch">重置</el-button>
    </div>

    <div class="table-container">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="orderNo" label="订单编号" min-width="150">
          <template slot-scope="scope">{{ scope.row.orderNo || '-' }}</template>
        </el-table-column>
        <el-table-column prop="customerName" label="顾客" min-width="90">
          <template slot-scope="scope">{{ scope.row.customerName || '-' }}</template>
        </el-table-column>
        <el-table-column prop="shopName" label="店铺" min-width="110">
          <template slot-scope="scope">{{ scope.row.shopName || '-' }}</template>
        </el-table-column>
        <el-table-column prop="laundryName" label="服务" min-width="130" show-overflow-tooltip>
          <template slot-scope="scope">{{ scope.row.laundryName || '-' }}</template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="70">
          <template slot-scope="scope">{{ scope.row.quantity || '-' }}</template>
        </el-table-column>
        <el-table-column prop="totalPrice" label="金额" width="90">
          <template slot-scope="scope">¥{{ scope.row.totalPrice }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)" size="small">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="160">
          <template slot-scope="scope">{{ formatDate(scope.row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="200">
          <template slot-scope="scope">
            <el-button type="text" @click="viewDetail(scope.row)">详情</el-button>
            <el-button type="text" @click="viewProgress(scope.row)">进度</el-button>
            <el-button type="text" @click="handlePay(scope.row)" v-if="scope.row.status === 0 && isCustomer">支付</el-button>
            <el-button type="text" @click="handleStatus(scope.row)" v-if="scope.row.status < 3 && (isAdmin || isShopOwner)">处理</el-button>
            <el-button type="text" class="danger-btn" @click="handleCancel(scope.row)" v-if="scope.row.status < 2">取消</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination background layout="total, prev, pager, next" :total="total" :page-size="pageSize" :current-page.sync="pageNum" @current-change="loadData" />
      </div>
    </div>

    <!-- 详情对话框 -->
    <el-dialog title="订单详情" :visible.sync="detailDialogVisible" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="订单编号">{{ currentOrder.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentOrder.status)">{{ getStatusText(currentOrder.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="顾客">{{ currentOrder.customerName }}</el-descriptions-item>
        <el-descriptions-item label="店铺">{{ currentOrder.shopName }}</el-descriptions-item>
        <el-descriptions-item label="服务">{{ currentOrder.laundryName }}</el-descriptions-item>
        <el-descriptions-item label="数量">{{ currentOrder.quantity }}</el-descriptions-item>
        <el-descriptions-item label="总价">¥{{ currentOrder.totalPrice }}</el-descriptions-item>
        <el-descriptions-item label="下单时间">{{ formatDate(currentOrder.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="取件地址" :span="2">{{ currentOrder.pickupAddress }}</el-descriptions-item>
        <el-descriptions-item label="送达地址" :span="2">{{ currentOrder.deliveryAddress }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentOrder.remark || '无' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 进度对话框 -->
    <el-dialog title="订单进度" :visible.sync="progressDialogVisible" width="500px">
      <el-timeline>
        <el-timeline-item v-for="item in progressList" :key="item.id" :timestamp="formatDate(item.createTime)" placement="top">
          <el-card>
            <h4>{{ item.description }}</h4>
            <p v-if="item.operatorName">操作人：{{ item.operatorName }}</p>
          </el-card>
        </el-timeline-item>
      </el-timeline>
      <el-empty v-if="progressList.length === 0" description="暂无进度信息" />
    </el-dialog>

    <!-- 处理订单对话框 -->
    <el-dialog title="处理订单" :visible.sync="statusDialogVisible" width="400px">
      <el-form label-width="80px">
        <el-form-item label="订单状态">
          <el-select v-model="statusForm.status" style="width: 100%">
            <el-option label="已取件" :value="1" v-if="currentOrder.status < 1" />
            <el-option label="洗涤中" :value="2" />
            <el-option label="已完成" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="说明">
          <el-input v-model="statusForm.description" type="textarea" placeholder="请输入处理说明" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="statusDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitStatus" :loading="submitting">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { orderApi } from '@/api'

export default {
  name: 'Orders',
  data() {
    return {
      tableData: [], loading: false,
      searchParams: { orderNo: '', status: null },
      pageNum: 1, pageSize: 10, total: 0,
      detailDialogVisible: false, currentOrder: {},
      progressDialogVisible: false, progressList: [],
      statusDialogVisible: false, statusForm: { status: 2, description: '' },
      submitting: false
    }
  },
  computed: {
    ...mapGetters(['isAdmin', 'isShopOwner', 'isCustomer', 'userInfo'])
  },
  created() {
    this.loadData()
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const params = {
          pageNum: this.pageNum, pageSize: this.pageSize,
          orderNo: this.searchParams.orderNo, status: this.searchParams.status
        }
        const res = this.isAdmin ? await orderApi.getPage(params) : await orderApi.getMyOrders(params)
        this.tableData = res.data.records
        this.total = res.data.total
      } catch (e) {} finally { this.loading = false }
    },
    resetSearch() {
      this.searchParams = { orderNo: '', status: null }
      this.pageNum = 1
      this.loadData()
    },
    getStatusType(status) {
      return ['warning', 'primary', 'info', 'success', 'danger'][status] || 'info'
    },
    getStatusText(status) {
      return ['待支付', '已支付', '洗涤中', '已完成', '已取消'][status] || '未知'
    },
    formatDate(date) {
      return date ? new Date(date).toLocaleString() : ''
    },
    viewDetail(row) {
      this.currentOrder = row
      this.detailDialogVisible = true
    },
    async viewProgress(row) {
      this.currentOrder = row
      try {
        const res = await orderApi.getProgress(row.id)
        this.progressList = res.data
        this.progressDialogVisible = true
      } catch (e) {}
    },
    async handlePay(row) {
      this.$confirm('确定要支付该订单吗？', '提示', { type: 'warning' })
        .then(async () => {
          try {
            await orderApi.pay(row.id)
            this.$message.success('支付成功')
            this.loadData()
          } catch (e) {}
        }).catch(() => {})
    },
    handleStatus(row) {
      this.currentOrder = row
      this.statusForm = { status: row.status + 1, description: '' }
      this.statusDialogVisible = true
    },
    async submitStatus() {
      this.submitting = true
      try {
        await orderApi.updateStatus(this.currentOrder.id, this.statusForm.status, this.statusForm.description)
        this.$message.success('处理成功')
        this.statusDialogVisible = false
        this.loadData()
      } catch (e) {} finally { this.submitting = false }
    },
    handleCancel(row) {
      this.$confirm('确定要取消该订单吗？', '提示', { type: 'warning' })
        .then(async () => {
          try {
            await orderApi.cancel(row.id)
            this.$message.success('订单已取消')
            this.loadData()
          } catch (e) {}
        }).catch(() => {})
    }
  }
}
</script>

<style lang="scss" scoped>
.orders-page { .page-title { font-size: 24px; color: #303133; margin-bottom: 20px; } }
.search-bar { background: #fff; padding: 20px; border-radius: 12px; margin-bottom: 20px; display: flex; gap: 15px; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06); }
.table-container { background: #fff; border-radius: 12px; padding: 20px; box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06); }
.pagination-container { margin-top: 20px; display: flex; justify-content: flex-end; }
.danger-btn { color: #f56c6c !important; }
</style>
