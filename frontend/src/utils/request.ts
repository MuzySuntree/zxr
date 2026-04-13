import axios, { AxiosInstance, AxiosRequestConfig } from 'axios'
import { ElMessage } from 'element-plus'

export interface ApiResponse<T> {
  code: number
  message: string
  data: T
  timestamp: string
}

const service: AxiosInstance = axios.create({
  baseURL: 'http://127.0.0.1:8080',
  timeout: 10000
})

service.interceptors.response.use(
  (response) => {
    const result = response.data as ApiResponse<any>
    if (result && typeof result.code !== 'undefined' && result.code !== 200) {
      ElMessage.error(result.message || '请求失败')
      return Promise.reject(new Error(result.message || '请求失败'))
    }
    return result
  },
  (error) => {
    ElMessage.error(error?.message || '网络异常')
    return Promise.reject(error)
  }
)

export const get = <T>(url: string, config?: AxiosRequestConfig) =>
  service.get<any, ApiResponse<T>>(url, config)

export const post = <T>(url: string, data?: any, config?: AxiosRequestConfig) =>
  service.post<any, ApiResponse<T>>(url, data, config)

export const put = <T>(url: string, data?: any, config?: AxiosRequestConfig) =>
  service.put<any, ApiResponse<T>>(url, data, config)

export const del = <T>(url: string, config?: AxiosRequestConfig) =>
  service.delete<any, ApiResponse<T>>(url, config)
