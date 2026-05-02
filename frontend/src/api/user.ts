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

export interface SysUserItem {
  id: number
  username: string
  realName?: string
  gender?: number
  phone?: string
  avatar?: string
  email?: string
  idCard?: string
  emergencyContact?: string
  emergencyPhone?: string
  remark?: string
  role: number
  status: number
  lastLoginTime?: string
  createTime?: string
}

export const registerUser = (data: UserRegisterDTO) => post<number>('/user/register', data)
export const sendRegisterCode = (phone: string) => post<any>(`/user/register/code?phone=${encodeURIComponent(phone)}`)
export const checkRegisterCode = (phone: string, code: string) =>
  get<boolean>(`/user/register/code/check?phone=${encodeURIComponent(phone)}&code=${encodeURIComponent(code)}`)
export const loginUser = (data: UserLoginDTO) => post<any>('/user/login', data)

export const getUserById = (id: number) => get<SysUserItem>(`/user/${id}`)
export const listUsers = () => get<SysUserItem[]>('/user/list')
export const updateUser = (data: Partial<SysUserItem>) => put<boolean>('/user/update', data)
export const deleteUser = (id: number) => del<boolean>(`/user/${id}`)
