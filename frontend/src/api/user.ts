import { post, get, put, del } from '../utils/request'

export interface UserRegisterDTO {
  username: string
  password: string
  confirmPassword: string
  realName: string
  gender: number | null
  phone: string
  verifyCode: string
}

export interface UserLoginDTO {
  username: string
  password: string
}

export const registerUser = (data: UserRegisterDTO) => post<number>('/user/register', data)
export const sendRegisterCode = (phone: string) => post<any>(`/user/register/code?phone=${encodeURIComponent(phone)}`)
export const checkRegisterCode = (phone: string, code: string) =>
  get<boolean>(`/user/register/code/check?phone=${encodeURIComponent(phone)}&code=${encodeURIComponent(code)}`)
export const loginUser = (data: UserLoginDTO) => post<any>('/user/login', data)
export const getUserById = (id: number) => get<any>(`/user/${id}`)
export const listUsers = () => get<any[]>('/user/list')
export const updateUser = (data: any) => put<boolean>('/user/update', data)
export const deleteUser = (id: number) => del<boolean>(`/user/${id}`)
