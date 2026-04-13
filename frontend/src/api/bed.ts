import { del, get, post, put } from '../utils/request'

export const listBeds = () => get<any[]>('/bed/list')
export const saveBed = (data: any) => post<boolean>('/bed/save', data)
export const updateBed = (data: any) => put<boolean>('/bed/update', data)
export const deleteBed = (id: number) => del<boolean>(`/bed/${id}`)
export const getAvailableBedsByGender = (params: any) => get<any[]>('/bed/available/by-gender', params)
