import http from './http'

export const getShops = () => http.get('/api/shops')

export const createShop = (payload) => http.post('/api/shops', payload)

export const updateShop = (id, payload) => http.put(`/api/shops/${id}`, payload)

export const deleteShop = (id) => http.delete(`/api/shops/${id}`)
