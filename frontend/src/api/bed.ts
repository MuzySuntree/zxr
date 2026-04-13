import { get, post, put, del } from '../utils/request'

export const listBeds = () => get<any[]>('/bed/list')
export const saveBed = (data: any) => post<boolean>('/bed/save', data)
export const updateBed = (data: any) => put<boolean>('/bed/update', data)
export const deleteBed = (bedId: number) => del<boolean>(`/bed/${bedId}`)
export const getBedById = (bedId: number) => get<any>(`/bed/${bedId}`)

export const getAvailableBeds = (params: { checkInDate: string; checkOutDate: string }) =>
  get<any[]>('/bed/available', { params })

export const getAvailableBedsByGender = (params: {
  checkInDate: string
  checkOutDate: string
  userGender: number
}) => get<any[]>('/bed/available/by-gender', { params })

export const getAvailableBedsByRoom = (params: {
  roomId: number
  checkInDate: string
  checkOutDate: string
}) => get<any[]>('/bed/available/by-room', { params })
