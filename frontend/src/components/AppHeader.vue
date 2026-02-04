<script setup>
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { Calendar, LogOut, User, Home } from 'lucide-vue-next'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const navItems = [
  { name: 'home', path: '/', icon: Home, label: '首页' },
  { name: 'calendar', path: '/calendar', icon: Calendar, label: '日历' }
]

function handleLogout() {
  authStore.logout()
  router.push('/login')
}
</script>

<template>
  <header class="sticky top-0 z-40 bg-white/80 backdrop-blur-md border-b border-gray-100">
    <div class="max-w-6xl mx-auto px-4 h-16 flex items-center justify-between">
      <!-- Logo -->
      <router-link to="/" class="flex items-center gap-2 group">
        <span class="text-2xl">🌸</span>
        <span class="text-h3 text-gray-800 group-hover:text-peach transition-colors">
          心情随笔
        </span>
      </router-link>

      <!-- Navigation -->
      <nav class="flex items-center gap-1">
        <router-link
          v-for="item in navItems"
          :key="item.name"
          :to="item.path"
          class="flex items-center gap-2 px-4 py-2 rounded-md text-body-sm transition-all duration-200"
          :class="route.path === item.path 
            ? 'bg-peach-50 text-peach' 
            : 'text-gray-600 hover:bg-gray-50 hover:text-gray-800'"
        >
          <component :is="item.icon" class="w-4 h-4" />
          <span class="hidden sm:inline">{{ item.label }}</span>
        </router-link>
      </nav>

      <!-- User Menu -->
      <div class="flex items-center gap-3">
        <div v-if="authStore.user" class="hidden sm:flex items-center gap-2 text-body-sm text-gray-600">
          <User class="w-4 h-4" />
          <span>{{ authStore.user.username }}</span>
        </div>
        <button 
          @click="handleLogout"
          class="flex items-center gap-2 px-3 py-2 text-body-sm text-gray-500 hover:text-red-500 transition-colors"
          title="退出登录"
        >
          <LogOut class="w-4 h-4" />
          <span class="hidden sm:inline">退出</span>
        </button>
      </div>
    </div>
  </header>
</template>
