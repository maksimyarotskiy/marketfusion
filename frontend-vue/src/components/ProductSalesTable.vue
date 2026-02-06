<template>
  <div class="table-block" v-if="rows.length">
    <div class="table-header">
      <h3>Продажи по товарам</h3>
      <div class="table-controls">
        <input v-model.trim="search" class="input" placeholder="Поиск по товару или SKU" />
        <select v-model="sortKey" class="select">
          <option value="qty-desc">Сортировка: Продано ↓</option>
          <option value="qty-asc">Сортировка: Продано ↑</option>
          <option value="rev-desc">Сортировка: Выручка ↓</option>
          <option value="rev-asc">Сортировка: Выручка ↑</option>
          <option value="name-asc">Сортировка: Название A→Z</option>
          <option value="name-desc">Сортировка: Название Z→A</option>
        </select>
      </div>
    </div>

    <div class="table-wrapper">
      <table class="table">
        <thead>
          <tr>
            <th>Товар</th>
            <th>SKU</th>
            <th class="num">Продано (шт)</th>
            <th class="num">Выручка</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="row in pagedRows" :key="row.productId">
            <td>{{ row.name }}</td>
            <td class="muted">{{ row.sku || '—' }}</td>
            <td class="num">{{ row.totalQuantity }}</td>
            <td class="num">{{ formatCurrency(row.totalRevenue) }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div class="pagination" v-if="totalPages > 1">
      <button class="secondary-btn" :disabled="page === 1" @click="page--">←</button>
      <span class="page-info">Страница {{ page }} из {{ totalPages }}</span>
      <button class="secondary-btn" :disabled="page === totalPages" @click="page++">→</button>
      <select v-model="pageSize" class="select page-size">
        <option :value="10">10 / стр</option>
        <option :value="20">20 / стр</option>
        <option :value="50">50 / стр</option>
      </select>
    </div>
  </div>

  <div v-else class="state empty">Нет данных по товарам за выбранный период.</div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'

const props = defineProps({
  rows: {
    type: Array,
    default: () => [],
  },
})

const search = ref('')
const sortKey = ref('qty-desc')
const page = ref(1)
const pageSize = ref(10)

const filteredRows = computed(() => {
  const q = search.value.toLowerCase()
  const base = props.rows.filter((r) => {
    const name = String(r.name || '').toLowerCase()
    const sku = String(r.sku || '').toLowerCase()
    return !q || name.includes(q) || sku.includes(q)
  })

  const sorted = [...base]
  switch (sortKey.value) {
    case 'qty-desc':
      sorted.sort((a, b) => (b.totalQuantity || 0) - (a.totalQuantity || 0))
      break
    case 'qty-asc':
      sorted.sort((a, b) => (a.totalQuantity || 0) - (b.totalQuantity || 0))
      break
    case 'rev-desc':
      sorted.sort((a, b) => Number(b.totalRevenue || 0) - Number(a.totalRevenue || 0))
      break
    case 'rev-asc':
      sorted.sort((a, b) => Number(a.totalRevenue || 0) - Number(b.totalRevenue || 0))
      break
    case 'name-desc':
      sorted.sort((a, b) => String(b.name || '').localeCompare(String(a.name || ''), 'ru'))
      break
    default:
      sorted.sort((a, b) => String(a.name || '').localeCompare(String(b.name || ''), 'ru'))
  }

  return sorted
})

const totalPages = computed(() => Math.max(1, Math.ceil(filteredRows.value.length / pageSize.value)))

const pagedRows = computed(() => {
  const start = (page.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredRows.value.slice(start, end)
})

const formatCurrency = (value) => {
  const num = Number(value)
  if (Number.isNaN(num)) return '—'
  return new Intl.NumberFormat('ru-RU', {
    style: 'currency',
    currency: 'RUB',
    minimumFractionDigits: 2,
  }).format(num)
}

watch([search, sortKey, pageSize, () => props.rows], () => {
  page.value = 1
})
</script>
