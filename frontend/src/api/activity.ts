import { get, post, put } from '../utils/request'

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
}

export const listActivities = () => get<ActivityItem[]>('/activity/list')
export const signupActivity = (activityId: number, userId: number) =>
  post<boolean>(`/activity/signup?activityId=${activityId}&userId=${userId}`)
export const cancelSignupActivity = (activityId: number, userId: number) =>
  put<boolean>(`/activity/signup/cancel?activityId=${activityId}&userId=${userId}`)
export const listUserSignedActivities = (userId: number) => get<ActivityItem[]>(`/activity/signup/user/${userId}`)
