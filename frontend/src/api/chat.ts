import { get, post, put } from '../utils/request'

export interface ChatSession {
  id: number
  userId: number
  adminId?: number
  status: number
  lastMessage?: string
  lastMessageTime?: string
}

export interface ChatMessage {
  id: number
  sessionId: number
  senderId: number
  senderRole: number
  content: string
  isRead: number
  createTime: string
}

export const openChatSession = (userId: number) => post<ChatSession>(`/chat/session/open?userId=${userId}`)
export const listUserChatSessions = (userId: number) => get<ChatSession[]>(`/chat/session/user/${userId}`)
export const sendChatMessage = (data: { sessionId: number; senderId: number; senderRole: 1 | 2; content: string }) =>
  post<boolean>('/chat/message/send', data)
export const listChatMessages = (sessionId: number) => get<ChatMessage[]>(`/chat/message/list?sessionId=${sessionId}`)
export const markChatRead = (sessionId: number, readerRole: 1 | 2) =>
  put<boolean>(`/chat/message/read?sessionId=${sessionId}&readerRole=${readerRole}`)
