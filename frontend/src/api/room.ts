import { get, post, put, del } from '../utils/request'

export const listRooms = () => get<any[]>('/room/list')
export const saveRoom = (data: any) => post<boolean>('/room/save', data)
export const updateRoom = (data: any) => put<boolean>('/room/update', data)
export const deleteRoom = (roomId: number) => del<boolean>(`/room/${roomId}`)
export const getRoomById = (roomId: number) => get<any>(`/room/${roomId}`)
export const getRoomOccupancy = (params: { roomId: number; checkInDate: string; checkOutDate: string }) =>
  get<any>('/room/occupancy', { params })
