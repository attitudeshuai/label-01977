<script setup>
import { computed } from 'vue'

const props = defineProps({
  modelValue: String
})

const emit = defineEmits(['update:modelValue'])

const moods = [
  { value: 'happy', emoji: '😊', label: '开心', color: 'bg-mood-happy/20' },
  { value: 'calm', emoji: '😌', label: '平静', color: 'bg-mood-calm/20' },
  { value: 'sad', emoji: '😢', label: '难过', color: 'bg-mood-sad/20' },
  { value: 'angry', emoji: '😠', label: '愤怒', color: 'bg-mood-angry/20' },
  { value: 'anxious', emoji: '😰', label: '焦虑', color: 'bg-mood-anxious/20' }
]

function selectMood(value) {
  emit('update:modelValue', props.modelValue === value ? null : value)
}
</script>

<template>
  <div>
    <label class="label">今天的心情</label>
    <div class="flex flex-wrap gap-3">
      <button
        v-for="mood in moods"
        :key="mood.value"
        type="button"
        @click="selectMood(mood.value)"
        class="flex flex-col items-center justify-center w-16 h-16 rounded-lg border-2 transition-all duration-200"
        :class="modelValue === mood.value 
          ? `${mood.color} border-gray-300 scale-110` 
          : 'bg-white border-gray-100 hover:border-gray-200'"
      >
        <span class="text-2xl mb-1">{{ mood.emoji }}</span>
        <span class="text-caption text-gray-500">{{ mood.label }}</span>
      </button>
    </div>
  </div>
</template>
