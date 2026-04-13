import { get, put } from '../utils/request'

export const listUserNotices = (userId: number) => get<any[]>(`/notice/user/${userId}`)
export const markNoticeRead = (id: number) => put<boolean>(`/notice/read/${id}`)
