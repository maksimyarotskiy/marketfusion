<template>
  <div class="chart-container" v-if="pointsPriceQty.length">
    <h3>Линейная регрессия: цена -> продажи</h3>
  <p class="corr-summary">
    y = {{ regLineLabel }}; R² = {{ regR2Label }}; DW = {{ dwLabel }}
  </p>
  <div class="dw-note">
    <div class="dw-title">Коэффициент Дарбина-Уотсона (Durbin-Watson)</div>
    <div class="dw-text">Проверяет автокорреляцию остатков регрессии.</div>
    <ul class="dw-list">
      <li>DW ~ 2 - автокорреляции нет</li>
      <li>DW &lt; 2 - положительная автокорреляция</li>
      <li>DW &gt; 2 - отрицательная автокорреляция</li>
    </ul>
    <div class="dw-text">В данном случае: {{ dwInterpretation }}</div>
  </div>
    <div style="height: 320px; width: 100%">
      <Scatter :data="regChartData" :options="regChartOptions" />
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
  LineElement,
  Title,
  Tooltip,
  Legend,
} from 'chart.js'

ChartJS.register(LinearScale, PointElement, LineElement, Title, Tooltip, Legend)

const props = defineProps({
  revenueByDay: { type: Object, required: true },
  quantityByDay: { type: Object, required: true },
})

const pointsPriceQty = computed(() => {
  const revenueEntries = Object.entries(props.revenueByDay || {}).sort(
    ([a], [b]) => new Date(a) - new Date(b),
  )
  return revenueEntries
    .filter(([day]) => props.quantityByDay?.[day] !== undefined)
    .map(([day, revenue]) => {
      const qty = Number(props.quantityByDay[day]) || 0
      const rev = Number(revenue) || 0
      if (qty <= 0) return null
      return {
        day,
        x: rev / qty,
        y: qty,
      }
    })
    .filter(Boolean)
})

const calcLinearRegression = (points) => {
  if (points.length < 2) return null
  const xs = points.map((p) => p.x)
  const ys = points.map((p) => p.y)
  const xMean = xs.reduce((s, v) => s + v, 0) / xs.length
  const yMean = ys.reduce((s, v) => s + v, 0) / ys.length

  let num = 0
  let den = 0
  for (let i = 0; i < xs.length; i++) {
    const dx = xs[i] - xMean
    num += dx * (ys[i] - yMean)
    den += dx * dx
  }
  if (den === 0) return null
  const a = num / den
  const b = yMean - a * xMean

  let ssRes = 0
  let ssTot = 0
  for (let i = 0; i < xs.length; i++) {
    const yPred = a * xs[i] + b
    ssRes += (ys[i] - yPred) ** 2
    ssTot += (ys[i] - yMean) ** 2
  }
  const r2 = ssTot === 0 ? 0 : 1 - ssRes / ssTot

  return { a, b, r2 }
}

const regression = computed(() => calcLinearRegression(pointsPriceQty.value))

const regLineLabel = computed(() => {
  if (!regression.value) return 'недостаточно данных'
  const { a, b } = regression.value
  return `${a.toFixed(4)}x + ${b.toFixed(2)}`
})

const regR2Label = computed(() => {
  if (!regression.value) return '—'
  return regression.value.r2.toFixed(3)
})

const calcDurbinWatson = (points, model) => {
  if (!model || points.length < 3) return null
  const { a, b } = model
  const residuals = points.map((p) => p.y - (a * p.x + b))
  let num = 0
  let den = 0
  for (let i = 1; i < residuals.length; i++) {
    const diff = residuals[i] - residuals[i - 1]
    num += diff * diff
  }
  for (let i = 0; i < residuals.length; i++) {
    den += residuals[i] * residuals[i]
  }
  if (!Number.isFinite(den) || den === 0) return null
  return num / den
}

const dwValue = computed(() => calcDurbinWatson(pointsPriceQty.value, regression.value))
const dwLabel = computed(() => {
  if (dwValue.value === null) return '—'
  return dwValue.value.toFixed(3)
})

const dwInterpretation = computed(() => {
  if (dwValue.value === null) return 'недостаточно данных'
  const v = dwValue.value
  if (v >= 1.5 && v <= 2.5) return 'существенной автокорреляции не выявлено'
  if (v < 1.5) return 'обнаружена положительная автокорреляция'
  return 'обнаружена отрицательная автокорреляция'
})

const regChartData = computed(() => {
  const points = pointsPriceQty.value
  if (!points.length || !regression.value) return { datasets: [] }

  const xs = points.map((p) => p.x)
  const minX = Math.min(...xs)
  const maxX = Math.max(...xs)
  const { a, b } = regression.value

  return {
    datasets: [
      {
        label: 'Наблюдения',
        data: points,
        borderColor: '#f97316',
        backgroundColor: 'rgba(249, 115, 22, 0.35)',
        pointRadius: 4,
        showLine: false,
      },
      {
        label: 'Линия регрессии',
        data: [
          { x: minX, y: a * minX + b },
          { x: maxX, y: a * maxX + b },
        ],
        borderColor: '#2563eb',
        backgroundColor: 'rgba(37, 99, 235, 0.15)',
        pointRadius: 0,
        showLine: true,
      },
    ],
  }
})

const regChartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: { position: 'top' },
    tooltip: {
      callbacks: {
        label: (ctx) => {
          const p = ctx.raw
          return ` ${p.day}: ${p.x.toFixed(2)} RUB, ${p.y} шт`
        },
      },
    },
  },
  scales: {
    x: { title: { display: true, text: 'Средняя цена дня, RUB' } },
    y: { beginAtZero: true, title: { display: true, text: 'Продано, шт' } },
  },
}
</script>

<style scoped>
.chart-container {
  margin-top: 24px;
}

.corr-summary {
  margin: 0 0 12px;
  color: var(--muted);
}

.dw-note {
  margin: 0 0 12px;
  padding: 10px 12px;
  border: 1px solid rgba(148, 163, 184, 0.35);
  border-radius: 8px;
  background: rgba(148, 163, 184, 0.08);
}

.dw-title {
  font-weight: 600;
  margin-bottom: 4px;
}

.dw-text {
  color: var(--muted);
  margin: 0 0 6px;
}

.dw-list {
  margin: 0 0 6px 16px;
  color: var(--muted);
}
</style>
