<script setup>
import { ref, watch } from 'vue'
import { X, CheckCircle, AlertCircle, AlertTriangle, Info } from 'lucide-vue-next'

const props = defineProps({
  show: Boolean,
  type: {
    type: String,
    default: 'success', // success, error, warning, info
    validator: (value) => ['success', 'error', 'warning', 'info'].includes(value)
  },
  message: String,
  duration: {
    type: Number,
    default: 3000
  }
})

const emit = defineEmits(['close'])

const visible = ref(false)

watch(() => props.show, (newVal) => {
  if (newVal) {
    visible.value = true
    if (props.duration > 0) {
      setTimeout(() => {
        visible.value = false
        emit('close')
      }, props.duration)
    }
  } else {
    visible.value = false
  }
}, { immediate: true })

const typeConfig = {
  success: { bg: 'bg-mint/90', icon: CheckCircle, iconColor: 'text-green-600' },
  error: { bg: 'bg-red-100/90', icon: AlertCircle, iconColor: 'text-red-600' },
  warning: { bg: 'bg-butter/90', icon: AlertTriangle, iconColor: 'text-amber-600' },
  info: { bg: 'bg-sky/90', icon: Info, iconColor: 'text-blue-600' }
}
</script>

<template>
  <Teleport to="body">
    <Transition
      enter-active-class="transition-all duration-300 ease-out"
      enter-from-class="translate-x-full opacity-0"
      enter-to-class="translate-x-0 opacity-100"
      leave-active-class="transition-all duration-200 ease-in"
      leave-from-class="translate-x-0 opacity-100"
      leave-to-class="translate-x-full opacity-0"
    >
      <div
        v-if="visible"
        class="fixed top-4 right-4 z-50 flex items-center gap-3 px-4 py-3 rounded-md shadow-lg backdrop-blur-sm"
        :class="typeConfig[type].bg"
      >
        <component 
          :is="typeConfig[type].icon" 
          :class="typeConfig[type].iconColor"
          class="w-5 h-5 flex-shrink-0" 
        />
        <span class="text-body-sm text-gray-700">{{ message }}</span>
        <button 
          @click="visible = false; emit('close')"
          class="ml-2 text-gray-500 hover:text-gray-700 transition-colors"
        >
          <X class="w-4 h-4" />
        </button>
      </div>
    </Transition>
  </Teleport>
</template>
