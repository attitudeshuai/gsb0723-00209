<template>
  <div class="customer-service-page">
    <h2 class="page-title">在线客服</h2>

    <div class="service-container">
      <!-- 用户列表（管理员视图） -->
      <div class="user-list" v-if="isAdmin">
        <h3>用户列表</h3>
        <div class="user-item" v-for="user in userList" :key="user.userId" :class="{ active: currentUserId === user.userId }" @click="selectUser(user)">
          <el-avatar :size="40" icon="el-icon-user-solid"></el-avatar>
          <div class="user-info">
            <span class="name">{{ user.userName || '用户' + user.userId }}</span>
            <span class="preview">{{ user.content }}</span>
          </div>
          <el-badge :value="user.unreadCount" v-if="user.unreadCount > 0" />
        </div>
        <el-empty v-if="userList.length === 0" description="暂无消息" />
      </div>

      <!-- 聊天窗口 -->
      <div class="chat-window">
        <div class="chat-header" v-if="isAdmin && currentUserId">
          正在与 {{ currentUserName }} 对话
        </div>
        <div class="messages-container" ref="messagesContainer">
          <div v-for="msg in messages" :key="msg.id" :class="['message', isMyMessage(msg) ? 'mine' : 'other']">
            <el-avatar :size="36" icon="el-icon-user-solid"></el-avatar>
            <div class="message-content">
              <div class="bubble">{{ msg.content }}</div>
              <div class="time">{{ formatDate(msg.createTime) }}</div>
            </div>
          </div>
          <el-empty v-if="messages.length === 0" description="暂无消息，发送消息开始对话" />
        </div>
        <div class="input-container" v-if="!isAdmin || currentUserId">
          <el-input v-model="messageInput" placeholder="输入消息..." @keyup.enter.native="sendMessage" />
          <el-button type="primary" @click="sendMessage" :loading="sending">发送</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { customerServiceApi } from '@/api'

export default {
  name: 'CustomerService',
  data() {
    return {
      userList: [], messages: [], messageInput: '', sending: false,
      currentUserId: null, currentUserName: ''
    }
  },
  computed: {
    ...mapGetters(['isAdmin', 'userInfo'])
  },
  created() {
    if (this.isAdmin) {
      this.loadUserList()
    } else {
      this.loadMyMessages()
    }
  },
  methods: {
    async loadUserList() {
      try {
        const res = await customerServiceApi.getPage({ pageNum: 1, pageSize: 100 })
        // 按用户分组
        const userMap = {}
        res.data.records.forEach(msg => {
          if (!userMap[msg.userId]) {
            userMap[msg.userId] = { userId: msg.userId, userName: msg.userName, content: msg.content, unreadCount: 0 }
          }
          if (msg.type === 0 && msg.isRead === 0) {
            userMap[msg.userId].unreadCount++
          }
        })
        this.userList = Object.values(userMap)
      } catch (e) {}
    },
    async loadMyMessages() {
      try {
        const res = await customerServiceApi.getMyMessages()
        this.messages = res.data
        this.$nextTick(() => this.scrollToBottom())
      } catch (e) {}
    },
    async selectUser(user) {
      this.currentUserId = user.userId
      this.currentUserName = user.userName
      try {
        const res = await customerServiceApi.getUserMessages(user.userId)
        this.messages = res.data
        await customerServiceApi.markAsRead(user.userId)
        user.unreadCount = 0
        this.$nextTick(() => this.scrollToBottom())
      } catch (e) {}
    },
    async sendMessage() {
      if (!this.messageInput.trim()) return
      this.sending = true
      try {
        if (this.isAdmin) {
          await customerServiceApi.reply(this.currentUserId, this.messageInput)
        } else {
          await customerServiceApi.send({ content: this.messageInput })
        }
        this.messageInput = ''
        if (this.isAdmin) {
          const res = await customerServiceApi.getUserMessages(this.currentUserId)
          this.messages = res.data
        } else {
          await this.loadMyMessages()
        }
      } catch (e) {} finally { this.sending = false }
    },
    isMyMessage(msg) {
      if (this.isAdmin) return msg.type === 1
      return msg.type === 0
    },
    scrollToBottom() {
      const container = this.$refs.messagesContainer
      if (container) container.scrollTop = container.scrollHeight
    },
    formatDate(date) {
      return date ? new Date(date).toLocaleString() : ''
    }
  }
}
</script>

<style lang="scss" scoped>
.customer-service-page { .page-title { font-size: 24px; color: #303133; margin-bottom: 20px; } }

.service-container {
  display: flex;
  gap: 20px;
  height: calc(100vh - 180px);
  min-height: 500px;
}

.user-list {
  width: 280px;
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  overflow-y: auto;

  h3 { font-size: 16px; color: #303133; margin-bottom: 15px; }

  .user-item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 12px;
    border-radius: 8px;
    cursor: pointer;
    transition: background 0.3s;

    &:hover, &.active { background: #f5f7fa; }

    .user-info {
      flex: 1;
      overflow: hidden;

      .name { display: block; font-weight: 500; color: #303133; }
      .preview { display: block; font-size: 12px; color: #909399; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
    }
  }
}

.chat-window {
  flex: 1;
  background: #fff;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);

  .chat-header {
    padding: 15px 20px;
    border-bottom: 1px solid #eee;
    font-weight: 500;
    color: #303133;
  }

  .messages-container {
    flex: 1;
    padding: 20px;
    overflow-y: auto;
    background: #f5f7fa;

    .message {
      display: flex;
      gap: 12px;
      margin-bottom: 20px;

      &.mine {
        flex-direction: row-reverse;
        .bubble { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: #fff; }
      }

      &.other .bubble { background: #fff; }

      .message-content {
        max-width: 60%;

        .bubble { padding: 12px 16px; border-radius: 12px; line-height: 1.5; }
        .time { font-size: 12px; color: #909399; margin-top: 5px; }
      }
    }
  }

  .input-container {
    padding: 15px 20px;
    border-top: 1px solid #eee;
    display: flex;
    gap: 10px;
  }
}
</style>
