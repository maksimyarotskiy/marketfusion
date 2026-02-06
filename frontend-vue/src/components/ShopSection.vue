<template>
  <section class="block">
    <div class="block-header">
      <h2>Магазины</h2>
      <button v-if="!editingShop" @click="startCreate" class="primary-btn">
        + Добавить магазин
      </button>
    </div>

    <ShopForm
      v-if="editingShop"
      :key="editingShopKey"
      v-model="editingShop"
      :loading="isSaving"
      @submit="saveShop"
      @cancel="cancelEditing"
    />

    <div v-if="isLoadingShops" class="state">Загрузка магазинов...</div>
    <div v-else-if="!shops.length" class="state empty">
      Пока нет магазинов. Добавьте первый магазин, чтобы начать.
    </div>
    <div v-else class="shops-list">
      <div class="shop-card" v-for="shop in shops" :key="shop.id">
        <div>
          <h3>{{ shop.name }}</h3>
          <p><strong>Платформа:</strong> {{ shop.platform }}</p>
        </div>
        <div class="shop-actions">
          <button @click="() => selectShop(shop)" class="secondary-btn small">Товары</button>
          <button @click="() => startEdit(shop)" class="secondary-btn small">Редактировать</button>
          <button @click="() => promptDelete(shop)" class="danger-btn small">Удалить</button>
        </div>
      </div>
    </div>

    <ConfirmDialog
      :open="confirmOpen"
      title="Удаление магазина"
      :message="confirmMessage"
      @confirm="confirmDelete"
      @cancel="cancelDelete"
    />
  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import ShopForm from '@/components/ShopForm.vue'
import ConfirmDialog from '@/components/ConfirmDialog.vue'
import { useToast } from '@/composables/useToast'
import { createShop, deleteShop as removeShop, getShops, updateShop } from '@/services/shops'

const emit = defineEmits(['shop-selected'])

const shops = ref([])
const editingShop = ref(null)
const editingShopKey = ref(0)
const isSaving = ref(false)
const isLoadingShops = ref(false)

const confirmOpen = ref(false)
const shopToDelete = ref(null)
const confirmMessage = ref('')

const { showToast } = useToast()

const loadShops = async () => {
  isLoadingShops.value = true
  try {
    const res = await getShops()
    shops.value = res.data
  } catch (err) {
    showToast('Не удалось загрузить магазины', 'error')
  } finally {
    isLoadingShops.value = false
  }
}

const cancelEditing = () => {
  editingShop.value = null
  editingShopKey.value++
}

const startCreate = () => {
  editingShop.value = { name: '', platform: '', apiKey: '' }
  editingShopKey.value++
}

const startEdit = (shop) => {
  editingShop.value = { ...shop }
  editingShopKey.value++
}

const saveShop = async (shopData) => {
  isSaving.value = true
  try {
    if (shopData.id) {
      await updateShop(shopData.id, shopData)
      showToast('Магазин обновлен', 'success')
    } else {
      await createShop(shopData)
      showToast('Магазин создан', 'success')
    }
    cancelEditing()
    await loadShops()
  } catch (err) {
    showToast('Ошибка сохранения магазина', 'error')
  } finally {
    isSaving.value = false
  }
}

const promptDelete = (shop) => {
  shopToDelete.value = shop
  confirmMessage.value = `Удалить магазин «${shop.name}»? Это действие нельзя отменить.`
  confirmOpen.value = true
}

const cancelDelete = () => {
  confirmOpen.value = false
  shopToDelete.value = null
}

const confirmDelete = async () => {
  if (!shopToDelete.value) return
  try {
    await removeShop(shopToDelete.value.id)
    showToast('Магазин удален', 'success')
    cancelEditing()
    await loadShops()
  } catch (err) {
    showToast('Ошибка удаления магазина', 'error')
  } finally {
    cancelDelete()
  }
}

const selectShop = (shop) => {
  emit('shop-selected', shop)
}

onMounted(() => {
  loadShops()
})
</script>
