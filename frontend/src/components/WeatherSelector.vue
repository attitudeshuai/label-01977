<script setup>
import { computed } from 'vue'

const props = defineProps({
  modelValue: String
})

const emit = defineEmits(['update:modelValue'])

const weathers = [
  { value: 'sunny', emoji: '☀️', label: '晴', color: 'bg-weather-sunny/20' },
  { value: 'cloudy', emoji: '⛅', label: '多云', color: 'bg-weather-cloudy/20' },
  { value: 'overcast', emoji: '☁️', label: '阴', color: 'bg-weather-overcast/20' },
  { value: 'rainy', emoji: '🌧️', label: '雨', color: 'bg-weather-rainy/20' },
  { value: 'snowy', emoji: '❄️', label: '雪', color: 'bg-weather-snowy/20' }
]

function selectWeather(value) {
  emit('update:modelValue', props.modelValue === value ? null : value)
}
</script>

<template>
  <div>
    <label class="label">今天的天气</label>
    <div class="flex flex-wrap gap-3">
      <button
        v-for="weather in weathers"
        :key="weather.value"
        type="button"
        @click="selectWeather(weather.value)"
        class="flex flex-col items-center justify-center w-16 h-16 rounded-lg border-2 transition-all duration-200"
        :class="modelValue === weather.value 
          ? `${weather.color} border-gray-300 scale-110` 
          : 'bg-white border-gray-100 hover:border-gray-200'"
      >
        <span class="text-2xl mb-1">{{ weather.emoji }}</span>
        <span class="text-caption text-gray-500">{{ weather.label }}</span>
      </button>
    </div>
  </div>
</template>
