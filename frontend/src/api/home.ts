import { get } from '../utils/request'

export interface HomeBannerItem {
  id: number
  title: string
  subtitle?: string
  imageUrl: string
  linkUrl?: string
}

export interface RecommendActivityItem {
  id: number
  title: string
  coverImage?: string
  activityTime?: string
  location?: string
  maxPeople?: number
  joinedPeople?: number
}

export interface LatestMessageItem {
  id: number
  category: number
  title: string
  content: string
  createTime?: string
}

export interface HomeIndexData {
  banners: HomeBannerItem[]
  recommendActivities: RecommendActivityItem[]
  latestMessages: LatestMessageItem[]
}

export const getHomeIndex = () => get<HomeIndexData>('/home/index')
