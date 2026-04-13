import { del, get, post, put } from '../utils/request'

export const listRooms = () => get<any[]>('/room/list')
export const saveRoom = (data: any) => post<boolean>('/room/save', data)
export const updateRoom = (data: any) => put<boolean>('/room/update', data)
export const deleteRoom = (id: number) => del<boolean>(`/room/${id}`)
