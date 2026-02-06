<template>
  <div class="chart-container">
    <h3>Доля выручки по платформам</h3>
    <div style="height: 360px; width: 100%">
      <Pie :data="chartData" :options="chartOptions" />
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { Pie } from 'vue-chartjs'
import { Chart as ChartJS, ArcElement, Tooltip, Legend, Title } from 'chart.js'

ChartJS.register(ArcElement, Tooltip, Legend, Title)

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
      data: values.value,
      backgroundColor: ['#6366f1', '#14b8a6', '#f97316', '#0ea5e9', '#a855f7'],
      borderWidth: 1,
    },
  ],
}))

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: { position: 'right' },
    tooltip: {
      callbacks: {
        label: (ctx) => `${ctx.label}: ${ctx.parsed.toFixed(2)} ₽`,
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
