import { del, get, post, put } from '../utils/request'

export const createOrder = (data: any) => post<number>('/order/create', data)
export const listOrders = () => get<any[]>('/order/list')
export const payOrder = (id: number) => post<any>(`/order/pay/${id}`)
export const allocateOrder = (id: number) => post<any>(`/order/allocate/${id}`)
export const getAllocation = (id: number) => get<any>(`/order/allocation/${id}`)
export const cancelOrder = (id: number) => put<boolean>(`/order/cancel/${id}`)
export const deleteOrder = (id: number) => del<boolean>(`/order/${id}`)
