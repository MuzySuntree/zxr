import { del, get, post, put } from '../utils/request'

export interface UserPaymentSetting {
  id?: number
  userId: number
  payType: number
  balance: number
  isDefault: number
  status: number
}

export const getUserPaymentSettings = (userId: number) => get<UserPaymentSetting[]>(`/payment/user/${userId}`)
export const savePaymentSetting = (data: UserPaymentSetting) => post<number>('/payment/save', data)
export const updatePaymentSetting = (data: UserPaymentSetting) => put<boolean>('/payment/update', data)
export const deletePaymentSetting = (id: number) => del<boolean>(`/payment/${id}`)
export const setDefaultPaymentSetting = (id: number) => put<boolean>(`/payment/default/${id}`)
