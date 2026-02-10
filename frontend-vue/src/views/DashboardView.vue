<template>
  <div class="dashboard">
    <header class="dashboard-header">
      <div>
        <h1>MarketFusion</h1>
        <p class="subtitle">Панель аналитики и управления</p>
      </div>

      <div class="profile" @click="toggleMenu">
        <div class="avatar">{{ initials }}</div>
        <div class="profile-info">
          <div class="profile-email">{{ userEmail || 'Пользователь' }}</div>
        </div>
        <button class="menu-btn" aria-label="Открыть меню">⋯</button>

        <div class="menu" v-if="menuOpen" @click.stop>
          <button class="menu-item" @click="toggleTheme">
            Тема: {{ themeLabel }}
          </button>
          <button class="menu-item danger" @click="logout">Выйти</button>
        </div>
      </div>
    </header>

    <ShopSection @shop-selected="handleShopSelected" />

    <ProductsSection :shop="selectedShop" @close="selectedShop = null" />

    <StocksSection />

    <AnalyticsSection />
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue'
import ShopSection from '@/components/ShopSection.vue'
import ProductsSection from '@/components/ProductsSection.vue'
import StocksSection from '@/components/StocksSection.vue'
import AnalyticsSection from '@/components/AnalyticsSection.vue'

const selectedShop = ref(null)
const menuOpen = ref(false)
const userEmail = ref('')
const theme = ref('light')

const logout = () => {
  localStorage.removeItem('accessToken')
  localStorage.removeItem('userEmail')
  window.dispatchEvent(new CustomEvent('auth:logout'))
}

const handleShopSelected = (shop) => {
  selectedShop.value = shop
}

const toggleMenu = (e) => {
  e.stopPropagation()
  menuOpen.value = !menuOpen.value
}

const closeMenu = () => {
  menuOpen.value = false
}

const applyTheme = (value) => {
  theme.value = value
  document.documentElement.setAttribute('data-theme', value)
  localStorage.setItem('theme', value)
}

const toggleTheme = (e) => {
  e.stopPropagation()
  const next = theme.value === 'dark' ? 'light' : 'dark'
  applyTheme(next)
}

const themeLabel = computed(() => (theme.value === 'dark' ? 'Тёмная' : 'Светлая'))

const initials = computed(() => {
  const email = userEmail.value || ''
  if (!email) return 'U'
  return email.slice(0, 2).toUpperCase()
})

onMounted(() => {
  userEmail.value = localStorage.getItem('userEmail') || ''
  const saved = localStorage.getItem('theme') || 'light'
  applyTheme(saved)
  window.addEventListener('click', closeMenu)
})

onUnmounted(() => {
  window.removeEventListener('click', closeMenu)
})
</script>

<style scoped>
.dashboard {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
  min-height: 100vh;
  background-color: var(--bg);
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  gap: 16px;
}

.dashboard-header h1 {
  font-size: 1.9rem;
  color: var(--text);
}

.subtitle {
  color: var(--muted);
  font-size: 0.95rem;
}

.profile {
  display: flex;
  align-items: center;
  gap: 12px;
  background: var(--card);
  border: 1px solid var(--border);
  padding: 10px 12px;
  border-radius: 14px;
  box-shadow: var(--shadow);
  position: relative;
  cursor: pointer;
}

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: var(--avatar-bg);
  color: var(--avatar-text);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 0.9rem;
}

.profile-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.profile-email {
  font-weight: 600;
  color: var(--text);
  font-size: 0.95rem;
}

.menu-btn {
  background: none;
  border: none;
  font-size: 1.2rem;
  cursor: pointer;
  color: var(--muted);
}

.menu {
  position: absolute;
  right: 10px;
  top: calc(100% + 8px);
  background: var(--menu-bg);
  border: 1px solid var(--border);
  border-radius: 10px;
  box-shadow: var(--shadow);
  padding: 6px;
  z-index: 10;
  min-width: 160px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.menu-item {
  width: 100%;
  text-align: left;
  border: none;
  background: none;
  padding: 8px 10px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  color: var(--text);
}

.menu-item:hover {
  background: rgba(148, 163, 184, 0.18);
}

.menu-item.danger {
  color: var(--danger);
}

.menu-item.danger:hover {
  background: rgba(248, 113, 113, 0.16);
}

@media (max-width: 768px) {
  .dashboard-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .profile {
    width: 100%;
    justify-content: space-between;
  }
}
</style>
