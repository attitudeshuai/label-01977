import axios from 'axios'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'

/**
 * API Composable
 * Axios 实例配置 + 拦截器
 */

// Create axios instance
const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Request interceptor - add JWT token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor - handle errors
api.interceptors.response.use(
  (response) => {
    return response.data
  },
  (error) => {
    // Handle 401 - Unauthorized
    if (error.response?.status === 401) {
      const authStore = useAuthStore()
      authStore.logout()
      // Redirect to login (if router is available)
      if (typeof window !== 'undefined') {
        window.location.href = '/login'
      }
    }
    return Promise.reject(error.response?.data || error)
  }
)

/**
 * useApi composable
 */
export function useApi() {
  // Auth APIs
  const authApi = {
    register: (data) => api.post('/auth/register', data),
    login: (data) => api.post('/auth/login', data),
    me: () => api.get('/auth/me'),
    logout: () => api.post('/auth/logout')
  }

  // Journal APIs
  const journalApi = {
    list: (params) => api.get('/journals', { params }),
    get: (id) => api.get(`/journals/${id}`),
    create: (data) => api.post('/journals', data),
    update: (id, data) => api.put(`/journals/${id}`, data),
    delete: (id) => api.delete(`/journals/${id}`),
    search: (q) => api.get('/journals/search', { params: { q } })
  }

  // Calendar API
  const calendarApi = {
    getMonth: (year, month) => api.get(`/calendar/${year}/${month}`)
  }

  // Stats API
  const statsApi = {
    overview: () => api.get('/stats/overview')
  }

  // Health check
  const healthApi = {
    check: () => api.get('/health')
  }

  return {
    api,
    authApi,
    journalApi,
    calendarApi,
    statsApi,
    healthApi
  }
}

export default api
