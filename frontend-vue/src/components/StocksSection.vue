<template>
  <section class="block">
    <div class="block-header">
      <h2>Остатки</h2>
      <div class="header-actions">
        <select v-model="selectedShop" class="select">
          <option value="">Все магазины</option>
          <option v-for="shop in shops" :key="shop.id" :value="String(shop.id)">
            {{ shop.name }}
          </option>
        </select>
        <button class="secondary-btn" @click="loadStocks" :disabled="loading">
          {{ loading ? 'Загрузка...' : 'Обновить' }}
        </button>
        <button class="primary-btn" @click="downloadCsv" :disabled="isDownloading || loading">
          {{ isDownloading ? 'Скачивание...' : 'Скачать CSV' }}
        </button>
      </div>
    </div>

    <div v-if="loading" class="state">Загрузка остатков...</div>
    <div v-else-if="!rows.length" class="state empty">Нет данных по остаткам.</div>

    <div v-else class="table-wrapper">
      <table class="table">
        <thead>
          <tr>
            <th>
              <button class="sort-btn" type="button" data-sort="name" @click="setSort('name')">
                Товар
                <span class="sort-indicator">{{ sortLabel('name') }}</span>
              </button>
            </th>
            <th>
              <button class="sort-btn" type="button" data-sort="sku" @click="setSort('sku')">
                SKU
                <span class="sort-indicator">{{ sortLabel('sku') }}</span>
              </button>
            </th>
            <th class="num">
              <button class="sort-btn num" type="button" data-sort="totalQuantity" @click="setSort('totalQuantity')">
                Остаток
                <span class="sort-indicator">{{ sortLabel('totalQuantity') }}</span>
              </button>
            </th>
            <th class="num">
              <button class="sort-btn num" type="button" data-sort="daysUntilOos" @click="setSort('daysUntilOos')">
                Дней до отсутствия (OOS)
                <span class="sort-indicator">{{ sortLabel('daysUntilOos') }}</span>
              </button>
            </th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="row in sortedRows"
            :key="row.productId"
            :class="row.daysUntilOos !== null && row.daysUntilOos <= 7 ? 'low' : ''"
          >
            <td>{{ row.name }}</td>
            <td class="muted">{{ row.sku || '—' }}</td>
            <td class="num">{{ row.totalQuantity }}</td>
            <td class="num">{{ row.daysUntilOos === null ? '—' : row.daysUntilOos.toFixed(1) }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-if="rows.length" class="table-hint">
      OOS — Out of stock (нет в наличии). Показатель рассчитан на основе средней дневной продажи.
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { getShops } from '@/services/shops'
import { getStockSummary } from '@/services/stocks'
import { useToast } from '@/composables/useToast'

const rows = ref([])
const shops = ref([])
const selectedShop = ref('')
const loading = ref(false)
const isDownloading = ref(false)
const sortKey = ref('totalQuantity')
const sortDir = ref('desc')

const { showToast } = useToast()

const sortedRows = computed(() => {
  const data = [...rows.value]
  const key = sortKey.value
  const dir = sortDir.value === 'asc' ? 1 : -1

  const getValue = (row) => {
    if (key === 'daysUntilOos') {
      return row.daysUntilOos === null ? Number.POSITIVE_INFINITY : row.daysUntilOos
    }
    if (key === 'name') {
      return row.name || ''
    }
    if (key === 'sku') {
      return row.sku || ''
    }
    return row[key] ?? 0
  }

  return data.sort((a, b) => {
    const valueA = getValue(a)
    const valueB = getValue(b)

    if (typeof valueA === 'string' || typeof valueB === 'string') {
      return valueA.toString().localeCompare(valueB.toString(), 'ru', { sensitivity: 'base' }) * dir
    }

    return (valueA - valueB) * dir
  })
})

const setSort = (key) => {
  if (sortKey.value === key) {
    sortDir.value = sortDir.value === 'asc' ? 'desc' : 'asc'
    return
  }

  sortKey.value = key
  sortDir.value = key === 'name' || key === 'sku' ? 'asc' : 'desc'
}

const sortLabel = (key) => {
  if (sortKey.value !== key) return ''
  return sortDir.value === 'asc' ? '▲' : '▼'
}

const loadStocks = async () => {
  loading.value = true
  try {
    const shopId = selectedShop.value ? Number(selectedShop.value) : undefined
    const res = await getStockSummary(shopId)
    rows.value = res.data
  } catch (err) {
    showToast('Не удалось загрузить остатки', 'error')
  } finally {
    loading.value = false
  }
}

const downloadCsv = async () => {
  if (!rows.value.length) {
    showToast('Нет данных для выгрузки', 'error')
    return
  }

  isDownloading.value = true
  try {
    const base = import.meta.env.VITE_API_URL || 'http://localhost:8080'
    const shopParam = selectedShop.value ? `?shopId=${encodeURIComponent(selectedShop.value)}` : ''
    const url = `${base}/api/stocks/summary.csv${shopParam}`

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
    link.download = selectedShop.value
      ? `stock-summary-shop-${selectedShop.value}.csv`
      : 'stock-summary.csv'
    document.body.appendChild(link)
    link.click()
    link.remove()
    URL.revokeObjectURL(link.href)
  } catch (err) {
    showToast('Не удалось скачать CSV', 'error')
  } finally {
    isDownloading.value = false
  }
}

const loadShops = async () => {
  try {
    const res = await getShops()
    shops.value = res.data
  } catch (err) {
    showToast('Не удалось загрузить магазины', 'error')
  }
}

onMounted(async () => {
  await loadShops()
  await loadStocks()
})
</script>
