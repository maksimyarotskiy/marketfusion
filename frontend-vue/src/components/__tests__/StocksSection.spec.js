import { mount, flushPromises } from '@vue/test-utils'
import { describe, it, expect, vi, beforeEach } from 'vitest'
import StocksSection from '@/components/StocksSection.vue'

const mockGetShops = vi.fn()
const mockGetStockSummary = vi.fn()

vi.mock('@/services/shops', () => ({
  getShops: () => mockGetShops(),
}))

vi.mock('@/services/stocks', () => ({
  getStockSummary: (shopId) => mockGetStockSummary(shopId),
}))

vi.mock('@/composables/useToast', () => ({
  useToast: () => ({
    showToast: vi.fn(),
  }),
}))

const buildWrapper = async () => {
  const wrapper = mount(StocksSection)
  await flushPromises()
  return wrapper
}

beforeEach(() => {
  mockGetShops.mockResolvedValue({ data: [] })
  mockGetStockSummary.mockResolvedValue({
    data: [
      { productId: 1, name: 'Бета', sku: 'B-2', totalQuantity: 120, daysUntilOos: 12.5 },
      { productId: 2, name: 'Альфа', sku: 'A-1', totalQuantity: 20, daysUntilOos: 4.2 },
      { productId: 3, name: 'Гамма', sku: 'C-3', totalQuantity: 65, daysUntilOos: null },
    ],
  })
})

it('renders rows sorted by остаток descending by default', async () => {
  const wrapper = await buildWrapper()
  const rows = wrapper.findAll('tbody tr')
  expect(rows).toHaveLength(3)
  expect(rows[0].text()).toContain('Бета')
  expect(rows[1].text()).toContain('Гамма')
  expect(rows[2].text()).toContain('Альфа')
})

it('sorts by name when header clicked', async () => {
  const wrapper = await buildWrapper()

  await wrapper.find('button[data-sort="name"]').trigger('click')
  const rows = wrapper.findAll('tbody tr')
  expect(rows[0].text()).toContain('Альфа')
  expect(rows[1].text()).toContain('Бета')
  expect(rows[2].text()).toContain('Гамма')
})

it('sorts by days until OOS when header clicked', async () => {
  const wrapper = await buildWrapper()

  await wrapper.find('button[data-sort="daysUntilOos"]').trigger('click')
  const rows = wrapper.findAll('tbody tr')
  expect(rows[0].text()).toContain('Гамма')
  expect(rows[1].text()).toContain('Бета')
  expect(rows[2].text()).toContain('Альфа')
})
