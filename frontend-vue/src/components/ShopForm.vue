<template>
  <div class="shop-form-card">
    <h3>{{ isNew ? 'Создать магазин' : 'Редактировать магазин' }}</h3>
    <form @submit.prevent="handleSubmit">
      <div class="form-group">
        <label for="name">Название *</label>
        <input
          id="name"
          v-model="formData.name"
          type="text"
          placeholder="Например: Мой магазин на WB"
          required
        />
      </div>

      <div class="form-group">
        <label for="platform">Платформа *</label>
        <select id="platform" v-model="formData.platform" required>
          <option value="">Выберите...</option>
          <option value="WB">Wildberries</option>
          <option value="OZON">Ozon</option>
          <option value="YANDEX">Яндекс Маркет</option>
          <option value="OTHER">Другое</option>
        </select>
      </div>

      <div class="form-group">
        <label for="apiKey">API ключ</label>
        <input
          id="apiKey"
          v-model="formData.apiKey"
          type="password"
          placeholder="Введите API ключ"
        />
      </div>

      <div class="form-actions">
        <button type="submit" :disabled="loading">
          {{ loading ? 'Сохранение...' : isNew ? 'Создать' : 'Сохранить' }}
        </button>
        <button type="button" @click="$emit('cancel')" v-if="!isNew" :disabled="loading">
          Отмена
        </button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'

const props = defineProps({
  modelValue: {
    type: Object,
    default: () => ({ name: '', platform: '', apiKey: '' }),
  },
  loading: {
    type: Boolean,
    default: false,
  },
})

const emit = defineEmits(['update:modelValue', 'submit', 'cancel'])

const formData = ref({ ...props.modelValue })

watch(
  () => props.modelValue,
  (newVal) => {
    formData.value = { ...newVal }
  },
)

const isNew = computed(() => !formData.value.id)

const handleSubmit = () => {
  if (!formData.value.name || !formData.value.platform) return
  emit('submit', formData.value)
}

watch(formData, (val) => {
  emit('update:modelValue', val)
})
</script>

<style scoped>
.shop-form-card {
  background: var(--card);
  border-radius: 12px;
  padding: 20px;
  border: 1px solid var(--border);
  box-shadow: var(--shadow);
  max-width: 100%;
  margin: 12px 0 20px;
  box-sizing: border-box;
}

h3 {
  margin-top: 0;
  color: var(--text);
  font-size: 1.2rem;
}

.form-group {
  margin-bottom: 16px;
}

label {
  display: block;
  margin-bottom: 6px;
  font-weight: 600;
  color: #334155;
}

input,
select {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid var(--border);
  border-radius: 8px;
  font-size: 1rem;
  transition: border-color 0.2s, box-shadow 0.2s;
  box-sizing: border-box;
}

input:focus,
select:focus {
  outline: none;
  border-color: var(--primary);
  box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.12);
}

.form-actions {
  display: flex;
  gap: 12px;
  margin-top: 20px;
  flex-wrap: wrap;
}

button {
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  cursor: pointer;
  transition: opacity 0.2s;
}

button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

button[type='submit'] {
  background: var(--primary);
  color: white;
}

button[type='submit']:hover:not(:disabled) {
  background: var(--primary-hover);
}

button[type='button'] {
  background: #e2e8f0;
  color: #1e293b;
}
</style>
