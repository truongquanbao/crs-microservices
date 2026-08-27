import { useCallback, useState } from 'react'
import { useCourses } from './api/useCourses'
import CourseList from './components/CourseList'
import Pagination from './components/Pagination'
import SearchBox from './components/SearchBox'
import './App.css'

function App() {
  const [keyword, setKeyword] = useState('')
  const [page, setPage] = useState(0)
  const { courses, totalPages, state, errorMessage, refetch } = useCourses(keyword, page)

  const handleSearch = useCallback((newKeyword: string) => {
    setKeyword(newKeyword)
    setPage(0)
  }, [])

  return (
    <main className="course-page">
      <header className="page-header">
        <p className="eyebrow">CRS / COURSE CATALOG</p>
        <h1>Danh sach mon hoc</h1>
        <p className="subtitle">Tra cuu hoc phan va so cho dang con trong he thong.</p>
      </header>
      <SearchBox onSearch={handleSearch} />
      <section className="course-section" aria-live="polite">
        <CourseList courses={courses} state={state} errorMessage={errorMessage} onRetry={refetch} />
        <Pagination currentPage={page} totalPages={totalPages} onPageChange={setPage} />
      </section>
    </main>
  )
}

export default App
