import { del, get, post, put } from '../utils/request'

export interface BannerItem {
  id?: number
  title: string
  subtitle?: string
  imageUrl: string
  linkUrl?: string
  sortNo: number
  status: number
}

export const listBanners = () => get<BannerItem[]>('/banner/list')
export const saveBanner = (data: BannerItem) => post<number>('/banner/save', data)
export const updateBanner = (data: BannerItem) => put<boolean>('/banner/update', data)
export const deleteBanner = (id: number) => del<boolean>(`/banner/${id}`)
