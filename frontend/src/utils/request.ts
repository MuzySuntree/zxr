import axios from 'axios'
import { ElMessage } from 'element-plus'

export interface ApiResponse<T> { code: number; message: string; data: T; timestamp: string }

const request = axios.create({ baseURL: 'http://127.0.0.1:8080', timeout: 10000 })

request.interceptors.response.use(
  (resp) => {
    const result = resp.data as ApiResponse<any>
    if (typeof result?.code !== 'undefined' && result.code !== 200) {
      ElMessage.error(result.message || '请求失败')
      return Promise.reject(new Error(result.message || '请求失败'))
    }
    return result
  },
  (err) => {
    ElMessage.error(err?.message || '网络错误')
    return Promise.reject(err)
  }
)

export const get = <T>(url: string, params?: Record<string, any>) => request.get<any, ApiResponse<T>>(url, { params })
export const post = <T>(url: string, data?: any) => request.post<any, ApiResponse<T>>(url, data)
export const put = <T>(url: string, data?: any) => request.put<any, ApiResponse<T>>(url, data)
export const del = <T>(url: string) => request.delete<any, ApiResponse<T>>(url)
