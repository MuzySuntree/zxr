import { get, post, put } from '../utils/request'

export const saveNotice = (data: any) => post<number>('/notice/save', data)
export const listUserNotices = (userId: number) => get<any[]>(`/notice/user/${userId}`)
export const getNoticeById = (noticeId: number) => get<any>(`/notice/${noticeId}`)
export const markNoticeRead = (noticeId: number) => put<boolean>(`/notice/read/${noticeId}`)
