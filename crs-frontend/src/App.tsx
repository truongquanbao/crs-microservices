import { useEffect, useState } from 'react'
import { getCourses } from './api/courseApi'
import type { Course } from './types/course'
import './App.css'

function App() {
  const [courses, setCourses] = useState<Course[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    getCourses()
      .then((response) => setCourses(response.data.content))
      .catch((requestError: unknown) => {
        console.error(requestError)
        setError('Khong ket noi duoc toi he thong. Kiem tra lai api gateway da chay chua.')
      })
      .finally(() => setLoading(false))
  }, [])

  return (
    <div className="connection-check" aria-live="polite">
      <h1>Kiem tra ket noi CRS qua Gateway</h1>
      {loading && <p>Dang tai du lieu...</p>}
      {error && <p className="error">{error}</p>}
      {!loading && !error && <pre>{JSON.stringify(courses, null, 2)}</pre>}
    </div>
  )
}

export default App
