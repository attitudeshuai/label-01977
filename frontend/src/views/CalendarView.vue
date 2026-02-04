<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useApi } from '@/composables/useApi'
import MainLayout from '@/components/layouts/MainLayout.vue'
import AppToast from '@/components/AppToast.vue'
import { ChevronLeft, ChevronRight, Loader2 } from 'lucide-vue-next'

const router = useRouter()
const { calendarApi } = useApi()

// Current viewing date
const currentYear = ref(new Date().getFullYear())
const currentMonth = ref(new Date().getMonth() + 1) // 1-12

// Calendar data from API
const calendarData = ref([])
const loading = ref(true)

// Today
const today = new Date()
const todayStr = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`

// Toast
const toast = reactive({
  show: false,
  type: 'info',
  message: ''
})

// Mood/Weather emoji
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

// Month names
const monthNames = ['一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月']
const weekDays = ['日', '一', '二', '三', '四', '五', '六']

// Compute calendar grid
const calendarGrid = computed(() => {
  const year = currentYear.value
  const month = currentMonth.value
  
  // First day of month (0=Sunday)
  const firstDay = new Date(year, month - 1, 1).getDay()
  // Days in month
  const daysInMonth = new Date(year, month, 0).getDate()
  
  // Create grid
  const grid = []
  let week = []
  
  // Fill empty days before first day
  for (let i = 0; i < firstDay; i++) {
    week.push(null)
  }
  
  // Fill days
  for (let day = 1; day <= daysInMonth; day++) {
    const dateStr = `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`
    const journalEntry = calendarData.value.find(j => j.date === dateStr)
    
    week.push({
      day,
      dateStr,
      isToday: dateStr === todayStr,
      journal: journalEntry || null
    })
    
    if (week.length === 7) {
      grid.push(week)
      week = []
    }
  }
  
  // Fill remaining days
  if (week.length > 0) {
    while (week.length < 7) {
      week.push(null)
    }
    grid.push(week)
  }
  
  return grid
})

// Load calendar data
async function loadCalendarData() {
  loading.value = true
  try {
    const response = await calendarApi.getMonth(currentYear.value, currentMonth.value)
    if (response.success) {
      calendarData.value = response.data || []
    }
  } catch (error) {
    console.error('Failed to load calendar:', error)
    toast.type = 'error'
    toast.message = '加载日历数据失败'
    toast.show = true
  } finally {
    loading.value = false
  }
}

// Navigate months
function prevMonth() {
  if (currentMonth.value === 1) {
    currentMonth.value = 12
    currentYear.value--
  } else {
    currentMonth.value--
  }
}

function nextMonth() {
  if (currentMonth.value === 12) {
    currentMonth.value = 1
    currentYear.value++
  } else {
    currentMonth.value++
  }
}

// Handle date click
function handleDateClick(cell) {
  if (!cell) return
  
  if (cell.journal) {
    // Has journal - go to detail
    router.push(`/journal/${cell.journal.journalId}`)
  } else {
    // No journal - go to create with date
    router.push(`/journal/new?date=${cell.dateStr}`)
  }
}

// Watch for month/year changes
watch([currentYear, currentMonth], () => {
  loadCalendarData()
})

onMounted(() => {
  loadCalendarData()
})
</script>

<template>
  <MainLayout>
    <div class="max-w-4xl mx-auto">
      <!-- Calendar Header -->
      <div class="flex items-center justify-between mb-6">
        <button 
          @click="prevMonth"
          class="p-2 rounded-full hover:bg-gray-100 transition-colors"
        >
          <ChevronLeft class="w-6 h-6 text-gray-600" />
        </button>
        
        <h1 class="text-h1 text-gray-800">
          {{ currentYear }}年 {{ monthNames[currentMonth - 1] }}
        </h1>
        
        <button 
          @click="nextMonth"
          class="p-2 rounded-full hover:bg-gray-100 transition-colors"
        >
          <ChevronRight class="w-6 h-6 text-gray-600" />
        </button>
      </div>

      <!-- Calendar Grid -->
      <div class="card overflow-hidden">
        <!-- Week Headers -->
        <div class="grid grid-cols-7 bg-gray-50 border-b border-gray-100">
          <div 
            v-for="day in weekDays" 
            :key="day"
            class="py-3 text-center text-body-sm font-medium text-gray-500"
          >
            {{ day }}
          </div>
        </div>

        <!-- Loading -->
        <div v-if="loading" class="flex items-center justify-center py-20">
          <Loader2 class="w-8 h-8 text-peach animate-spin" />
        </div>

        <!-- Calendar Days -->
        <div v-else>
          <div 
            v-for="(week, weekIndex) in calendarGrid" 
            :key="weekIndex"
            class="grid grid-cols-7 border-b border-gray-100 last:border-b-0"
          >
            <div
              v-for="(cell, cellIndex) in week"
              :key="cellIndex"
              @click="handleDateClick(cell)"
              class="min-h-[80px] p-2 border-r border-gray-100 last:border-r-0 transition-colors"
              :class="[
                cell ? 'cursor-pointer hover:bg-peach-50' : 'bg-gray-50',
                cell?.isToday ? 'bg-peach-50/50' : ''
              ]"
            >
              <template v-if="cell">
                <!-- Day Number -->
                <div class="flex items-center justify-between mb-1">
                  <span 
                    class="w-7 h-7 flex items-center justify-center rounded-full text-body-sm"
                    :class="cell.isToday ? 'bg-peach text-white font-medium' : 'text-gray-700'"
                  >
                    {{ cell.day }}
                  </span>
                </div>
                
                <!-- Journal Indicator -->
                <div v-if="cell.journal" class="flex items-center gap-1 flex-wrap">
                  <span v-if="cell.journal.mood" class="text-lg">
                    {{ moodEmoji[cell.journal.mood] }}
                  </span>
                  <span v-if="cell.journal.weather" class="text-lg">
                    {{ weatherEmoji[cell.journal.weather] }}
                  </span>
                </div>
              </template>
            </div>
          </div>
        </div>
      </div>

      <!-- Legend -->
      <div class="mt-6 flex flex-wrap items-center justify-center gap-6 text-body-sm text-gray-500">
        <div class="flex items-center gap-2">
          <div class="w-4 h-4 rounded-full bg-peach"></div>
          <span>今天</span>
        </div>
        <div class="flex items-center gap-2">
          <span>😊☀️</span>
          <span>有日记</span>
        </div>
        <div class="flex items-center gap-2">
          <div class="w-4 h-4 rounded bg-gray-100"></div>
          <span>点击可新建日记</span>
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
  </MainLayout>
</template>
