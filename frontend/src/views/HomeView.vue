<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useJournalStore } from '@/stores/journal'
import { useApi } from '@/composables/useApi'
import MainLayout from '@/components/layouts/MainLayout.vue'
import AppToast from '@/components/AppToast.vue'
import { Plus, BookOpen, Calendar, Smile, Loader2 } from 'lucide-vue-next'

const router = useRouter()
const authStore = useAuthStore()
const journalStore = useJournalStore()
const { journalApi, statsApi, authApi } = useApi()

const loading = ref(true)
const journals = ref([])
const stats = ref({ totalJournals: 0, thisMonthJournals: 0, topMood: null })
const toast = reactive({
  show: false,
  type: 'info',
  message: ''
})

// Mood emoji mapping
const moodEmoji = {
  happy: '😊',
  calm: '😌',
  sad: '😢',
  angry: '😠',
  anxious: '😰'
}

// Mood label mapping (Chinese)
const moodLabel = {
  happy: '开心',
  calm: '平静',
  sad: '难过',
  angry: '愤怒',
  anxious: '焦虑'
}

// Weather emoji mapping
const weatherEmoji = {
  sunny: '☀️',
  cloudy: '⛅',
  overcast: '☁️',
  rainy: '🌧️',
  snowy: '❄️'
}

// Format date
function formatDate(dateStr) {
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', { 
    year: 'numeric', 
    month: 'long', 
    day: 'numeric' 
  })
}

// Truncate content (strip HTML)
function truncateContent(html, maxLength = 100) {
  const text = html.replace(/<[^>]*>/g, '')
  if (text.length <= maxLength) return text
  return text.slice(0, maxLength) + '...'
}

// Load data
async function loadData() {
  loading.value = true
  try {
    // Load user info if not loaded
    if (!authStore.user) {
      const userRes = await authApi.me()
      if (userRes.success) {
        authStore.setUser(userRes.data)
      }
    }

    // Load journals
    const journalRes = await journalApi.list({ page: 0, size: 10 })
    if (journalRes.success !== false) {
      journals.value = journalRes.data || []
    }

    // Load stats
    const statsRes = await statsApi.overview()
    if (statsRes.success) {
      stats.value = statsRes.data
    }
  } catch (error) {
    console.error('Failed to load data:', error)
    toast.type = 'error'
    toast.message = '加载数据失败'
    toast.show = true
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<template>
  <MainLayout>
    <!-- Loading State -->
    <div v-if="loading" class="flex items-center justify-center py-20">
      <Loader2 class="w-8 h-8 text-peach animate-spin" />
    </div>

    <template v-else>
      <!-- Stats Cards -->
      <div class="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-8">
        <div class="card flex items-center gap-4">
          <div class="w-12 h-12 rounded-full bg-peach-50 flex items-center justify-center">
            <BookOpen class="w-6 h-6 text-peach" />
          </div>
          <div>
            <p class="text-caption text-gray-500">日记总数</p>
            <p class="text-h2 text-gray-800">{{ stats.totalJournals }}</p>
          </div>
        </div>

        <div class="card flex items-center gap-4">
          <div class="w-12 h-12 rounded-full bg-sky/20 flex items-center justify-center">
            <Calendar class="w-6 h-6 text-blue-500" />
          </div>
          <div>
            <p class="text-caption text-gray-500">本月日记</p>
            <p class="text-h2 text-gray-800">{{ stats.thisMonthJournals }}</p>
          </div>
        </div>

        <div class="card flex items-center gap-4">
          <div class="w-12 h-12 rounded-full bg-butter/30 flex items-center justify-center">
            <span class="text-2xl">{{ stats.topMood ? moodEmoji[stats.topMood] : '😊' }}</span>
          </div>
          <div>
            <p class="text-caption text-gray-500">最近心情</p>
            <p class="text-h3 text-gray-800">{{ stats.topMood ? moodLabel[stats.topMood] : '暂无' }}</p>
          </div>
        </div>
      </div>

      <!-- Journal List -->
      <div class="mb-6 flex items-center justify-between">
        <h2 class="text-h2 text-gray-800">最近日记</h2>
        <router-link to="/calendar" class="text-body-sm text-peach hover:text-peach-dark flex items-center gap-1">
          <Calendar class="w-4 h-4" />
          日历视图
        </router-link>
      </div>

      <!-- Empty State -->
      <div v-if="journals.length === 0" class="card text-center py-12">
        <div class="text-5xl mb-4 animate-float">📔</div>
        <h3 class="text-h3 text-gray-700 mb-2">还没有日记呢</h3>
        <p class="text-body text-gray-500 mb-6">开始记录你的第一篇日记吧~</p>
        <button @click="router.push('/journal/new')" class="btn-primary">
          <Plus class="w-5 h-5 inline mr-1" />
          写日记
        </button>
      </div>

      <!-- Journal Cards -->
      <div v-else class="space-y-4">
        <div 
          v-for="journal in journals" 
          :key="journal.id"
          @click="router.push(`/journal/${journal.id}`)"
          class="card cursor-pointer group"
        >
          <div class="flex items-start justify-between mb-3">
            <div class="flex items-center gap-2">
              <span v-if="journal.mood" class="text-xl">{{ moodEmoji[journal.mood] }}</span>
              <span v-if="journal.weather" class="text-xl">{{ weatherEmoji[journal.weather] }}</span>
            </div>
            <span class="text-caption text-gray-400">{{ formatDate(journal.createdAt) }}</span>
          </div>
          <h3 class="text-h3 text-gray-800 mb-2 group-hover:text-peach transition-colors">
            {{ journal.title }}
          </h3>
          <p class="text-body text-gray-500 line-clamp-2">
            {{ truncateContent(journal.content) }}
          </p>
        </div>
      </div>
    </template>

    <!-- FAB - New Journal -->
    <button
      @click="router.push('/journal/new')"
      class="fixed bottom-6 right-6 w-14 h-14 bg-peach text-white rounded-full shadow-lg hover:shadow-glow hover:-translate-y-1 transition-all duration-200 flex items-center justify-center"
    >
      <Plus class="w-6 h-6" />
    </button>

    <!-- Toast -->
    <AppToast
      :show="toast.show"
      :type="toast.type"
      :message="toast.message"
      @close="toast.show = false"
    />
  </MainLayout>
</template>
