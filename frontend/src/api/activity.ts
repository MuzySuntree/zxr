import { del, get, post, put } from '../utils/request'

export interface ActivityItem {
  id: number
  title: string
  coverImage?: string
  description?: string
  activityTime?: string
  location?: string
  maxPeople?: number
  joinedPeople?: number
  status: number
  recommend?: number
  sortNo?: number
}

export interface ActivitySaveDTO {
  id?: number
  title: string
  coverImage?: string
  description?: string
  activityTime: string
  location: string
  maxPeople: number
  joinedPeople?: number
  status: number
  recommend: number
  sortNo: number
}

export const listActivities = () => get<ActivityItem[]>('/activity/list')
export const saveActivity = (data: ActivitySaveDTO) => post<number>('/activity/save', data)
export const updateActivity = (data: ActivitySaveDTO) => put<boolean>('/activity/update', data)
export const deleteActivity = (id: number) => del<boolean>(`/activity/${id}`)
export const setActivityRecommend = (id: number, recommend: number) => put<boolean>(`/activity/recommend/${id}?recommend=${recommend}`)

export const uploadActivityImage = (file: File) => {
  const formData = new FormData()
  formData.append('file', file)
  return post<string>('/activity/upload-image', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export const signupActivity = (activityId: number, userId: number) =>
  post<boolean>(`/activity/signup?activityId=${activityId}&userId=${userId}`)
export const cancelSignupActivity = (activityId: number, userId: number) =>
  put<boolean>(`/activity/signup/cancel?activityId=${activityId}&userId=${userId}`)
export const listUserSignedActivities = (userId: number) => get<ActivityItem[]>(`/activity/signup/user/${userId}`)
