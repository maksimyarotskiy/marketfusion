<script setup>
import { onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import ToastMessage from '@/components/ToastMessage.vue'
import { useToast } from '@/composables/useToast'

const router = useRouter()
const { showToast } = useToast()

const handleLogout = () => {
  showToast('Сессия истекла. Войдите снова.', 'info')
  router.push({ name: 'login' })
}

onMounted(() => {
  window.addEventListener('auth:logout', handleLogout)
})

onUnmounted(() => {
  window.removeEventListener('auth:logout', handleLogout)
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
  color: #0f172a;
}
</style>
