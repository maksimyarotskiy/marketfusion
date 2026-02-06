<template>
  <div class="toast" :class="toastState.type" v-if="toastState.visible" @click="hideToast">
    <span class="toast-text">{{ toastState.message }}</span>
  </div>
</template>

<script setup>
import { useToast } from '@/composables/useToast'

const { toastState, hideToast } = useToast()
</script>

<style scoped>
.toast {
  position: fixed;
  right: 20px;
  bottom: 20px;
  padding: 12px 16px;
  border-radius: 10px;
  color: white;
  font-weight: 600;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.15);
  z-index: 1000;
  max-width: 360px;
  cursor: pointer;
  animation: slide-in 0.2s ease-out;
}

.toast.info {
  background: #334155;
}

.toast.success {
  background: #16a34a;
}

.toast.error {
  background: #dc2626;
}

.toast-text {
  display: block;
  line-height: 1.35;
}

@keyframes slide-in {
  from {
    transform: translateY(10px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}
</style>
