import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    redirect: '/home'
  },
  // 前台页面
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/front/Home.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/shops',
    name: 'Shops',
    component: () => import('@/views/front/Shops.vue'),
    meta: { title: '店铺信息' }
  },
  {
    path: '/shop/:id',
    name: 'ShopDetail',
    component: () => import('@/views/front/ShopDetail.vue'),
    meta: { title: '店铺详情' }
  },
  {
    path: '/discussions',
    name: 'Discussions',
    component: () => import('@/views/front/Discussions.vue'),
    meta: { title: '交流区' }
  },
  {
    path: '/discussion/:id',
    name: 'DiscussionDetail',
    component: () => import('@/views/front/DiscussionDetail.vue'),
    meta: { title: '帖子详情' }
  },
  {
    path: '/announcements',
    name: 'Announcements',
    component: () => import('@/views/front/Announcements.vue'),
    meta: { title: '系统公告' }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册' }
  },
  // 后台管理
  {
    path: '/admin',
    component: () => import('@/views/admin/Layout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        redirect: 'dashboard'
      },
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { title: '后台首页' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/admin/Profile.vue'),
        meta: { title: '个人中心' }
      },
      {
        path: 'customers',
        name: 'Customers',
        component: () => import('@/views/admin/Customers.vue'),
        meta: { title: '顾客管理', roles: ['admin'] }
      },
      {
        path: 'shop-owners',
        name: 'ShopOwners',
        component: () => import('@/views/admin/ShopOwners.vue'),
        meta: { title: '店家管理', roles: ['admin'] }
      },
      {
        path: 'shops',
        name: 'AdminShops',
        component: () => import('@/views/admin/Shops.vue'),
        meta: { title: '店铺信息管理' }
      },
      {
        path: 'clothes-types',
        name: 'ClothesTypes',
        component: () => import('@/views/admin/ClothesTypes.vue'),
        meta: { title: '衣服类型管理', roles: ['admin', 'shop_owner'] }
      },
      {
        path: 'laundry-info',
        name: 'LaundryInfo',
        component: () => import('@/views/admin/LaundryInfo.vue'),
        meta: { title: '洗衣信息管理' }
      },
      {
        path: 'orders',
        name: 'Orders',
        component: () => import('@/views/admin/Orders.vue'),
        meta: { title: '订单信息管理' }
      },
      {
        path: 'order-progress',
        name: 'OrderProgress',
        component: () => import('@/views/admin/OrderProgress.vue'),
        meta: { title: '订单进度管理' }
      },
      {
        path: 'discussions',
        name: 'AdminDiscussions',
        component: () => import('@/views/admin/Discussions.vue'),
        meta: { title: '交流区管理', roles: ['admin'] }
      },
      {
        path: 'announcements',
        name: 'AdminAnnouncements',
        component: () => import('@/views/admin/Announcements.vue'),
        meta: { title: '系统公告管理', roles: ['admin'] }
      },
      {
        path: 'customer-service',
        name: 'CustomerService',
        component: () => import('@/views/admin/CustomerService.vue'),
        meta: { title: '在线客服' }
      }
    ]
  }
]

const router = new VueRouter({
  mode: 'hash',
  base: process.env.BASE_URL,
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 洗衣店管理系统` : '洗衣店管理系统'
  
  const token = localStorage.getItem('token')
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  
  if (to.meta.requiresAuth && !token) {
    next({ path: '/login', query: { redirect: to.fullPath } })
  } else if (to.meta.roles && !to.meta.roles.includes(userInfo.role)) {
    next('/admin/dashboard')
  } else {
    next()
  }
})

export default router
