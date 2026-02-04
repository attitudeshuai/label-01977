<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useApi } from '@/composables/useApi'
import AppToast from '@/components/AppToast.vue'
import { Eye, EyeOff, Loader2 } from 'lucide-vue-next'

const router = useRouter()
const authStore = useAuthStore()
const { authApi } = useApi()

const form = reactive({
  username: '',
  password: ''
})

const showPassword = ref(false)
const loading = ref(false)
const toast = reactive({
  show: false,
  type: 'error',
  message: ''
})

async function handleSubmit() {
  if (!form.username || !form.password) {
    showToast('error', '请填写用户名和密码')
    return
  }

  loading.value = true
  try {
    const response = await authApi.login(form)
    if (response.success) {
      authStore.setToken(response.token)
      authStore.setUser(response.user)
      showToast('success', '登录成功')
      setTimeout(() => router.push('/'), 500)
    } else {
      showToast('error', response.message || '登录失败')
    }
  } catch (error) {
    showToast('error', error.message || '登录失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

function showToast(type, message) {
  toast.type = type
  toast.message = message
  toast.show = true
}
</script>

<template>
  <div class="min-h-screen bg-cream flex items-center justify-center p-4">
    <div class="w-full max-w-md">
      <!-- Logo -->
      <div class="text-center mb-8">
        <div class="text-5xl mb-4 animate-float">🌸</div>
        <h1 class="text-h1 text-gray-800 mb-2">心情随笔</h1>
        <p class="text-body text-gray-500">登录以继续记录美好时光</p>
      </div>

      <!-- Login Form -->
      <div class="card">
        <form @submit.prevent="handleSubmit" class="space-y-5">
          <!-- Username -->
          <div>
            <label class="label">用户名 / 邮箱</label>
            <input
              v-model="form.username"
              type="text"
              class="input"
              placeholder="请输入用户名或邮箱"
              :disabled="loading"
            />
          </div>

          <!-- Password -->
          <div>
            <label class="label">密码</label>
            <div class="relative">
              <input
                v-model="form.password"
                :type="showPassword ? 'text' : 'password'"
                class="input pr-10"
                placeholder="请输入密码"
                :disabled="loading"
              />
              <button
                type="button"
                @click="showPassword = !showPassword"
                class="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600"
              >
                <Eye v-if="!showPassword" class="w-5 h-5" />
                <EyeOff v-else class="w-5 h-5" />
              </button>
            </div>
          </div>

          <!-- Submit -->
          <button
            type="submit"
            class="btn-primary w-full flex items-center justify-center gap-2"
            :disabled="loading"
          >
            <Loader2 v-if="loading" class="w-5 h-5 animate-spin" />
            <span>{{ loading ? '登录中...' : '登录' }}</span>
          </button>
        </form>

        <!-- Register Link -->
        <div class="mt-6 text-center text-body-sm text-gray-500">
          还没有账号？
          <router-link to="/register" class="text-peach hover:text-peach-dark transition-colors">
            立即注册
          </router-link>
        </div>
      </div>
    </div>

    <!-- Toast -->
    <AppToast
      :show="toast.show"
      :type="toast.type"
      :message="toast.message"
      @close="toast.show = false"
    />
  </div>
</template>
