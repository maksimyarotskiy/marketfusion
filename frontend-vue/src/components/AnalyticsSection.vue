<template>
  <section class="block">
    <div class="block-header">
      <h2>Аналитика</h2>
      <div class="header-actions">
        <button @click="openRecommendations" class="secondary-btn" :disabled="isLoading">
          Советы
        </button>
        <button @click="loadAll" class="secondary-btn" :disabled="isLoading">
          {{ isLoading ? 'Загрузка...' : 'Загрузить аналитику' }}
        </button>
        <button @click="downloadCsv" class="primary-btn" :disabled="isLoading">
          Скачать CSV
        </button>
      </div>
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
    <CorrelationScatterChart
      v-if="revenueData && quantityData"
      :revenue-by-day="revenueData"
      :quantity-by-day="quantityData"
    />
    <RegressionChart
      v-if="revenueData && quantityData"
      :revenue-by-day="revenueData"
      :quantity-by-day="quantityData"
    />
    <PlatformRevenueChart v-if="analytics?.revenueByPlatform" :data="analytics.revenueByPlatform" />
    <PlatformRevenuePieChart v-if="analytics?.revenueByPlatform" :data="analytics.revenueByPlatform" />
    <ProductSalesTable v-if="productSummary" :rows="productSummary" />
  </section>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import RevenueChart from '@/components/RevenueChart.vue'
import PlatformRevenueChart from '@/components/PlatformRevenueChart.vue'
import PlatformRevenuePieChart from '@/components/PlatformRevenuePieChart.vue'
import ProductSalesTable from '@/components/ProductSalesTable.vue'
import CorrelationScatterChart from '@/components/CorrelationScatterChart.vue'
import RegressionChart from '@/components/RegressionChart.vue'
import { useToast } from '@/composables/useToast'
import {
  getAverageCheck,
  getDailyQuantity,
  getDailyRevenue,
  getProductSalesSummary,
  getRevenueByPlatform,
  getRevenueTotal,
  getTopProducts,
  getTotalItems,
} from '@/services/analytics'

const analytics = ref(null)
const revenueData = ref(null)
const quantityData = ref(null)
const productSummary = ref(null)
const isLoading = ref(false)

const { showToast } = useToast()
const router = useRouter()

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

    const [revenue30, avgCheck, totalItems, daily, dailyQty, topProducts, revenueByPlatform, summary] =
      await Promise.all([
        getRevenueTotal(from, to).then((r) => r.data),
        getAverageCheck(from, to).then((r) => r.data),
        getTotalItems(from, to).then((r) => r.data),
        getDailyRevenue(from, to).then((r) => r.data),
        getDailyQuantity(from, to).then((r) => r.data),
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
    quantityData.value = dailyQty
    productSummary.value = summary
  } catch (err) {
    showToast('Ошибка загрузки аналитики', 'error')
  } finally {
    isLoading.value = false
  }
}

const downloadCsv = async () => {
  if (!fromDate.value || !toDate.value) {
    showToast('Выберите обе даты', 'error')
    return
  }

  if (fromDate.value > toDate.value) {
    showToast('Дата начала не может быть позже даты окончания', 'error')
    return
  }

  const from = toStartOfDayIso(fromDate.value)
  const to = toEndOfDayIso(toDate.value)
  const base = import.meta.env.VITE_API_URL || 'http://localhost:8080'
  const url = `${base}/api/analytics/products-summary.csv?from=${encodeURIComponent(from)}&to=${encodeURIComponent(to)}`

  try {
    const token = localStorage.getItem('accessToken')
    const res = await fetch(url, {
      headers: token ? { Authorization: `Bearer ${token}` } : {},
    })

    if (!res.ok) {
      throw new Error(`HTTP ${res.status}`)
    }

    const blob = await res.blob()
    const link = document.createElement('a')
    link.href = URL.createObjectURL(blob)
    link.download = `product-sales-${fromDate.value}_${toDate.value}.csv`
    document.body.appendChild(link)
    link.click()
    link.remove()
    URL.revokeObjectURL(link.href)
  } catch (err) {
    showToast('Не удалось скачать CSV', 'error')
  }
}

const openRecommendations = () => {
  router.push({
    name: 'recommendations',
    query: {
      from: fromDate.value,
      to: toDate.value,
    },
  })
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
