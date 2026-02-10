import http from './http'

export const getStockSummary = (shopId) => {
  const params = shopId ? { shopId } : {}
  return http.get('/api/stocks/summary', { params })
}
