import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    token: localStorage.getItem('token') || '',
    userInfo: JSON.parse(localStorage.getItem('userInfo') || '{}')
  },
  
  getters: {
    isLoggedIn: state => !!state.token,
    userInfo: state => state.userInfo,
    userRole: state => state.userInfo.role || '',
    isAdmin: state => state.userInfo.role === 'admin',
    isShopOwner: state => state.userInfo.role === 'shop_owner',
    isCustomer: state => state.userInfo.role === 'customer'
  },
  
  mutations: {
    SET_TOKEN(state, token) {
      state.token = token
      localStorage.setItem('token', token)
    },
    
    SET_USER_INFO(state, userInfo) {
      state.userInfo = userInfo
      localStorage.setItem('userInfo', JSON.stringify(userInfo))
    },
    
    LOGOUT(state) {
      state.token = ''
      state.userInfo = {}
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
    }
  },
  
  actions: {
    login({ commit }, { token, user }) {
      commit('SET_TOKEN', token)
      commit('SET_USER_INFO', user)
    },
    
    logout({ commit }) {
      commit('LOGOUT')
    },
    
    updateUserInfo({ commit }, userInfo) {
      commit('SET_USER_INFO', userInfo)
    }
  }
})
