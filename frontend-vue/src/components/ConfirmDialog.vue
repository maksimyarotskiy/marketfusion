<template>
  <div class="overlay" v-if="open">
    <div class="dialog">
      <h3>{{ title }}</h3>
      <p>{{ message }}</p>
      <div class="actions">
        <button class="secondary-btn" @click="$emit('cancel')">Отмена</button>
        <button class="danger-btn" @click="$emit('confirm')">Удалить</button>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  open: { type: Boolean, default: false },
  title: { type: String, default: 'Подтверждение' },
  message: { type: String, default: 'Вы уверены?' },
})
</script>

<style scoped>
.overlay {
  position: fixed;
  inset: 0;
  background: rgba(15, 23, 42, 0.35);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1100;
  padding: 20px;
}

.dialog {
  background: var(--card);
  border-radius: 14px;
  padding: 20px;
  width: 100%;
  max-width: 420px;
  box-shadow: var(--shadow);
  border: 1px solid var(--border);
}

.dialog h3 {
  margin-bottom: 8px;
  color: var(--text);
}

.dialog p {
  color: var(--muted);
}

.actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}
</style>
