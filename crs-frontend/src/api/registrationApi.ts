import axiosClient from './axiosClient'
import type { Registration } from '../types/registration'

export const registerCourse = (payload: { studentId: number; courseId: number }) => {
  return axiosClient.post<Registration>('/api/registrations', payload)
}

export const getMyRegistrations = () => {
  return axiosClient.get<Registration[]>('/api/registrations/my')
}

export const cancelRegistration = (id: number) => {
  return axiosClient.delete(`/api/registrations/${id}`)
}
