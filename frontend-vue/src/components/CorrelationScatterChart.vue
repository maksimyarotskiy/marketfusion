<template>
  <div class="chart-container" v-if="pointsQtyRevenue.length || pointsPriceQty.length">
    <h3>Корреляционный анализ</h3>

    <div class="corr-section" v-if="pointsPriceQty.length">
      <h4>Ценовая чувствительность: средняя цена дня и проданные штуки</h4>
      <p class="corr-summary">
        Коэффициент: <strong>{{ priceQtyRLabel }}</strong>
        <span>({{ priceQtyStrengthLabel }})</span>
      </p>
      <div style="height: 320px; width: 100%">
        <Scatter :data="priceQtyChartData" :options="priceQtyChartOptions" />
      </div>
    </div>

    <div class="corr-section" v-if="pointsQtyRevenue.length">
      <h4>Базовая связь: продано (шт) и выручка</h4>
      <p class="corr-summary">
        Коэффициент: <strong>{{ qtyRevenueRLabel }}</strong>
        <span>({{ qtyRevenueStrengthLabel }})</span>
      </p>
      <div style="height: 320px; width: 100%">
        <Scatter :data="qtyRevenueChartData" :options="qtyRevenueChartOptions" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { Scatter } from 'vue-chartjs'
import {
  Chart as ChartJS,
  LinearScale,
  PointElement,
  Title,
  Tooltip,
  Legend,
} from 'chart.js'

ChartJS.register(LinearScale, PointElement, Title, Tooltip, Legend)

const props = defineProps({
  revenueByDay: {
    type: Object,
    required: true,
  },
  quantityByDay: {
    type: Object,
    required: true,
  },
})

const pointsQtyRevenue = computed(() => {
  const revenueEntries = Object.entries(props.revenueByDay || {})
  return revenueEntries
    .filter(([day]) => props.quantityByDay?.[day] !== undefined)
    .map(([day, revenue]) => ({
      day,
      x: Number(props.quantityByDay[day]) || 0,
      y: Number(revenue) || 0,
    }))
})

const pointsPriceQty = computed(() =>
  pointsQtyRevenue.value
    .filter((point) => point.x > 0)
    .map((point) => ({
      day: point.day,
      x: point.y / point.x,
      y: point.x,
    })),
)

const calcPearson = (points) => {
  if (points.length < 2) return null

  const xs = points.map((p) => p.x)
  const ys = points.map((p) => p.y)

  const xMean = xs.reduce((sum, value) => sum + value, 0) / xs.length
  const yMean = ys.reduce((sum, value) => sum + value, 0) / ys.length

  let num = 0
  let xDen = 0
  let yDen = 0

  for (let i = 0; i < xs.length; i++) {
    const dx = xs[i] - xMean
    const dy = ys[i] - yMean
    num += dx * dy
    xDen += dx * dx
    yDen += dy * dy
  }

  const den = Math.sqrt(xDen * yDen)
  if (!Number.isFinite(den) || den === 0) return null
  return num / den
}

const strengthLabelFrom = (r) => {
  if (r === null) return 'нет оценки'
  const abs = Math.abs(r)
  if (abs < 0.3) return 'слабая связь'
  if (abs < 0.5) return 'умеренная связь'
  if (abs < 0.7) return 'заметная связь'
  return 'сильная связь'
}

const qtyRevenueR = computed(() => calcPearson(pointsQtyRevenue.value))
const qtyRevenueRLabel = computed(() => {
  if (qtyRevenueR.value === null) return 'недостаточно данных'
  return qtyRevenueR.value.toFixed(3)
})
const qtyRevenueStrengthLabel = computed(() => strengthLabelFrom(qtyRevenueR.value))

const priceQtyR = computed(() => calcPearson(pointsPriceQty.value))
const priceQtyRLabel = computed(() => {
  if (priceQtyR.value === null) return 'недостаточно данных'
  return priceQtyR.value.toFixed(3)
})
const priceQtyStrengthLabel = computed(() => strengthLabelFrom(priceQtyR.value))

const qtyRevenueChartData = computed(() => ({
  datasets: [
    {
      label: 'Дневные точки (кол-во vs выручка)',
      data: pointsQtyRevenue.value,
      borderColor: '#0ea5e9',
      backgroundColor: 'rgba(14, 165, 233, 0.35)',
      pointRadius: 4,
    },
  ],
}))

const priceQtyChartData = computed(() => ({
  datasets: [
    {
      label: 'Дневные точки (цена vs кол-во)',
      data: pointsPriceQty.value,
      borderColor: '#f97316',
      backgroundColor: 'rgba(249, 115, 22, 0.35)',
      pointRadius: 4,
    },
  ],
}))

const qtyRevenueChartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      position: 'top',
    },
    tooltip: {
      callbacks: {
        label: (ctx) => {
          const point = ctx.raw
          return ` ${point.day}: ${point.x} шт, ${point.y.toFixed(2)} RUB`
        },
      },
    },
  },
  scales: {
    x: {
      title: {
        display: true,
        text: 'Продано, шт',
      },
    },
    y: {
      beginAtZero: true,
      title: {
        display: true,
        text: 'Выручка, RUB',
      },
    },
  },
}

const priceQtyChartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      position: 'top',
    },
    tooltip: {
      callbacks: {
        label: (ctx) => {
          const point = ctx.raw
          return ` ${point.day}: ${point.x.toFixed(2)} RUB, ${point.y} шт`
        },
      },
    },
  },
  scales: {
    x: {
      title: {
        display: true,
        text: 'Средняя цена дня, RUB',
      },
    },
    y: {
      beginAtZero: true,
      title: {
        display: true,
        text: 'Продано, шт',
      },
    },
  },
}
</script>

<style scoped>
.chart-container {
  margin-top: 24px;
}

.corr-section {
  margin-top: 18px;
}

.corr-section h4 {
  margin: 0 0 8px;
}

.corr-summary {
  margin: 0 0 12px;
  color: var(--muted);
}
</style>
