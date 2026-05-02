import { get, post } from '../utils/request'

export interface MessageItem {
  id: number
  userId: number
  category: number
  title: string
  content: string
  contactInfo?: string
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
export const saveMessage = (data: MessageCreateDTO) => post<number>('/message/save', data)
