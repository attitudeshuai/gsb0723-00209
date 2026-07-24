<template>
  <div class="shop-detail-page">
    <!-- 导航栏 -->
    <nav class="navbar">
      <div class="nav-container">
        <div class="logo" @click="$router.push('/home')">
          <i class="el-icon-s-cooperation"></i>
          <span>洗衣店管理系统</span>
        </div>
        <div class="nav-links">
          <router-link to="/home">首页</router-link>
          <router-link to="/shops">店铺信息</router-link>
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

    <div class="page-content" v-loading="loading">
      <div class="content-container">
        <!-- 店铺信息 -->
        <div class="shop-info-card" v-if="shop">
          <div class="shop-banner">
            <div class="shop-logo">
              <i class="el-icon-s-shop"></i>
            </div>
            <div class="shop-info">
              <h1>{{ shop.name }}</h1>
              <el-tag v-if="shop.status === 1" type="success">营业中</el-tag>
              <el-tag v-else type="info">已关闭</el-tag>
              <p class="description">{{ shop.description || '暂无描述' }}</p>
              <div class="meta-grid">
                <div class="meta-item">
                  <i class="el-icon-star-on"></i>
                  <span>评分：{{ shop.rating || 5.0 }}</span>
                </div>
                <div class="meta-item">
                  <i class="el-icon-phone"></i>
                  <span>电话：{{ shop.phone || '暂无' }}</span>
                </div>
                <div class="meta-item">
                  <i class="el-icon-time"></i>
                  <span>营业时间：{{ shop.businessHours || '暂无' }}</span>
                </div>
                <div class="meta-item">
                  <i class="el-icon-location"></i>
                  <span>地址：{{ shop.address || '暂无' }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 洗衣服务 -->
        <div class="services-section">
          <h2 class="section-title">洗衣服务</h2>
          <div class="services-grid" v-loading="loadingServices">
            <div
              v-for="item in laundryList"
              :key="item.id"
              class="service-card"
            >
              <div class="service-icon">
                <i class="el-icon-s-goods"></i>
              </div>
              <div class="service-info">
                <h3>{{ item.name }}</h3>
                <p class="type">{{ item.clothesTypeName }}</p>
                <p class="desc">{{ item.description || '暂无描述' }}</p>
                <div class="service-meta">
                  <span class="method">{{ item.washMethod }}</span>
                  <span class="time">{{ item.deliveryTime }}</span>
                </div>
                <div class="price-row">
                  <span class="price">¥{{ item.price }}</span>
                  <span class="original-price" v-if="item.originalPrice">¥{{ item.originalPrice }}</span>
                </div>
              </div>
              <div class="service-action">
                <el-button type="primary" size="small" @click="handleOrder(item)">立即下单</el-button>
              </div>
            </div>
          </div>
          <el-empty v-if="!loadingServices && laundryList.length === 0" description="暂无洗衣服务" />
        </div>
      </div>
    </div>

    <!-- 下单对话框 -->
    <el-dialog title="下单" :visible.sync="orderDialogVisible" width="500px">
      <el-form ref="orderForm" :model="orderForm" :rules="orderRules" label-width="100px">
        <el-form-item label="服务名称">
          <el-input :value="selectedService.name" disabled />
        </el-form-item>
        <el-form-item label="单价">
          <el-input :value="'¥' + selectedService.price" disabled />
        </el-form-item>
        <el-form-item label="数量" prop="quantity">
          <el-input-number v-model="orderForm.quantity" :min="1" :max="99" />
        </el-form-item>
        <el-form-item label="总价">
          <span class="total-price">¥{{ (selectedService.price * orderForm.quantity).toFixed(2) }}</span>
        </el-form-item>
        <el-form-item label="取件地址" prop="pickupAddress">
          <el-input v-model="orderForm.pickupAddress" placeholder="请输入取件地址" />
        </el-form-item>
        <el-form-item label="送达地址" prop="deliveryAddress">
          <el-input v-model="orderForm.deliveryAddress" placeholder="请输入送达地址" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="orderForm.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="orderDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitOrder">确认下单</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { frontApi, orderApi } from '@/api'

export default {
  name: 'ShopDetail',
  data() {
    return {
      shop: null,
      laundryList: [],
      loading: false,
      loadingServices: false,
      orderDialogVisible: false,
      selectedService: {},
      orderForm: {
        quantity: 1,
        pickupAddress: '',
        deliveryAddress: '',
        remark: ''
      },
      orderRules: {
        quantity: [{ required: true, message: '请输入数量', trigger: 'blur' }],
        pickupAddress: [{ required: true, message: '请输入取件地址', trigger: 'blur' }],
        deliveryAddress: [{ required: true, message: '请输入送达地址', trigger: 'blur' }]
      },
      submitting: false
    }
  },
  computed: {
    ...mapGetters(['isLoggedIn', 'userInfo'])
  },
  created() {
    this.loadShop()
    this.loadServices()
  },
  methods: {
    async loadShop() {
      const id = this.$route.params.id
      this.loading = true
      try {
        const res = await frontApi.getShopDetail(id)
        this.shop = res.data
      } catch (e) {
        // 错误已处理
      } finally {
        this.loading = false
      }
    },
    async loadServices() {
      const shopId = this.$route.params.id
      this.loadingServices = true
      try {
        const res = await frontApi.getShopLaundry(shopId)
        this.laundryList = res.data
      } catch (e) {
        // 错误已处理
      } finally {
        this.loadingServices = false
      }
    },
    handleOrder(service) {
      if (!this.isLoggedIn) {
        this.$message.warning('请先登录')
        this.$router.push('/login')
        return
      }
      this.selectedService = service
      this.orderForm = {
        quantity: 1,
        pickupAddress: this.userInfo.address || '',
        deliveryAddress: this.userInfo.address || '',
        remark: ''
      }
      this.orderDialogVisible = true
    },
    submitOrder() {
      this.$refs.orderForm.validate(async valid => {
        if (valid) {
          this.submitting = true
          try {
            await orderApi.create({
              laundryInfoId: this.selectedService.id,
              ...this.orderForm
            })
            this.$message.success('下单成功')
            this.orderDialogVisible = false
            this.$router.push('/admin/orders')
          } catch (e) {
            // 错误已处理
          } finally {
            this.submitting = false
          }
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.shop-detail-page {
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

.shop-info-card {
  background: #fff;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  margin-bottom: 40px;

  .shop-banner {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    padding: 40px;
    display: flex;
    gap: 30px;

    @media (max-width: 768px) {
      flex-direction: column;
      align-items: center;
      text-align: center;
    }
  }

  .shop-logo {
    width: 120px;
    height: 120px;
    background: rgba(255, 255, 255, 0.2);
    border-radius: 24px;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;

    i {
      font-size: 60px;
      color: #fff;
    }
  }

  .shop-info {
    color: #fff;

    h1 {
      font-size: 28px;
      margin-bottom: 10px;
      display: inline-block;
      margin-right: 15px;
    }

    .description {
      opacity: 0.9;
      margin: 15px 0;
    }

    .meta-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 12px;
      margin-top: 20px;

      @media (max-width: 768px) {
        grid-template-columns: 1fr;
      }

      .meta-item {
        display: flex;
        align-items: center;
        gap: 8px;
        opacity: 0.9;

        i {
          font-size: 18px;
        }
      }
    }
  }
}

.services-section {
  .section-title {
    font-size: 24px;
    color: #303133;
    margin-bottom: 30px;
    padding-left: 15px;
    border-left: 4px solid #667eea;
  }
}

.services-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;

  @media (max-width: 992px) {
    grid-template-columns: repeat(2, 1fr);
  }

  @media (max-width: 576px) {
    grid-template-columns: 1fr;
  }
}

.service-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: all 0.3s;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
  }

  .service-icon {
    width: 60px;
    height: 60px;
    background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
    border-radius: 16px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 15px;

    i {
      font-size: 28px;
      color: #fff;
    }
  }

  .service-info {
    h3 {
      font-size: 18px;
      color: #303133;
      margin-bottom: 8px;
    }

    .type {
      color: #667eea;
      font-size: 13px;
      margin-bottom: 8px;
    }

    .desc {
      color: #909399;
      font-size: 14px;
      margin-bottom: 12px;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    .service-meta {
      display: flex;
      gap: 15px;
      margin-bottom: 12px;

      span {
        font-size: 12px;
        color: #606266;
        background: #f5f7fa;
        padding: 4px 8px;
        border-radius: 4px;
      }
    }

    .price-row {
      .price {
        font-size: 24px;
        font-weight: 700;
        color: #f56c6c;
      }

      .original-price {
        font-size: 14px;
        color: #909399;
        text-decoration: line-through;
        margin-left: 10px;
      }
    }
  }

  .service-action {
    margin-top: 15px;
    text-align: right;
  }
}

.total-price {
  font-size: 24px;
  font-weight: 700;
  color: #f56c6c;
}
</style>
