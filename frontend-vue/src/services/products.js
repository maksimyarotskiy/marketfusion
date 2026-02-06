import http from './http'

export const getProductsByShop = (shopId) => http.get(`/api/products/shop/${shopId}`)
