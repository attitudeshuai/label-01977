<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useApi } from '@/composables/useApi'
import MainLayout from '@/components/layouts/MainLayout.vue'
import TiptapEditor from '@/components/TiptapEditor.vue'
import MoodSelector from '@/components/MoodSelector.vue'
import WeatherSelector from '@/components/WeatherSelector.vue'
import AppToast from '@/components/AppToast.vue'
import { ArrowLeft, Save, Loader2 } from 'lucide-vue-next'

const router = useRouter()
const route = useRoute()
const { journalApi } = useApi()

// Check if editing existing journal
const isEditing = computed(() => !!route.params.id)
const journalId = computed(() => route.params.id)

// Form state
const form = reactive({
  title: '',
  content: '',
  mood: null,
  weather: null
})

const loading = ref(false)
const saving = ref(false)
const toast = reactive({
  show: false,
  type: 'info',
  message: ''
})

// Load existing journal for editing
async function loadJournal() {
  if (!isEditing.value) return
  
  loading.value = true
  try {
    const response = await journalApi.get(journalId.value)
    if (response.success) {
      const journal = response.data
      form.title = journal.title
      form.content = journal.content
      form.mood = journal.mood
      form.weather = journal.weather
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

// Save journal
async function handleSave() {
  if (!form.title.trim()) {
    showToast('error', '请输入标题')
    return
  }
  if (!form.content.trim() || form.content === '<p></p>') {
    showToast('error', '请输入内容')
    return
  }

  saving.value = true
  try {
    let response
    if (isEditing.value) {
      response = await journalApi.update(journalId.value, form)
    } else {
      response = await journalApi.create(form)
    }

    if (response.success) {
      showToast('success', isEditing.value ? '更新成功' : '保存成功')
      setTimeout(() => {
        router.push(`/journal/${response.data.id}`)
      }, 500)
    } else {
      showToast('error', response.message || '保存失败')
    }
  } catch (error) {
    showToast('error', error.message || '保存失败')
  } finally {
    saving.value = false
  }
}

function showToast(type, message) {
  toast.type = type
  toast.message = message
  toast.show = true
}

onMounted(() => {
  loadJournal()
  
  // Check for date param (from calendar)
  const dateParam = route.query.date
  if (dateParam && !isEditing.value) {
    // Could pre-fill date info here if needed
  }
})
</script>

<template>
  <MainLayout>
    <!-- Loading -->
    <div v-if="loading" class="flex items-center justify-center py-20">
      <Loader2 class="w-8 h-8 text-peach animate-spin" />
    </div>

    <template v-else>
      <!-- Header -->
      <div class="flex items-center justify-between mb-6">
        <button 
          @click="router.back()"
          class="flex items-center gap-2 text-gray-600 hover:text-gray-800 transition-colors"
        >
          <ArrowLeft class="w-5 h-5" />
          <span>返回</span>
        </button>
        <h1 class="text-h2 text-gray-800">
          {{ isEditing ? '编辑日记' : '写日记' }}
        </h1>
        <div class="w-20"></div>
      </div>

      <!-- Form -->
      <div class="max-w-3xl mx-auto">
        <div class="card space-y-6">
          <!-- Title -->
          <div>
            <label class="label">标题</label>
            <input
              v-model="form.title"
              type="text"
              class="input text-lg"
              placeholder="给今天的日记起个标题吧"
              :disabled="saving"
            />
          </div>

          <!-- Mood & Weather -->
          <div class="grid grid-cols-1 sm:grid-cols-2 gap-6">
            <MoodSelector v-model="form.mood" />
            <WeatherSelector v-model="form.weather" />
          </div>

          <!-- Content -->
          <div>
            <label class="label">内容</label>
            <TiptapEditor 
              v-model="form.content"
              placeholder="开始记录今天的故事..."
            />
          </div>

          <!-- Actions -->
          <div class="flex justify-end gap-3 pt-4">
            <button
              @click="router.back()"
              class="btn-secondary"
              :disabled="saving"
            >
              取消
            </button>
            <button
              @click="handleSave"
              class="btn-primary flex items-center gap-2"
              :disabled="saving"
            >
              <Loader2 v-if="saving" class="w-5 h-5 animate-spin" />
              <Save v-else class="w-5 h-5" />
              <span>{{ saving ? '保存中...' : '保存' }}</span>
            </button>
          </div>
        </div>
      </div>
    </template>

    <!-- Toast -->
    <AppToast
      :show="toast.show"
      :type="toast.type"
      :message="toast.message"
      @close="toast.show = false"
    />
  </MainLayout>
</template>
