import { del, get, post, put } from '../utils/request'

export const registerUser = (data: any) => post<number>('/user/register', data)
export const loginUser = (data: any) => post<any>('/user/login', data)
export const getUserById = (id: number) => get<any>(`/user/${id}`)
export const listUsers = () => get<any[]>('/user/list')
export const updateUser = (data: any) => put<boolean>('/user/update', data)
export const deleteUser = (id: number) => del<boolean>(`/user/${id}`)
