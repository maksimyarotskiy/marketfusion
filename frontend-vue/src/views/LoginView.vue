<template>
  <div class="login-view">
    <AuthForm @authenticated="handleAuth" />
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import AuthForm from '@/components/AuthForm.vue'

const router = useRouter()

const handleAuth = () => {
  router.push({ name: 'dashboard' })
}
</script>

<style scoped>
.login-view {
  min-height: 100vh;
}
</style>
