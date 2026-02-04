<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()
</script>

<template>
  <div class="min-h-screen bg-cream flex items-center justify-center p-8">
    <div class="text-center">
      <div class="text-8xl mb-4 animate-float">🌙</div>
      <h1 class="text-display text-gray-800 mb-2">404</h1>
      <p class="text-body-lg text-gray-500 mb-8">页面不存在，迷路了吗？</p>
      <button 
        @click="router.push('/')"
        class="btn-primary"
      >
        返回首页
      </button>
    </div>
  </div>
</template>
