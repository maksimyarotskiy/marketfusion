<template>
  <div class="chart-container">
    <h3>Выручка по платформам</h3>
    <div style="height: 360px; width: 100%">
      <Bar :data="chartData" :options="chartOptions" />
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { Bar } from 'vue-chartjs'
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
} from 'chart.js'

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend)

const props = defineProps({
  data: {
    type: Object,
    required: true,
  },
})

const labels = computed(() => Object.keys(props.data || {}))
const values = computed(() => Object.values(props.data || {}).map((v) => Number(v) || 0))

const chartData = computed(() => ({
  labels: labels.value,
  datasets: [
    {
      label: 'Выручка (₽)',
      data: values.value,
      backgroundColor: ['#6366f1', '#14b8a6', '#f97316', '#0ea5e9', '#a855f7'],
      borderRadius: 8,
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
  margin-top: 24px;
}
</style>
