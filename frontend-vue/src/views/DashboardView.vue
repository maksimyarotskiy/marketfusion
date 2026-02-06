<template>
  <div class="dashboard">
    <header class="dashboard-header">
      <div>
        <h1>MarketFusion</h1>
        <p class="subtitle">Панель управления маркетплейсами</p>
      </div>
      <button @click="logout" class="logout-btn">Выйти</button>
    </header>

    <ShopSection @shop-selected="handleShopSelected" />

    <ProductsSection :shop="selectedShop" @close="selectedShop = null" />

    <AnalyticsSection />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import ShopSection from '@/components/ShopSection.vue'
import ProductsSection from '@/components/ProductsSection.vue'
import AnalyticsSection from '@/components/AnalyticsSection.vue'

const selectedShop = ref(null)

const logout = () => {
  localStorage.removeItem('accessToken')
  window.dispatchEvent(new CustomEvent('auth:logout'))
}

const handleShopSelected = (shop) => {
  selectedShop.value = shop
}
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

.logout-btn {
  background: var(--danger);
  color: white;
  border: none;
  padding: 10px 18px;
  border-radius: 10px;
  cursor: pointer;
  font-weight: 600;
  transition: background 0.2s;
}

.logout-btn:hover {
  background: #b91c1c;
}

@media (max-width: 768px) {
  .dashboard-header {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
