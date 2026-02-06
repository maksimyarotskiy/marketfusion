import http from './http'

export const getRevenueTotal = (from, to) => {
  if (from && to) {
    return http.get('/api/analytics/revenue/custom', { params: { from, to } })
  }
  return http.get('/api/analytics/revenue')
}

export const getDailyRevenue = (from, to) => {
  if (from && to) {
    return http.get('/api/analytics/daily-revenue/custom', { params: { from, to } })
  }
  return http.get('/api/analytics/daily-revenue')
}

export const getAverageCheck = (from, to) =>
  http.get(`/api/analytics/average-check?from=${from}&to=${to}`)

export const getTotalItems = (from, to) =>
  http.get(`/api/analytics/total-items?from=${from}&to=${to}`)

export const getTopProducts = (limit = 5, from, to) => {
  if (from && to) {
    return http.get('/api/analytics/top-products/custom', { params: { from, to, limit } })
  }
  return http.get(`/api/analytics/top-products?limit=${limit}`)
}

export const getRevenueByPlatform = (from, to) =>
  http.get(`/api/analytics/revenue-by-platform?from=${from}&to=${to}`)
