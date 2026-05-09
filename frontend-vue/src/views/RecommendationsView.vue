<template>
  <div class="recommendations-page">
    <header class="page-header block">
      <div>
        <h1>Рекомендации по метрикам</h1>
        <p class="subtitle">
          Страница формирует советы на основе выручки, структуры продаж и динамики за выбранный период.
        </p>
      </div>

      <div class="header-actions">
        <button class="secondary-btn" @click="goBack">К аналитике</button>
        <button class="primary-btn" @click="loadRecommendations" :disabled="isLoading">
          {{ isLoading ? 'Загрузка...' : 'Обновить советы' }}
        </button>
      </div>
    </header>

    <section class="block">
      <div class="date-controls">
        <div class="field">
          <label>С</label>
          <input v-model="fromDate" type="date" class="input" />
        </div>
        <div class="field">
          <label>По</label>
          <input v-model="toDate" type="date" class="input" />
        </div>
        <div class="hint">Выберите период, затем обновите подборку рекомендаций.</div>
      </div>
    </section>

    <div v-if="!analysis && !isLoading" class="state empty">
      Нажмите «Обновить советы», чтобы получить рекомендации по текущим метрикам.
    </div>

    <section v-if="analysis" class="block">
      <div class="block-header">
        <h2>Краткая сводка</h2>
      </div>

      <div class="highlights-grid">
        <div v-for="item in analysis.highlights" :key="item.label" class="highlight-card">
          <div class="highlight-label">{{ item.label }}</div>
          <div class="highlight-value">{{ formatHighlight(item) }}</div>
        </div>
      </div>
    </section>

    <section v-if="analysis" class="block">
      <div class="block-header">
        <h2>Пользовательские советы</h2>
      </div>

      <div class="recommendation-list">
        <article
          v-for="(item, index) in analysis.recommendations"
          :key="`${item.title}-${index}`"
          class="recommendation-card"
          :class="item.level"
        >
          <div class="recommendation-top">
            <h3>{{ item.title }}</h3>
            <span class="level-badge">{{ levelLabel(item.level) }}</span>
          </div>
          <p class="recommendation-text">{{ item.text }}</p>
          <p class="recommendation-basis">
            <strong>Основание:</strong> {{ item.basis }}
          </p>
        </article>
      </div>
    </section>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
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
import { analyzeMetrics } from '@/utils/recommendations'

const router = useRouter()
const route = useRoute()
const { showToast } = useToast()

const toInputDate = (date) => date.toISOString().slice(0, 10)
const today = new Date()
const defaultTo = toInputDate(today)
const defaultFrom = toInputDate(new Date(new Date().setDate(today.getDate() - 30)))

const fromDate = ref(typeof route.query.from === 'string' ? route.query.from : defaultFrom)
const toDate = ref(typeof route.query.to === 'string' ? route.query.to : defaultTo)
const isLoading = ref(false)
const analysis = ref(null)

const toStartOfDayIso = (dateStr) => new Date(`${dateStr}T00:00:00.000Z`).toISOString()
const toEndOfDayIso = (dateStr) => new Date(`${dateStr}T23:59:59.999Z`).toISOString()

const loadRecommendations = async () => {
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

    const [revenueTotal, avgCheck, totalItems, dailyRevenue, dailyQuantity, topProducts, revenueByPlatform, productSummary] =
      await Promise.all([
        getRevenueTotal(from, to).then((response) => response.data),
        getAverageCheck(from, to).then((response) => response.data),
        getTotalItems(from, to).then((response) => response.data),
        getDailyRevenue(from, to).then((response) => response.data),
        getDailyQuantity(from, to).then((response) => response.data),
        getTopProducts(5, from, to).then((response) => response.data),
        getRevenueByPlatform(from, to).then((response) => response.data),
        getProductSalesSummary(from, to).then((response) => response.data),
      ])

    analysis.value = analyzeMetrics({
      revenueTotal,
      avgCheck,
      totalItems,
      dailyRevenue,
      dailyQuantity,
      topProducts,
      revenueByPlatform,
      productSummary,
    })

    router.replace({
      name: 'recommendations',
      query: {
        from: fromDate.value,
        to: toDate.value,
      },
    })
  } catch (error) {
    showToast('Не удалось получить рекомендации', 'error')
  } finally {
    isLoading.value = false
  }
}

const goBack = () => {
  router.push({
    name: 'dashboard',
    query: {
      from: fromDate.value,
      to: toDate.value,
    },
  })
}

const levelLabel = (level) => {
  switch (level) {
    case 'danger':
      return 'Критично'
    case 'warning':
      return 'Внимание'
    case 'success':
      return 'Позитивно'
    default:
      return 'Наблюдение'
  }
}

const formatCurrency = (value) =>
  new Intl.NumberFormat('ru-RU', {
    style: 'currency',
    currency: 'RUB',
    minimumFractionDigits: 2,
  }).format(Number(value) || 0)

const formatHighlight = (item) => {
  if (item.type === 'currency') return formatCurrency(item.value)
  return item.value
}

onMounted(() => {
  loadRecommendations()
})
</script>

<style scoped>
.recommendations-page {
  padding: 24px;
  max-width: 1200px;
  margin: 0 auto;
  min-height: 100vh;
  background-color: var(--bg);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
}

.page-header h1 {
  font-size: 1.8rem;
  color: var(--text);
}

.subtitle {
  margin-top: 8px;
  color: var(--muted);
  max-width: 700px;
  line-height: 1.5;
}

.highlights-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 14px;
}

.highlight-card {
  border: 1px solid var(--border);
  background: var(--surface);
  border-radius: 14px;
  padding: 16px;
}

.highlight-label {
  color: var(--muted);
  font-size: 0.88rem;
  margin-bottom: 8px;
}

.highlight-value {
  font-size: 1.15rem;
  font-weight: 700;
  color: var(--text);
}

.recommendation-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.recommendation-card {
  border: 1px solid var(--border);
  border-left-width: 6px;
  background: var(--surface);
  border-radius: 14px;
  padding: 16px 18px;
}

.recommendation-card.info {
  border-left-color: #0ea5e9;
}

.recommendation-card.success {
  border-left-color: #22c55e;
}

.recommendation-card.warning {
  border-left-color: #f59e0b;
}

.recommendation-card.danger {
  border-left-color: #ef4444;
}

.recommendation-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  margin-bottom: 10px;
}

.recommendation-top h3 {
  font-size: 1.05rem;
  color: var(--text);
}

.level-badge {
  padding: 6px 10px;
  border-radius: 999px;
  background: var(--surface-2);
  color: var(--muted);
  font-size: 0.82rem;
  font-weight: 700;
  white-space: nowrap;
}

.recommendation-text {
  color: var(--text);
  line-height: 1.55;
  margin-bottom: 10px;
}

.recommendation-basis {
  color: var(--muted);
  line-height: 1.5;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
  }

  .recommendation-top {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
