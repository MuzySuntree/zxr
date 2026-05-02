import { get, post, put, del } from '../utils/request'

export const createOrder = (data: any) => post<number>('/order/create', data)
export const listOrders = () => get<any[]>('/order/list')
export const getOrderById = (orderId: number) => get<any>(`/order/${orderId}`)
export const cancelOrder = (orderId: number) => put<boolean>(`/order/cancel/${orderId}`)
export const deleteOrder = (orderId: number) => del<boolean>(`/order/${orderId}`)

export const checkOrderAvailable = (params: {
  checkInDate: string
  checkOutDate: string
  userGender?: number
  userId?: number
}) => get<boolean>('/order/check-available', { params })

export const payOrder = (orderId: number) => post<any>(`/order/pay/${orderId}`)
export const allocateOrder = (orderId: number) => post<any>(`/order/allocate/${orderId}`)
export const getAllocation = (orderId: number) => get<any>(`/order/allocation/${orderId}`)
