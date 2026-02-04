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
  email: '',
  password: '',
  confirmPassword: ''
})

const showPassword = ref(false)
const loading = ref(false)
const toast = reactive({
  show: false,
  type: 'error',
  message: ''
})

async function handleSubmit() {
  // Validation
  if (!form.username || !form.email || !form.password) {
    showToast('error', '请填写所有必填项')
    return
  }
  if (form.password !== form.confirmPassword) {
    showToast('error', '两次输入的密码不一致')
    return
  }
  if (form.password.length < 6) {
    showToast('error', '密码长度至少6位')
    return
  }

  loading.value = true
  try {
    const response = await authApi.register({
      username: form.username,
      email: form.email,
      password: form.password
    })
    if (response.success) {
      authStore.setToken(response.token)
      authStore.setUser(response.user)
      showToast('success', '注册成功')
      setTimeout(() => router.push('/'), 500)
    } else {
      showToast('error', response.message || '注册失败')
    }
  } catch (error) {
    showToast('error', error.message || '注册失败，请稍后重试')
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
        <p class="text-body text-gray-500">创建账号，开始记录美好时光</p>
      </div>

      <!-- Register Form -->
      <div class="card">
        <form @submit.prevent="handleSubmit" class="space-y-5">
          <!-- Username -->
          <div>
            <label class="label">用户名</label>
            <input
              v-model="form.username"
              type="text"
              class="input"
              placeholder="请输入用户名"
              :disabled="loading"
            />
          </div>

          <!-- Email -->
          <div>
            <label class="label">邮箱</label>
            <input
              v-model="form.email"
              type="email"
              class="input"
              placeholder="请输入邮箱"
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
                placeholder="请输入密码 (至少6位)"
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

          <!-- Confirm Password -->
          <div>
            <label class="label">确认密码</label>
            <input
              v-model="form.confirmPassword"
              type="password"
              class="input"
              placeholder="请再次输入密码"
              :disabled="loading"
            />
          </div>

          <!-- Submit -->
          <button
            type="submit"
            class="btn-primary w-full flex items-center justify-center gap-2"
            :disabled="loading"
          >
            <Loader2 v-if="loading" class="w-5 h-5 animate-spin" />
            <span>{{ loading ? '注册中...' : '注册' }}</span>
          </button>
        </form>

        <!-- Login Link -->
        <div class="mt-6 text-center text-body-sm text-gray-500">
          已有账号？
          <router-link to="/login" class="text-peach hover:text-peach-dark transition-colors">
            立即登录
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
