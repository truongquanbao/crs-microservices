import { useCallback, useState } from 'react'
import axios from 'axios'
import { useCourses } from '../api/useCourses'
import { registerCourse } from '../api/registrationApi'
import { useAuth } from '../context/AuthContext'
import { useToast } from '../hooks/useToast'
import SearchBox from '../components/SearchBox'
import CourseList from '../components/CourseList'
import Pagination from '../components/Pagination'
import Toast from '../components/Toast'
import type { Course } from '../types/course'
import type { ApiErrorResponse } from '../types/apiError'

export default function RegisterCoursePage() {
  const [keyword, setKeyword] = useState('')
  const [page, setPage] = useState(0)
  const { user } = useAuth()
  const { courses, totalPages, state, errorMessage, refetch } = useCourses(keyword, page)
  const { toast, showToast, clearToast } = useToast()
  const [registeringId, setRegisteringId] = useState<number | null>(null)

  const handleSearch = useCallback((newKeyword: string) => {
    setKeyword(newKeyword)
    setPage(0)
  }, [])

  const handleRegister = useCallback(
    async (course: Course) => {
      if (!user?.userId) {
        showToast('Khong co thong tin nguoi dung.', 'error')
        return
      }

      setRegisteringId(course.id)
      try {
        await registerCourse({ studentId: user.userId, courseId: course.id })
        showToast(`Dang ky thanh cong mon "${course.tenMonHoc}"`, 'success')
        refetch()
      } catch (err) {
        let message = 'Dang ky khong thanh cong.'
        if (axios.isAxiosError<ApiErrorResponse>(err) && err.response?.data?.message) {
          message = err.response.data.message
        }
        showToast(message, 'error')
      } finally {
        setRegisteringId(null)
      }
    },
    [user, showToast, refetch],
  )

  return (
    <main style={{ padding: 24, maxWidth: 800, margin: '0 auto' }}>
      <h1>Dang ky hoc phan</h1>
      <SearchBox onSearch={handleSearch} />
      <div style={{ marginTop: 16 }}>
        <CourseList
          courses={courses}
          state={state}
          errorMessage={errorMessage}
          onRetry={refetch}
          onRegister={handleRegister}
          registeringId={registeringId}
        />
      </div>
      <Pagination currentPage={page} totalPages={totalPages} onPageChange={setPage} />
      {toast && <Toast message={toast.message} type={toast.type} onClose={clearToast} />}
    </main>
  )
}
