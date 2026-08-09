import axiosInstance from './axios'
import type { StockAlert } from '../types'

export const notificationApi = {
  getAlerts: () =>
    axiosInstance.get<StockAlert[]>('/notifications').then((r) => r.data),

  getAlertCount: () =>
    axiosInstance.get<number>('/notifications/count').then((r) => r.data),
}
