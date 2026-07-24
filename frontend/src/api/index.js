import axios from 'axios'
import { Message } from 'element-ui'
import store from '@/store'
import router from '@/router'

// 创建 axios 实例
const request = axios.create({
  baseURL: '/api',
  timeout: 30000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    const token = store.state.token
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      Message.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  error => {
    if (error.response) {
      const status = error.response.status
      if (status === 401) {
        Message.error('登录已过期，请重新登录')
        store.dispatch('logout')
        router.push('/login')
      } else if (status === 403) {
        Message.error('没有权限访问')
      } else {
        Message.error(error.response.data?.message || '网络错误')
      }
    } else {
      Message.error('网络连接失败')
    }
    return Promise.reject(error)
  }
)

// 认证相关
export const authApi = {
  login: data => request.post('/auth/login', data),
  register: data => request.post('/auth/register', data)
}

// 用户相关
export const userApi = {
  getCurrentUser: () => request.get('/user/info'),
  updateUser: (id, data) => request.put(`/user/${id}`, data),
  updateCurrentUser: data => request.put('/user/info', data),
  updatePassword: data => request.put('/user/password', data),
  getPage: params => request.get('/user/page', { params }),
  getDetail: id => request.get(`/user/${id}`),
  updateStatus: (id, status) => request.put(`/user/${id}/status`, null, { params: { status } }),
  delete: id => request.delete(`/user/${id}`)
}

// 店铺相关
export const shopApi = {
  getPage: params => request.get('/shop/page', { params }),
  getDetail: id => request.get(`/shop/${id}`),
  getMyShop: () => request.get('/shop/my'),
  create: data => request.post('/shop', data),
  update: (id, data) => request.put(`/shop/${id}`, data),
  updateStatus: (id, status) => request.put(`/shop/${id}/status`, null, { params: { status } }),
  delete: id => request.delete(`/shop/${id}`)
}

// 衣服类型相关
export const clothesTypeApi = {
  getPage: params => request.get('/clothes-type/page', { params }),
  getList: params => request.get('/clothes-type/list', { params }),
  getDetail: id => request.get(`/clothes-type/${id}`),
  create: data => request.post('/clothes-type', data),
  update: (id, data) => request.put(`/clothes-type/${id}`, data),
  updateStatus: (id, status) => request.put(`/clothes-type/${id}/status`, null, { params: { status } }),
  delete: id => request.delete(`/clothes-type/${id}`)
}

// 洗衣信息相关
export const laundryInfoApi = {
  getPage: params => request.get('/laundry-info/page', { params }),
  getList: params => request.get('/laundry-info/list', { params }),
  getDetail: id => request.get(`/laundry-info/${id}`),
  create: data => request.post('/laundry-info', data),
  update: (id, data) => request.put(`/laundry-info/${id}`, data),
  updateStatus: (id, status) => request.put(`/laundry-info/${id}/status`, null, { params: { status } }),
  delete: id => request.delete(`/laundry-info/${id}`)
}

// 订单相关
export const orderApi = {
  getPage: params => request.get('/order/page', { params }),
  getMyOrders: params => request.get('/order/my', { params }),
  getDetail: id => request.get(`/order/${id}`),
  getProgress: id => request.get(`/order/${id}/progress`),
  create: data => request.post('/order', data),
  pay: id => request.put(`/order/${id}/pay`),
  updateStatus: (id, status, description) => request.put(`/order/${id}/status`, null, { params: { status, description } }),
  cancel: id => request.put(`/order/${id}/cancel`),
  getStatistics: params => request.get('/order/statistics', { params })
}

// 订单进度相关
export const orderProgressApi = {
  getPage: params => request.get('/order-progress/page', { params }),
  add: data => request.post('/order-progress', data)
}

// 交流区相关
export const discussionApi = {
  getPage: params => request.get('/discussion/page', { params }),
  getDetail: id => request.get(`/discussion/${id}`),
  getComments: id => request.get(`/discussion/${id}/comments`),
  create: data => request.post('/discussion', data),
  update: (id, data) => request.put(`/discussion/${id}`, data),
  delete: id => request.delete(`/discussion/${id}`),
  like: id => request.post(`/discussion/${id}/like`),
  addComment: (id, data) => request.post(`/discussion/${id}/comment`, data)
}

// 公告相关
export const announcementApi = {
  getPage: params => request.get('/announcement/page', { params }),
  getList: () => request.get('/announcement/list'),
  getDetail: id => request.get(`/announcement/${id}`),
  create: data => request.post('/announcement', data),
  update: (id, data) => request.put(`/announcement/${id}`, data),
  updateStatus: (id, status) => request.put(`/announcement/${id}/status`, null, { params: { status } }),
  delete: id => request.delete(`/announcement/${id}`)
}

// 客服相关
export const customerServiceApi = {
  getPage: params => request.get('/customer-service/page', { params }),
  getMyMessages: () => request.get('/customer-service/my'),
  getUserMessages: userId => request.get(`/customer-service/user/${userId}`),
  send: data => request.post('/customer-service', data),
  reply: (userId, content) => request.post('/customer-service/reply', null, { params: { userId, content } }),
  markAsRead: userId => request.put(`/customer-service/read/${userId}`),
  getUnreadCount: params => request.get('/customer-service/unread', { params })
}

// 前台接口
export const frontApi = {
  getShops: params => request.get('/front/shops', { params }),
  getShopDetail: id => request.get(`/front/shops/${id}`),
  getShopLaundry: shopId => request.get(`/front/shops/${shopId}/laundry`),
  getLaundryDetail: id => request.get(`/front/laundry/${id}`),
  getClothesTypes: () => request.get('/front/clothes-types'),
  getAnnouncements: () => request.get('/front/announcements'),
  getAnnouncementDetail: id => request.get(`/front/announcements/${id}`),
  getDiscussions: params => request.get('/front/discussions', { params }),
  getDiscussionDetail: id => request.get(`/front/discussions/${id}`),
  getDiscussionComments: id => request.get(`/front/discussions/${id}/comments`)
}

export default request
