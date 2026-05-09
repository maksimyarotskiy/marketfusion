const toNumber = (value) => {
  const num = Number(value)
  return Number.isFinite(num) ? num : 0
}

const sum = (values) => values.reduce((acc, value) => acc + value, 0)

const average = (values) => {
  if (!values.length) return 0
  return sum(values) / values.length
}

const stdDev = (values) => {
  if (values.length < 2) return 0
  const mean = average(values)
  const variance = average(values.map((value) => (value - mean) ** 2))
  return Math.sqrt(variance)
}

const percentChange = (base, next) => {
  if (!Number.isFinite(base) || base === 0) return null
  return (next - base) / base
}

const splitHalves = (values) => {
  if (values.length < 4) return null
  const middle = Math.floor(values.length / 2)
  return [values.slice(0, middle), values.slice(middle)]
}

const sortedEntries = (obj) =>
  Object.entries(obj || {})
    .map(([date, value]) => [date, toNumber(value)])
    .sort(([a], [b]) => new Date(a) - new Date(b))

const buildRevenueTrend = (dailyRevenue) => {
  const values = sortedEntries(dailyRevenue).map(([, value]) => value)
  const halves = splitHalves(values)
  if (!halves) return null
  const [firstHalf, secondHalf] = halves
  const firstAvg = average(firstHalf)
  const secondAvg = average(secondHalf)
  const change = percentChange(firstAvg, secondAvg)
  if (change === null) return null
  return { firstAvg, secondAvg, change }
}

const buildPricePressure = (dailyRevenue, dailyQuantity) => {
  const revenueEntries = sortedEntries(dailyRevenue)
  const points = revenueEntries
    .map(([date, revenue]) => {
      const qty = toNumber(dailyQuantity?.[date])
      if (qty <= 0) return null
      return {
        price: revenue / qty,
        qty,
      }
    })
    .filter(Boolean)

  const halves = splitHalves(points)
  if (!halves) return null

  const [firstHalf, secondHalf] = halves
  const firstPrice = average(firstHalf.map((point) => point.price))
  const secondPrice = average(secondHalf.map((point) => point.price))
  const firstQty = average(firstHalf.map((point) => point.qty))
  const secondQty = average(secondHalf.map((point) => point.qty))

  return {
    priceChange: percentChange(firstPrice, secondPrice),
    qtyChange: percentChange(firstQty, secondQty),
  }
}

const formatPercent = (value) => `${Math.abs(value * 100).toFixed(0)}%`

export const analyzeMetrics = ({
  revenueTotal,
  avgCheck,
  totalItems,
  dailyRevenue,
  dailyQuantity,
  topProducts,
  revenueByPlatform,
  productSummary,
}) => {
  const totalRevenue = toNumber(revenueTotal)
  const totalSold = toNumber(totalItems)
  const averageCheck = toNumber(avgCheck)
  const platformEntries = Object.entries(revenueByPlatform || {}).map(([platform, value]) => [
    platform,
    toNumber(value),
  ])
  const sortedPlatforms = [...platformEntries].sort((a, b) => b[1] - a[1])
  const topPlatform = sortedPlatforms[0] || null
  const platformShare = topPlatform && totalRevenue > 0 ? topPlatform[1] / totalRevenue : 0

  const normalizedTopProducts = (topProducts || []).map((item) => ({
    name: item.name,
    totalRevenue: toNumber(item.totalRevenue),
  }))
  const topProduct = normalizedTopProducts[0] || null
  const topProductShare = topProduct && totalRevenue > 0 ? topProduct.totalRevenue / totalRevenue : 0
  const top3Revenue = sum(normalizedTopProducts.slice(0, 3).map((item) => item.totalRevenue))
  const top3Share = totalRevenue > 0 ? top3Revenue / totalRevenue : 0

  const revenueValues = sortedEntries(dailyRevenue).map(([, value]) => value).filter((value) => value > 0)
  const volatility = revenueValues.length > 1 ? stdDev(revenueValues) / Math.max(average(revenueValues), 1) : 0
  const trend = buildRevenueTrend(dailyRevenue)
  const pricePressure = buildPricePressure(dailyRevenue, dailyQuantity)

  const recommendations = []

  if (totalRevenue <= 0 || totalSold <= 0) {
    recommendations.push({
      level: 'danger',
      title: 'Продажи за период отсутствуют',
      text: 'Проверьте, есть ли активные товары, корректно ли выбран период и не требуется ли запуск промо-активности.',
      basis: 'За выбранный интервал система не зафиксировала выручку и продажи.',
    })
  } else {
    if (trend?.change <= -0.15) {
      recommendations.push({
        level: 'warning',
        title: 'Продажи снижаются во второй половине периода',
        text: 'Имеет смысл проверить цену, наличие товара и активность продвижения по товарам, которые давали основной объем выручки.',
        basis: `Средняя дневная выручка снизилась примерно на ${formatPercent(trend.change)}.`,
      })
    } else if (trend?.change >= 0.15) {
      recommendations.push({
        level: 'success',
        title: 'Продажи показывают положительный тренд',
        text: 'Период можно использовать как ориентир: сохранить текущую ценовую политику и усилить продвижение по товарам-лидерам.',
        basis: `Средняя дневная выручка выросла примерно на ${formatPercent(trend.change)}.`,
      })
    }

    if (platformShare >= 0.7 && topPlatform) {
      recommendations.push({
        level: 'warning',
        title: 'Выручка сильно зависит от одной платформы',
        text: 'Стоит проверить, можно ли перераспределить ассортимент или рекламные усилия на другие площадки, чтобы снизить зависимость от одного канала.',
        basis: `${topPlatform[0]} формирует около ${(platformShare * 100).toFixed(0)}% общей выручки.`,
      })
    } else if (sortedPlatforms.length >= 2 && platformShare > 0 && platformShare <= 0.5) {
      recommendations.push({
        level: 'success',
        title: 'Выручка распределена между площадками достаточно ровно',
        text: 'Текущая структура продаж снижает риск потери оборота при просадке одной платформы.',
        basis: `Доля лидирующей площадки не превышает ${(platformShare * 100).toFixed(0)}% общей выручки.`,
      })
    }

    if (topProductShare >= 0.5 && topProduct) {
      recommendations.push({
        level: 'warning',
        title: 'Сильная зависимость от одного товара',
        text: 'Стоит расширять продажи следующих по выручке позиций, чтобы снизить риск просадки при падении спроса на один SKU.',
        basis: `${topProduct.name} дает около ${(topProductShare * 100).toFixed(0)}% выручки за период.`,
      })
    } else if (top3Share > 0 && top3Share <= 0.75 && normalizedTopProducts.length >= 3) {
      recommendations.push({
        level: 'success',
        title: 'Продажи распределены между несколькими товарами',
        text: 'Ассортимент не сводится к одному лидеру, что делает продажи более устойчивыми.',
        basis: `Первые три товара формируют около ${(top3Share * 100).toFixed(0)}% общей выручки.`,
      })
    }

    if (volatility >= 0.6) {
      recommendations.push({
        level: 'info',
        title: 'Дневная выручка меняется неравномерно',
        text: 'Имеет смысл проверить влияние акций, наличия товара и внешних событий по дням с резкими отклонениями.',
        basis: `Колебания дневной выручки заметно выше среднего уровня продаж за период.`,
      })
    }

    if (pricePressure?.priceChange >= 0.1 && pricePressure?.qtyChange <= -0.1) {
      recommendations.push({
        level: 'warning',
        title: 'Есть признаки чувствительности спроса к цене',
        text: 'Рост средней цены сопровождался снижением количества продаж. Для части товаров стоит проверить мягкий ценовой тест или промо-механику.',
        basis: `Средняя цена выросла примерно на ${formatPercent(pricePressure.priceChange)}, а средний дневной объем продаж снизился примерно на ${formatPercent(pricePressure.qtyChange)}.`,
      })
    }

    if (pricePressure?.priceChange <= -0.1 && pricePressure?.qtyChange <= 0.05) {
      recommendations.push({
        level: 'info',
        title: 'Снижение цены не дало заметного прироста объема',
        text: 'Вероятно, проблема не только в цене. Имеет смысл проверить карточку товара, рекламу и наличие на складе.',
        basis: `Средняя цена снизилась примерно на ${formatPercent(pricePressure.priceChange)}, но объем продаж не показал сопоставимого роста.`,
      })
    }

    if (totalSold < 20) {
      recommendations.push({
        level: 'info',
        title: 'Небольшое число продаж за период',
        text: 'По такому объему сложнее делать устойчивые выводы. Для более точной аналитики нужен более длинный период или больший объем продаж.',
        basis: `За выбранный интервал продано ${totalSold} единиц товара.`,
      })
    }
  }

  if (!recommendations.length) {
    recommendations.push({
      level: 'info',
      title: 'Выраженных отклонений по выбранным метрикам не найдено',
      text: 'Текущие показатели выглядят стабильными. Можно использовать этот период как базовый ориентир для последующего сравнения.',
      basis: 'Автоматические правила не выявили сильной концентрации, резкого тренда или ценового перекоса.',
    })
  }

  const highlights = [
    {
      label: 'Выручка',
      value: totalRevenue,
      type: 'currency',
    },
    {
      label: 'Средний чек',
      value: averageCheck,
      type: 'currency',
    },
    {
      label: 'Продано',
      value: totalSold,
      type: 'number',
    },
    {
      label: 'Лидирующая площадка',
      value: topPlatform ? `${topPlatform[0]} (${(platformShare * 100).toFixed(0)}%)` : '—',
      type: 'text',
    },
    {
      label: 'Доля топ-товара',
      value: topProduct ? `${(topProductShare * 100).toFixed(0)}%` : '—',
      type: 'text',
    },
    {
      label: 'Товаров в сводке',
      value: Array.isArray(productSummary) ? productSummary.length : 0,
      type: 'number',
    },
  ]

  return { recommendations, highlights }
}
