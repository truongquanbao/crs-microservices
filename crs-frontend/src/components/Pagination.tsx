interface PaginationProps {
  currentPage: number
  totalPages: number
  onPageChange: (page: number) => void
}

export default function Pagination({ currentPage, totalPages, onPageChange }: PaginationProps) {
  if (totalPages <= 1) return null

  return (
    <nav className="pagination" aria-label="Dieu huong trang">
      <button disabled={currentPage === 0} onClick={() => onPageChange(currentPage - 1)}>
        Trang truoc
      </button>
      {Array.from({ length: totalPages }, (_, page) => (
        <button
          className={page === currentPage ? 'active' : ''}
          key={page}
          aria-current={page === currentPage ? 'page' : undefined}
          onClick={() => onPageChange(page)}
        >
          {page + 1}
        </button>
      ))}
      <button disabled={currentPage >= totalPages - 1} onClick={() => onPageChange(currentPage + 1)}>
        Trang sau
      </button>
    </nav>
  )
}