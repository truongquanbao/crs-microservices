import { useCallback, useState } from 'react'
import axios from 'axios'
import { useCourses } from '../api/useCourses'
import { registerCourse } from '../api/registrationApi'
import { useAuth } from '../context/AuthContext'
import { useToast } from '../hooks/useToast'
import Toast from '../components/Toast'
import type { Course } from '../types/course'
import type { ApiErrorResponse } from '../types/apiError'

export default function RegisterCoursePage() {
  const { user } = useAuth()
  const { courses, state, errorMessage, refetch } = useCourses('', 0)
  const { toast, showToast, clearToast } = useToast()
  const [registeringId, setRegisteringId] = useState<number | null>(null)

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
    [user?.userId, showToast, refetch],
  )

  return (
    <main style={{ padding: 24, maxWidth: 800, margin: '0 auto' }}>
      <h1>Dang ky hoc phan</h1>
      {state === 'loading' && <p>Dang tai danh sach mon hoc...</p>}
      {state === 'error' && (
        <div style={{ color: '#b91c1c' }}>
          <p>{errorMessage}</p>
          <button onClick={refetch}>Thu lai</button>
        </div>
      )}
      {state === 'empty' && <p>Khong co mon hoc nao co san.</p>}
      {state === 'success' && (
        <table style={{ width: '100%', borderCollapse: 'collapse' }}>
          <thead>
            <tr style={{ textAlign: 'left', borderBottom: '2px solid #333' }}>
              <th>Ten mon hoc</th>
              <th>So tin chi</th>
              <th>So cho con lai</th>
              <th>Thao tac</th>
            </tr>
          </thead>
          <tbody>
            {courses.map((course) => (
              <tr key={course.id} style={{ borderBottom: '1px solid #eee' }}>
                <td>{course.tenMonHoc}</td>
                <td>{course.soTinChi}</td>
                <td style={{ color: course.soChoConLai === 0 ? '#b91c1c' : 'inherit' }}>
                  {course.soChoConLai} / {course.soChoToiDa}
                </td>
                <td>
                  <button
                    onClick={() => handleRegister(course)}
                    disabled={course.soChoConLai === 0 || registeringId === course.id}
                  >
                    {course.soChoConLai === 0 ? 'Het cho' : registeringId === course.id ? 'Dang ky...' : 'Dang ky'}
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
      {toast && <Toast message={toast.message} type={toast.type} onClose={clearToast} />}
    </main>
  )
}
