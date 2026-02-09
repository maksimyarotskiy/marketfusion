<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import ToastMessage from '@/components/ToastMessage.vue'
import { useToast } from '@/composables/useToast'

const router = useRouter()
const { showToast } = useToast()

const handleLogout = () => {
  showToast('Сессия истекла. Войдите снова.', 'info')
  router.push({ name: 'login' })
}

const applyTheme = (theme) => {
  if (!theme) return
  document.documentElement.setAttribute('data-theme', theme)
}

onMounted(() => {
  window.addEventListener('auth:logout', handleLogout)
  const saved = localStorage.getItem('theme')
  if (saved) {
    applyTheme(saved)
  }
})
</script>

<template>
  <div id="app">
    <router-view />
    <ToastMessage />
  </div>
</template>

<style>
#app {
  min-height: 100vh;
  font-family: 'Manrope', system-ui, -apple-system, Segoe UI, Roboto, sans-serif;
  color: var(--text);
}
</style>
