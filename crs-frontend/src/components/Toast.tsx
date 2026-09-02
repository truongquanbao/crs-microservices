import { useEffect } from 'react'

interface ToastProps {
  message: string
  type: 'success' | 'error'
  onClose: () => void
}

export default function Toast({ message, type, onClose }: ToastProps) {
  useEffect(() => {
    const timer = setTimeout(onClose, 3000)
    return () => clearTimeout(timer)
  }, [onClose])

  return (
    <div
      style={{
        position: 'fixed',
        top: 16,
        right: 16,
        padding: '12px 16px',
        borderRadius: 4,
        backgroundColor: type === 'success' ? '#10b981' : '#ef4444',
        color: 'white',
        zIndex: 9999,
      }}
    >
      {message}
    </div>
  )
}
