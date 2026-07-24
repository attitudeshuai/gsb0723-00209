<template>
  <div class="profile-page">
    <h2 class="page-title">个人中心</h2>

    <div class="profile-container">
      <!-- 基本信息 -->
      <div class="profile-card">
        <div class="card-header">
          <span>基本信息</span>
          <el-button type="text" @click="editInfo">编辑</el-button>
        </div>
        <div class="profile-info">
          <div class="avatar-section">
            <el-avatar :size="100" icon="el-icon-user-solid"></el-avatar>
            <div class="user-name">{{ userInfo.nickname || userInfo.username }}</div>
            <el-tag :type="getRoleType(userInfo.role)">{{ getRoleText(userInfo.role) }}</el-tag>
          </div>
          <div class="info-grid">
            <div class="info-item">
              <span class="label">用户名</span>
              <span class="value">{{ userInfo.username }}</span>
            </div>
            <div class="info-item">
              <span class="label">昵称</span>
              <span class="value">{{ userInfo.nickname || '未设置' }}</span>
            </div>
            <div class="info-item">
              <span class="label">手机号</span>
              <span class="value">{{ userInfo.phone || '未设置' }}</span>
            </div>
            <div class="info-item">
              <span class="label">邮箱</span>
              <span class="value">{{ userInfo.email || '未设置' }}</span>
            </div>
            <div class="info-item">
              <span class="label">性别</span>
              <span class="value">{{ getGenderText(userInfo.gender) }}</span>
            </div>
            <div class="info-item">
              <span class="label">地址</span>
              <span class="value">{{ userInfo.address || '未设置' }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 修改密码 -->
      <div class="profile-card">
        <div class="card-header">
          <span>修改密码</span>
        </div>
        <el-form ref="passwordForm" :model="passwordForm" :rules="passwordRules" label-width="100px">
          <el-form-item label="原密码" prop="oldPassword">
            <el-input v-model="passwordForm.oldPassword" type="password" show-password />
          </el-form-item>
          <el-form-item label="新密码" prop="newPassword">
            <el-input v-model="passwordForm.newPassword" type="password" show-password />
          </el-form-item>
          <el-form-item label="确认密码" prop="confirmPassword">
            <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submitPassword" :loading="submitting">确认修改</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>

    <!-- 编辑对话框 -->
    <el-dialog title="编辑个人信息" :visible.sync="editDialogVisible" width="500px">
      <el-form ref="editForm" :model="editForm" label-width="80px">
        <el-form-item label="昵称">
          <el-input v-model="editForm.nickname" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="editForm.phone" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="editForm.email" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="editForm.gender" style="width: 100%">
            <el-option label="未知" :value="0" />
            <el-option label="男" :value="1" />
            <el-option label="女" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="editForm.address" type="textarea" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit" :loading="editing">保存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { userApi } from '@/api'

export default {
  name: 'Profile',
  data() {
    const validateConfirm = (rule, value, callback) => {
      if (value !== this.passwordForm.newPassword) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }
    return {
      passwordForm: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      passwordRules: {
        oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, message: '密码长度不少于6位', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认密码', trigger: 'blur' },
          { validator: validateConfirm, trigger: 'blur' }
        ]
      },
      submitting: false,
      editDialogVisible: false,
      editForm: {},
      editing: false
    }
  },
  computed: {
    ...mapGetters(['userInfo'])
  },
  methods: {
    getRoleType(role) {
      const types = { admin: 'danger', shop_owner: 'warning', customer: 'success' }
      return types[role] || 'info'
    },
    getRoleText(role) {
      const texts = { admin: '管理员', shop_owner: '店家', customer: '顾客' }
      return texts[role] || '未知'
    },
    getGenderText(gender) {
      const texts = { 0: '未知', 1: '男', 2: '女' }
      return texts[gender] || '未知'
    },
    editInfo() {
      this.editForm = { ...this.userInfo }
      this.editDialogVisible = true
    },
    async submitEdit() {
      this.editing = true
      try {
        await userApi.updateCurrentUser(this.editForm)
        this.$message.success('更新成功')
        this.editDialogVisible = false
        // 更新本地状态
        this.$store.dispatch('updateUserInfo', { ...this.userInfo, ...this.editForm })
      } catch (e) {
        // 错误已处理
      } finally {
        this.editing = false
      }
    },
    submitPassword() {
      this.$refs.passwordForm.validate(async valid => {
        if (valid) {
          this.submitting = true
          try {
            await userApi.updatePassword({
              oldPassword: this.passwordForm.oldPassword,
              newPassword: this.passwordForm.newPassword
            })
            this.$message.success('密码修改成功，请重新登录')
            this.$store.dispatch('logout')
            this.$router.push('/login')
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
.profile-page {
  .page-title {
    font-size: 24px;
    color: #303133;
    margin-bottom: 30px;
  }
}

.profile-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;

  @media (max-width: 992px) {
    grid-template-columns: 1fr;
  }
}

.profile-card {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  overflow: hidden;

  .card-header {
    padding: 20px;
    border-bottom: 1px solid #eee;
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 16px;
    font-weight: 600;
    color: #303133;
  }

  .profile-info {
    padding: 30px;

    .avatar-section {
      text-align: center;
      margin-bottom: 30px;

      .user-name {
        font-size: 20px;
        font-weight: 600;
        color: #303133;
        margin: 15px 0 10px;
      }
    }

    .info-grid {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 20px;

      .info-item {
        .label {
          display: block;
          color: #909399;
          font-size: 13px;
          margin-bottom: 5px;
        }

        .value {
          color: #303133;
          font-size: 15px;
        }
      }
    }
  }

  .el-form {
    padding: 30px;
  }
}
</style>
