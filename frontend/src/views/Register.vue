<template>
  <div class="register-container">
    <div class="register-bg"></div>
    <div class="register-card">
      <div class="register-header">
        <h1>用户注册</h1>
        <p>注册账号，享受便捷洗衣服务</p>
      </div>
      <el-form ref="form" :model="form" :rules="rules" class="register-form">
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            prefix-icon="el-icon-user"
            placeholder="请输入用户名"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            prefix-icon="el-icon-lock"
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            prefix-icon="el-icon-lock"
            placeholder="请确认密码"
            show-password
          />
        </el-form-item>
        <el-form-item prop="nickname">
          <el-input
            v-model="form.nickname"
            prefix-icon="el-icon-postcard"
            placeholder="请输入昵称"
          />
        </el-form-item>
        <el-form-item prop="phone">
          <el-input
            v-model="form.phone"
            prefix-icon="el-icon-phone"
            placeholder="请输入手机号"
          />
        </el-form-item>
        <el-form-item prop="role">
          <el-select v-model="form.role" placeholder="请选择用户类型" style="width: 100%">
            <el-option label="顾客" value="customer" />
            <el-option label="店家" value="shop_owner" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            class="register-btn"
            @click="handleRegister"
          >
            注 册
          </el-button>
        </el-form-item>
        <div class="register-footer">
          <span>已有账号？</span>
          <router-link to="/login">立即登录</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
import { authApi } from '@/api'

export default {
  name: 'Register',
  data() {
    const validatePassword = (rule, value, callback) => {
      if (value !== this.form.password) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }
    return {
      form: {
        username: '',
        password: '',
        confirmPassword: '',
        nickname: '',
        phone: '',
        role: 'customer'
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '用户名长度在3到20个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, message: '密码长度不少于6个字符', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认密码', trigger: 'blur' },
          { validator: validatePassword, trigger: 'blur' }
        ],
        nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
        role: [{ required: true, message: '请选择用户类型', trigger: 'change' }]
      },
      loading: false
    }
  },
  methods: {
    handleRegister() {
      this.$refs.form.validate(async valid => {
        if (valid) {
          this.loading = true
          try {
            await authApi.register(this.form)
            this.$message.success('注册成功，请登录')
            this.$router.push('/login')
          } catch (e) {
            // 错误已在拦截器处理
          } finally {
            this.loading = false
          }
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden;
}

.register-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  
  &::before {
    content: '';
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 60%);
    animation: rotate 30s linear infinite;
  }
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.register-card {
  position: relative;
  width: 420px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.register-header {
  text-align: center;
  margin-bottom: 30px;
  
  h1 {
    font-size: 28px;
    font-weight: 700;
    background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    margin-bottom: 10px;
  }
  
  p {
    color: #909399;
    font-size: 14px;
  }
}

.register-form {
  .el-form-item {
    margin-bottom: 20px;
  }
  
  ::v-deep .el-input__inner {
    height: 44px;
    border-radius: 10px;
    border: 2px solid #e4e7ed;
    
    &:focus {
      border-color: #11998e;
    }
  }
}

.register-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  border-radius: 10px;
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  border: none;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 20px rgba(17, 153, 142, 0.4);
  }
}

.register-footer {
  text-align: center;
  color: #909399;
  
  a {
    color: #11998e;
    text-decoration: none;
    font-weight: 500;
  }
}

@media (max-width: 480px) {
  .register-card {
    width: 90%;
    padding: 30px 20px;
  }
}
</style>
