import { useCallback, useEffect, useState } from 'react'
import axios from 'axios'
import { getCourses } from './courseApi'
import type { ApiErrorResponse } from '../types/apiError'
import type { Course } from '../types/course'

export type LoadState = 'loading' | 'success' | 'empty' | 'error'

export function useCourses(keyword: string, page: number, size = 10) {
  const [courses, setCourses] = useState<Course[]>([])
  const [totalPages, setTotalPages] = useState(0)
  const [state, setState] = useState<LoadState>('loading')
  const [errorMessage, setErrorMessage] = useState('')

  const fetchCourses = useCallback(async (signal?: AbortSignal) => {
    setState('loading')
    setErrorMessage('')

    try {
      const response = await getCourses(keyword, page, size, signal)
      const data = response.data
      setCourses(data.content)
      setTotalPages(data.totalPages)
      setState(data.content.length === 0 ? 'empty' : 'success')
    } catch (error: unknown) {
      if (axios.isCancel(error)) return

      let message = 'Da xay ra loi khong xac dinh, vui long thu lai.'
      if (axios.isAxiosError<ApiErrorResponse>(error)) {
        if (error.response?.data?.message) {
          message = error.response.data.message
        } else if (!error.response) {
          message = 'Khong ket noi duoc toi he thong. Vui long thu lai sau.'
        }
      }
      setErrorMessage(message)
      setState('error')
    }
  }, [keyword, page, size])

  useEffect(() => {
    const controller = new AbortController()
    void fetchCourses(controller.signal)
    return () => controller.abort()
  }, [fetchCourses])

  const refetch = useCallback(() => void fetchCourses(), [fetchCourses])

  return { courses, totalPages, state, errorMessage, refetch }
}