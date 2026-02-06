<template>
  <div class="auth-container">
    <div class="auth-card">
      <h2>{{ isLogin ? 'Вход' : 'Регистрация' }}</h2>
      <p class="subtitle">Войдите, чтобы управлять магазинами и аналитикой</p>
      <form @submit.prevent="handleSubmit">
        <div class="form-group">
          <label for="email">Email</label>
          <input id="email" v-model.trim="email" type="email" placeholder="you@mail.com" required />
        </div>
        <div class="form-group">
          <label for="password">Пароль</label>
          <input
            id="password"
            v-model="password"
            type="password"
            placeholder="Минимум 6 символов"
            required
            minlength="6"
          />
        </div>
        <button type="submit" class="btn-primary" :disabled="loading">
          {{ loading ? 'Проверка...' : isLogin ? 'Войти' : 'Зарегистрироваться' }}
        </button>
      </form>
      <button @click="toggleMode" class="btn-link" :disabled="loading">
        {{ isLogin ? 'Нет аккаунта? Зарегистрируйтесь' : 'Уже есть аккаунт? Войдите' }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useToast } from '@/composables/useToast'
import { login, register } from '@/services/auth'

const props = defineProps({
  isLogin: { type: Boolean, default: true },
})

const emit = defineEmits(['authenticated'])

const email = ref('')
const password = ref('')
const isLogin = ref(props.isLogin)
const loading = ref(false)
const { showToast } = useToast()

const handleSubmit = async () => {
  if (loading.value) return
  loading.value = true
  try {
    const request = isLogin.value ? login : register
    const res = await request({ email: email.value, password: password.value })
    localStorage.setItem('accessToken', res.data.accessToken)
    emit('authenticated')
    showToast('Вы успешно вошли.', 'success')
  } catch (err) {
    const message = err.response?.data?.message || err.message || 'Ошибка авторизации'
    showToast(message, 'error')
  } finally {
    loading.value = false
  }
}

const toggleMode = () => {
  if (loading.value) return
  isLogin.value = !isLogin.value
}
</script>

<style scoped>
.auth-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: radial-gradient(circle at 20% 20%, #e0e7ff 0%, transparent 50%),
    radial-gradient(circle at 80% 0%, #dbeafe 0%, transparent 45%),
    #f8fafc;
  padding: 20px;
  margin: 0;
  width: 100%;
}

.auth-card {
  background: var(--card);
  padding: 32px;
  border-radius: 18px;
  box-shadow: var(--shadow);
  width: 100%;
  max-width: 420px;
  box-sizing: border-box;
}

.auth-card h2 {
  text-align: center;
  margin-bottom: 6px;
  color: var(--text);
  font-size: 1.6rem;
}

.subtitle {
  text-align: center;
  color: var(--muted);
  margin-bottom: 24px;
  font-size: 0.95rem;
}

.form-group {
  margin-bottom: 16px;
}

label {
  display: block;
  margin-bottom: 6px;
  font-weight: 600;
  color: #1e293b;
}

input {
  width: 100%;
  padding: 12px 14px;
  border: 1px solid var(--border);
  border-radius: 10px;
  font-size: 1rem;
  transition: border-color 0.2s, box-shadow 0.2s;
  box-sizing: border-box;
}

input:focus {
  outline: none;
  border-color: var(--primary);
  box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.15);
}

.btn-primary {
  width: 100%;
  padding: 12px;
  background: var(--primary);
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s, transform 0.2s;
}

.btn-primary:hover:not(:disabled) {
  background: var(--primary-hover);
  transform: translateY(-1px);
}

.btn-primary:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.btn-link {
  width: 100%;
  padding: 12px;
  background: none;
  color: var(--primary);
  border: none;
  border-radius: 10px;
  font-size: 0.95rem;
  cursor: pointer;
  margin-top: 12px;
  text-decoration: underline;
}

.btn-link:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
