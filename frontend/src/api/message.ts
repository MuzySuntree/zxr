import { del, get, post, put } from '../utils/request'

export interface MessageItem {
  id: number
  userId: number
  category: number
  title: string
  content: string
  contactInfo?: string
  status?: number
  viewCount?: number
  createTime?: string
}

export interface MessageCreateDTO {
  userId: number
  category: number
  title: string
  content: string
  contactInfo?: string
}

export const listPublishedMessages = () => get<MessageItem[]>('/message/list')
export const listAdminMessages = () => get<MessageItem[]>('/message/admin/list')
export const saveMessage = (data: MessageCreateDTO) => post<number>('/message/save', data)
export const auditMessage = (id: number, status: 1 | 2) => put<boolean>(`/message/audit/${id}?status=${status}`)
export const deleteMessage = (id: number) => del<boolean>(`/message/${id}`)
