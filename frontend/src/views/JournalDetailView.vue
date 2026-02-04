<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useApi } from '@/composables/useApi'
import MainLayout from '@/components/layouts/MainLayout.vue'
import AppToast from '@/components/AppToast.vue'
import { ArrowLeft, Edit, Trash2, Loader2 } from 'lucide-vue-next'

const router = useRouter()
const route = useRoute()
const { journalApi } = useApi()

const journal = ref(null)
const loading = ref(true)
const deleting = ref(false)
const showDeleteConfirm = ref(false)
const toast = reactive({
  show: false,
  type: 'info',
  message: ''
})

// Mood/Weather emoji mapping
const moodEmoji = {
  happy: '😊',
  calm: '😌',
  sad: '😢',
  angry: '😠',
  anxious: '😰'
}

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
    day: 'numeric',
    weekday: 'long'
  })
}

// Load journal
async function loadJournal() {
  loading.value = true
  try {
    const response = await journalApi.get(route.params.id)
    if (response.success) {
      journal.value = response.data
    } else {
      showToast('error', '日记不存在')
      router.push('/')
    }
  } catch (error) {
    showToast('error', '加载失败')
    router.push('/')
  } finally {
    loading.value = false
  }
}

// Delete journal
async function handleDelete() {
  deleting.value = true
  try {
    const response = await journalApi.delete(route.params.id)
    if (response.success) {
      showToast('success', '删除成功')
      setTimeout(() => router.push('/'), 500)
    } else {
      showToast('error', response.message || '删除失败')
    }
  } catch (error) {
    showToast('error', '删除失败')
  } finally {
    deleting.value = false
    showDeleteConfirm.value = false
  }
}

function showToast(type, message) {
  toast.type = type
  toast.message = message
  toast.show = true
}

onMounted(() => {
  loadJournal()
})
</script>

<template>
  <MainLayout>
    <!-- Loading -->
    <div v-if="loading" class="flex items-center justify-center py-20">
      <Loader2 class="w-8 h-8 text-peach animate-spin" />
    </div>

    <template v-else-if="journal">
      <!-- Header -->
      <div class="flex items-center justify-between mb-6">
        <button 
          @click="router.push('/')"
          class="flex items-center gap-2 text-gray-600 hover:text-gray-800 transition-colors"
        >
          <ArrowLeft class="w-5 h-5" />
          <span>返回</span>
        </button>
        <div class="flex items-center gap-2">
          <button 
            @click="router.push(`/journal/${journal.id}/edit`)"
            class="flex items-center gap-2 px-4 py-2 text-body-sm text-gray-600 hover:text-peach transition-colors"
          >
            <Edit class="w-4 h-4" />
            <span>编辑</span>
          </button>
          <button 
            @click="showDeleteConfirm = true"
            class="flex items-center gap-2 px-4 py-2 text-body-sm text-gray-600 hover:text-red-500 transition-colors"
          >
            <Trash2 class="w-4 h-4" />
            <span>删除</span>
          </button>
        </div>
      </div>

      <!-- Journal Content -->
      <div class="max-w-3xl mx-auto">
        <article class="card">
          <!-- Meta -->
          <div class="flex items-center justify-between mb-4">
            <div class="flex items-center gap-3">
              <span v-if="journal.mood" class="text-3xl">{{ moodEmoji[journal.mood] }}</span>
              <span v-if="journal.weather" class="text-3xl">{{ weatherEmoji[journal.weather] }}</span>
            </div>
            <span class="text-body-sm text-gray-400">{{ formatDate(journal.createdAt) }}</span>
          </div>

          <!-- Title -->
          <h1 class="text-h1 text-gray-800 mb-6">{{ journal.title }}</h1>

          <!-- Content -->
          <div 
            class="prose prose-lg max-w-none text-gray-700"
            v-html="journal.content"
          ></div>
        </article>
      </div>
    </template>

    <!-- Delete Confirmation Modal -->
    <Teleport to="body">
      <Transition
        enter-active-class="transition-opacity duration-200"
        enter-from-class="opacity-0"
        enter-to-class="opacity-100"
        leave-active-class="transition-opacity duration-150"
        leave-from-class="opacity-100"
        leave-to-class="opacity-0"
      >
        <div 
          v-if="showDeleteConfirm"
          class="fixed inset-0 z-50 flex items-center justify-center bg-black/50"
          @click.self="showDeleteConfirm = false"
        >
          <div class="bg-white rounded-lg p-6 max-w-sm mx-4 shadow-xl">
            <h3 class="text-h3 text-gray-800 mb-2">确认删除</h3>
            <p class="text-body text-gray-500 mb-6">删除后将无法恢复，确定要删除这篇日记吗？</p>
            <div class="flex justify-end gap-3">
              <button
                @click="showDeleteConfirm = false"
                class="btn-secondary"
                :disabled="deleting"
              >
                取消
              </button>
              <button
                @click="handleDelete"
                class="px-6 py-3 bg-red-500 text-white font-medium rounded-md hover:bg-red-600 transition-colors flex items-center gap-2"
                :disabled="deleting"
              >
                <Loader2 v-if="deleting" class="w-5 h-5 animate-spin" />
                <span>{{ deleting ? '删除中...' : '确认删除' }}</span>
              </button>
            </div>
          </div>
        </div>
      </Transition>
    </Teleport>

    <!-- Toast -->
    <AppToast
      :show="toast.show"
      :type="toast.type"
      :message="toast.message"
      @close="toast.show = false"
    />
  </MainLayout>
</template>

<style>
/* Prose styles for rendered content */
.prose h1 { @apply text-2xl font-semibold mb-4; }
.prose h2 { @apply text-xl font-semibold mb-3; }
.prose h3 { @apply text-lg font-semibold mb-2; }
.prose p { @apply mb-4; }
.prose ul { @apply list-disc pl-6 mb-4; }
.prose ol { @apply list-decimal pl-6 mb-4; }
.prose blockquote { 
  @apply border-l-4 border-peach pl-4 py-2 bg-peach-50 rounded-r-lg mb-4 italic;
}
.prose pre {
  @apply bg-gray-100 p-4 rounded-lg mb-4 overflow-x-auto;
}
.prose code {
  @apply font-mono text-sm;
}
</style>
