<template>
  <section class="block" v-if="visible">
    <div class="block-header">
      <h2>Товары ({{ shopName || 'Магазин' }})</h2>
      <button @click="close" class="secondary-btn">← Назад к магазинам</button>
    </div>

    <div class="products-controls" v-if="products.length">
      <input
        v-model.trim="searchQuery"
        class="input"
        type="text"
        placeholder="Поиск по названию или SKU"
      />
      <select v-model="sortKey" class="select">
        <option value="name-asc">Сортировка: Название A→Z</option>
        <option value="name-desc">Сортировка: Название Z→A</option>
        <option value="price-asc">Сортировка: Цена ↑</option>
        <option value="price-desc">Сортировка: Цена ↓</option>
      </select>
    </div>

    <div v-if="isLoading" class="state">Загрузка товаров...</div>
    <div v-else-if="!products.length" class="state empty">Товары не найдены.</div>
    <div v-else-if="!filteredProducts.length" class="state empty">
      Ничего не найдено по запросу.
    </div>

    <div v-else>
      <div class="products-list">
        <div class="product-card" v-for="product in pagedProducts" :key="product.id">
          <strong>{{ product.name }}</strong>
          <span>— {{ product.sku }} — {{ formatCurrency(product.price) }}</span>
        </div>
      </div>

      <div class="pagination" v-if="totalPages > 1">
        <button class="secondary-btn" :disabled="page === 1" @click="page--">
          ←
        </button>
        <span class="page-info">Страница {{ page }} из {{ totalPages }}</span>
        <button class="secondary-btn" :disabled="page === totalPages" @click="page++">
          →
        </button>
        <select v-model="pageSize" class="select page-size">
          <option :value="10">10 / стр</option>
          <option :value="20">20 / стр</option>
          <option :value="50">50 / стр</option>
        </select>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { useToast } from '@/composables/useToast'
import { getProductsByShop } from '@/services/products'

const props = defineProps({
  shop: { type: Object, default: null },
})

const emit = defineEmits(['close'])

const products = ref([])
const isLoading = ref(false)
const visible = ref(false)
const shopName = ref('')
const searchQuery = ref('')
const sortKey = ref('name-asc')
const page = ref(1)
const pageSize = ref(10)

const { showToast } = useToast()

const loadProducts = async (shopId) => {
  isLoading.value = true
  try {
    const res = await getProductsByShop(shopId)
    products.value = res.data
    searchQuery.value = ''
    sortKey.value = 'name-asc'
    page.value = 1
  } catch (err) {
    showToast('Ошибка загрузки товаров', 'error')
  } finally {
    isLoading.value = false
  }
}

const close = () => {
  products.value = []
  visible.value = false
  emit('close')
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

const filteredProducts = computed(() => {
  const q = searchQuery.value.toLowerCase()
  const base = products.value.filter((p) => {
    const name = String(p.name || '').toLowerCase()
    const sku = String(p.sku || '').toLowerCase()
    return !q || name.includes(q) || sku.includes(q)
  })

  const sorted = [...base]
  switch (sortKey.value) {
    case 'name-desc':
      sorted.sort((a, b) => String(b.name || '').localeCompare(String(a.name || ''), 'ru'))
      break
    case 'price-asc':
      sorted.sort((a, b) => Number(a.price || 0) - Number(b.price || 0))
      break
    case 'price-desc':
      sorted.sort((a, b) => Number(b.price || 0) - Number(a.price || 0))
      break
    default:
      sorted.sort((a, b) => String(a.name || '').localeCompare(String(b.name || ''), 'ru'))
  }

  return sorted
})

const totalPages = computed(() => Math.max(1, Math.ceil(filteredProducts.value.length / pageSize.value)))

const pagedProducts = computed(() => {
  const start = (page.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredProducts.value.slice(start, end)
})

watch([searchQuery, sortKey, pageSize], () => {
  page.value = 1
})

watch(
  () => props.shop,
  (newShop) => {
    if (!newShop) {
      visible.value = false
      return
    }
    shopName.value = newShop.name || ''
    visible.value = true
    loadProducts(newShop.id)
  },
  { immediate: true },
)
</script>
