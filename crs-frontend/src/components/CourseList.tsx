import type { LoadState } from '../api/useCourses'
import type { Course } from '../types/course'

interface CourseListProps {
  courses: Course[]
  state: LoadState
  errorMessage: string
  onRetry: () => void
}

export default function CourseList({ courses, state, errorMessage, onRetry }: CourseListProps) {
  if (state === 'loading') return <p className="status">Dang tai danh sach mon hoc...</p>
  if (state === 'error') {
    return <div className="status error"><p>{errorMessage}</p><button onClick={onRetry}>Thu lai</button></div>
  }
  if (state === 'empty') return <p className="status">Khong tim thay mon hoc nao phu hop.</p>

  return (
    <div className="table-wrap">
      <table>
        <thead><tr><th>Ten mon hoc</th><th>So tin chi</th><th>So cho con lai</th></tr></thead>
        <tbody>
          {courses.map((course) => (
            <tr key={course.id}>
              <td>{course.tenMonHoc}</td>
              <td>{course.soTinChi}</td>
              <td className={course.soChoConLai === 0 ? 'sold-out' : ''}>
                {course.soChoConLai} / {course.soChoToiDa}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}