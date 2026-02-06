<template>
  <section class="block">
    <div class="block-header">
      <h2>Аналитика</h2>
      <button @click="loadAll" class="secondary-btn" :disabled="isLoading">
        {{ isLoading ? 'Загрузка...' : 'Загрузить аналитику' }}
      </button>
    </div>

    <div class="date-controls">
      <div class="field">
        <label>С</label>
        <input v-model="fromDate" type="date" class="input" />
      </div>
      <div class="field">
        <label>По</label>
        <input v-model="toDate" type="date" class="input" />
      </div>
      <div class="hint">Выберите период и нажмите «Загрузить аналитику»</div>
    </div>

    <div v-if="!analytics && !isLoading" class="state empty">
      Нажмите «Загрузить аналитику», чтобы увидеть метрики.
    </div>

    <div v-if="analytics" class="analytics-grid">
      <div class="metric-card revenue">
        <div class="metric-icon">₽</div>
        <h3>Выручка за период</h3>
        <p class="metric-value">{{ formatCurrency(analytics.revenue30) }}</p>
      </div>

      <div class="metric-card avg-check">
        <div class="metric-icon">🧾</div>
        <h3>Средний чек</h3>
        <p class="metric-value">{{ formatCurrency(analytics.avgCheck) }}</p>
      </div>

      <div class="metric-card items">
        <div class="metric-icon">📦</div>
        <h3>Всего товаров продано</h3>
        <p class="metric-value">{{ analytics.totalItems }}</p>
      </div>

      <div class="metric-card top-products">
        <div class="metric-icon">🏆</div>
        <h3>Топ-5 товаров</h3>
        <ul class="top-list">
          <li v-for="(item, index) in analytics.topProducts" :key="index">
            {{ item.name }} — {{ formatCurrency(item.totalRevenue) }}
          </li>
        </ul>
      </div>

      <div class="metric-card platforms">
        <div class="metric-icon">📊</div>
        <h3>Выручка по платформам</h3>
        <ul class="platform-list">
          <li v-for="(revenue, platform) in analytics.revenueByPlatform" :key="platform">
            {{ platform }}: {{ formatCurrency(revenue) }}
          </li>
        </ul>
      </div>
    </div>

    <RevenueChart v-if="revenueData" :data="revenueData" />
    <PlatformRevenueChart v-if="analytics?.revenueByPlatform" :data="analytics.revenueByPlatform" />
    <PlatformRevenuePieChart v-if="analytics?.revenueByPlatform" :data="analytics.revenueByPlatform" />
    <ProductSalesTable v-if="productSummary" :rows="productSummary" />
  </section>
</template>

<script setup>
import { ref } from 'vue'
import RevenueChart from '@/components/RevenueChart.vue'
import PlatformRevenueChart from '@/components/PlatformRevenueChart.vue'
import PlatformRevenuePieChart from '@/components/PlatformRevenuePieChart.vue'
import ProductSalesTable from '@/components/ProductSalesTable.vue'
import { useToast } from '@/composables/useToast'
import {
  getAverageCheck,
  getDailyRevenue,
  getProductSalesSummary,
  getRevenueByPlatform,
  getRevenueTotal,
  getTopProducts,
  getTotalItems,
} from '@/services/analytics'

const analytics = ref(null)
const revenueData = ref(null)
const productSummary = ref(null)
const isLoading = ref(false)

const { showToast } = useToast()

const toInputDate = (date) => date.toISOString().slice(0, 10)

const today = new Date()
const defaultTo = toInputDate(today)
const defaultFrom = toInputDate(new Date(new Date().setDate(today.getDate() - 30)))

const fromDate = ref(defaultFrom)
const toDate = ref(defaultTo)

const toStartOfDayIso = (dateStr) => {
  const d = new Date(`${dateStr}T00:00:00.000Z`)
  return d.toISOString()
}

const toEndOfDayIso = (dateStr) => {
  const d = new Date(`${dateStr}T23:59:59.999Z`)
  return d.toISOString()
}

const loadAll = async () => {
  if (!fromDate.value || !toDate.value) {
    showToast('Выберите обе даты', 'error')
    return
  }

  if (fromDate.value > toDate.value) {
    showToast('Дата начала не может быть позже даты окончания', 'error')
    return
  }

  isLoading.value = true
  try {
    const from = toStartOfDayIso(fromDate.value)
    const to = toEndOfDayIso(toDate.value)

    const [revenue30, avgCheck, totalItems, daily, topProducts, revenueByPlatform, summary] =
      await Promise.all([
        getRevenueTotal(from, to).then((r) => r.data),
        getAverageCheck(from, to).then((r) => r.data),
        getTotalItems(from, to).then((r) => r.data),
        getDailyRevenue(from, to).then((r) => r.data),
        getTopProducts(5, from, to).then((r) => r.data),
        getRevenueByPlatform(from, to).then((r) => r.data),
        getProductSalesSummary(from, to).then((r) => r.data),
      ])

    analytics.value = {
      revenue30,
      avgCheck,
      totalItems,
      topProducts,
      revenueByPlatform,
    }
    revenueData.value = daily
    productSummary.value = summary
  } catch (err) {
    showToast('Ошибка загрузки аналитики', 'error')
  } finally {
    isLoading.value = false
  }
}

const formatCurrency = (value) => {
  const num = Number(value)
  if (Number.isNaN(num)) return '—'
  return new Intl.NumberFormat('ru-RU', {
    style: 'currency',
    currency: 'RUB',
    minimumFractionDigits: 2,
  }).format(num)
}
</script>
