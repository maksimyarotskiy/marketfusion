<template>
  <div class="chart-container">
    <h3>Динамика выручки</h3>
    <div style="height: 420px; width: 100%">
      <Line :data="chartData" :options="chartOptions" />
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { Line } from 'vue-chartjs'
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
} from 'chart.js'

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend)

const props = defineProps({
  data: {
    type: Object,
    required: true,
  },
})

const toNumber = (value) => {
  if (typeof value === 'number') return value
  if (typeof value === 'string') {
    const n = parseFloat(value)
    return isNaN(n) ? 0 : n
  }
  return 0
}

const formatDateLabel = (isoString) => {
  const d = new Date(isoString)
  return `${String(d.getDate()).padStart(2, '0')}.${String(d.getMonth() + 1).padStart(2, '0')}`
}

const entries = computed(() =>
  Object.entries(props.data).sort(([a], [b]) => new Date(a) - new Date(b)),
)

const labels = computed(() => entries.value.map(([date]) => formatDateLabel(date)))

const values = computed(() => entries.value.map(([, value]) => toNumber(value)))

const cumulative = computed(() => {
  let sum = 0
  return values.value.map((v) => {
    sum += v
    return sum
  })
})

const movingAvg = computed(() => {
  const window = 7
  return values.value.map((_, i) => {
    const start = Math.max(0, i - window + 1)
    const slice = values.value.slice(start, i + 1)
    const avg = slice.reduce((acc, v) => acc + v, 0) / slice.length
    return Number.isFinite(avg) ? avg : 0
  })
})

const chartData = computed(() => ({
  labels: labels.value,
  datasets: [
    {
      label: 'Выручка (день)',
      data: values.value,
      borderColor: '#2563eb',
      backgroundColor: 'rgba(37, 99, 235, 0.15)',
      tension: 0.3,
      fill: true,
      pointRadius: 2,
    },
    {
      label: 'Скользящее среднее (7д)',
      data: movingAvg.value,
      borderColor: '#14b8a6',
      backgroundColor: 'rgba(20, 184, 166, 0.1)',
      tension: 0.25,
      fill: false,
      pointRadius: 0,
      borderDash: [6, 4],
    },
    {
      label: 'Общая выручка',
      data: cumulative.value,
      borderColor: '#f97316',
      backgroundColor: 'rgba(249, 115, 22, 0.08)',
      tension: 0.2,
      fill: false,
      pointRadius: 0,
    },
  ],
}))

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      position: 'top',
    },
    tooltip: {
      callbacks: {
        label: (ctx) => `${ctx.parsed.y.toFixed(2)} ₽`,
      },
    },
  },
  scales: {
    y: {
      beginAtZero: true,
      ticks: {
        callback: (value) => `${value} ₽`,
      },
    },
  },
}
</script>

<style scoped>
.chart-container {
  margin-top: 32px;
}
</style>
