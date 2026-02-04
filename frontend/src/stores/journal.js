import { defineStore } from 'pinia'
import { ref } from 'vue'

/**
 * Journal Store
 * 日记状态管理
 */
export const useJournalStore = defineStore('journal', () => {
  // State
  const journals = ref([])
  const currentJournal = ref(null)
  const loading = ref(false)
  const error = ref(null)

  // Stats
  const stats = ref({
    total: 0,
    thisMonth: 0,
    topMood: null
  })

  // Actions
  function setJournals(data) {
    journals.value = data
  }

  function setCurrentJournal(journal) {
    currentJournal.value = journal
  }

  function setStats(data) {
    stats.value = data
  }

  function setLoading(value) {
    loading.value = value
  }

  function setError(err) {
    error.value = err
  }

  function clearError() {
    error.value = null
  }

  return {
    journals,
    currentJournal,
    loading,
    error,
    stats,
    setJournals,
    setCurrentJournal,
    setStats,
    setLoading,
    setError,
    clearError
  }
})
