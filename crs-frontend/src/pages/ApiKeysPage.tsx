import axios from 'axios'
import { useCallback, useEffect, useState } from 'react'
import { createApiKey, getApiKeys, revokeApiKey } from '../api/apiKeyApi'
import Toast from '../components/Toast'
import { useToast } from '../hooks/useToast'
import type { ApiErrorResponse } from '../types/apiError'
import type { ApiKey } from '../types/apiKey'

const dateTime = (value: string | null) =>
  value ? new Date(value).toLocaleString('vi-VN') : 'Khong het han'

const errorMessage = (error: unknown) => {
  if (axios.isAxiosError<ApiErrorResponse>(error)) {
    return error.response?.data?.message ?? 'Khong the xu ly yeu cau.'
  }
  return 'Da xay ra loi, vui long thu lai.'
}

export default function ApiKeysPage() {
  const [apiKeys, setApiKeys] = useState<ApiKey[]>([])
  const [ownerName, setOwnerName] = useState('')
  const [scopes, setScopes] = useState('courses:read')
  const [validDays, setValidDays] = useState('30')
  const [newKeyValue, setNewKeyValue] = useState<string | null>(null)
  const [loading, setLoading] = useState(true)
  const [submitting, setSubmitting] = useState(false)
  const { toast, showToast, clearToast } = useToast()

  const loadApiKeys = useCallback(async () => {
    try {
      const response = await getApiKeys()
      setApiKeys(response.data)
    } catch (error) {
      showToast(errorMessage(error), 'error')
    } finally {
      setLoading(false)
    }
  }, [showToast])

  useEffect(() => {
    // oxlint-disable-next-line react/set-state-in-effect -- load remote data on mount
    void loadApiKeys()
  }, [loadApiKeys])

  const handleCreate = async (event: React.FormEvent) => {
    event.preventDefault()
    setSubmitting(true)
    setNewKeyValue(null)
    try {
      const response = await createApiKey({
        ownerName,
        scopes,
        validDays: validDays ? Number(validDays) : undefined,
      })
      setNewKeyValue(response.data.keyValue ?? null)
      setOwnerName('')
      await loadApiKeys()
      showToast('Da tao API key. Hay sao chep key ngay bay gio.', 'success')
    } catch (error) {
      showToast(errorMessage(error), 'error')
    } finally {
      setSubmitting(false)
    }
  }

  const handleRevoke = async (apiKey: ApiKey) => {
    if (!window.confirm(`Thu hoi API key cua "${apiKey.ownerName}"?`)) return
    try {
      await revokeApiKey(apiKey.id)
      await loadApiKeys()
      showToast('Da thu hoi API key.', 'success')
    } catch (error) {
      showToast(errorMessage(error), 'error')
    }
  }

  const copyNewKey = async () => {
    if (!newKeyValue) return
    try {
      await navigator.clipboard.writeText(newKeyValue)
      showToast('Da sao chep API key.', 'success')
    } catch {
      showToast('Khong the sao chep tu dong. Hay sao chep thu cong.', 'error')
    }
  }

  return (
    <main style={{ padding: 24, maxWidth: 1100, margin: '0 auto' }}>
      <h1>Quan ly API Key</h1>
      <p>Cap key cho doi tac va gioi han quyen truy cap theo scope.</p>

      <form
        onSubmit={handleCreate}
        style={{ display: 'grid', gap: 12, padding: 16, background: '#fff', borderRadius: 8 }}
      >
        <label>
          Ten doi tac
          <input
            required
            value={ownerName}
            onChange={(event) => setOwnerName(event.target.value)}
            style={{ display: 'block', width: '100%', padding: 8 }}
          />
        </label>
        <label>
          Scopes (phan cach bang dau phay)
          <input
            required
            value={scopes}
            onChange={(event) => setScopes(event.target.value)}
            placeholder="courses:read,courses:read-detail"
            style={{ display: 'block', width: '100%', padding: 8 }}
          />
        </label>
        <label>
          So ngay hieu luc (de trong neu khong het han)
          <input
            type="number"
            min="1"
            value={validDays}
            onChange={(event) => setValidDays(event.target.value)}
            style={{ display: 'block', width: '100%', padding: 8 }}
          />
        </label>
        <button type="submit" disabled={submitting} style={{ width: 'fit-content' }}>
          {submitting ? 'Dang tao...' : 'Tao API Key'}
        </button>
      </form>

      {newKeyValue && (
        <section
          role="status"
          style={{ marginTop: 16, padding: 16, background: '#dcfce7', border: '1px solid #16a34a' }}
        >
          <strong>API key moi - chi hien thi lan nay:</strong>
          <code style={{ display: 'block', margin: '10px 0', wordBreak: 'break-all' }}>
            {newKeyValue}
          </code>
          <button type="button" onClick={copyNewKey}>Sao chep</button>
          <button type="button" onClick={() => setNewKeyValue(null)} style={{ marginLeft: 8 }}>
            Da luu key
          </button>
        </section>
      )}

      <h2 style={{ marginTop: 28 }}>Danh sach API Key</h2>
      {loading ? (
        <p>Dang tai...</p>
      ) : apiKeys.length === 0 ? (
        <p>Chua co API key nao.</p>
      ) : (
        <div style={{ overflowX: 'auto' }}>
          <table style={{ width: '100%', borderCollapse: 'collapse', background: '#fff' }}>
            <thead>
              <tr>
                {['ID', 'Doi tac', 'Scopes', 'Trang thai', 'Het han', 'Ngay tao', 'Thao tac'].map((title) => (
                  <th key={title} style={{ textAlign: 'left', padding: 10, borderBottom: '1px solid #cbd5e1' }}>
                    {title}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody>
              {apiKeys.map((apiKey) => (
                <tr key={apiKey.id}>
                  <td style={{ padding: 10 }}>{apiKey.id}</td>
                  <td style={{ padding: 10 }}>{apiKey.ownerName}</td>
                  <td style={{ padding: 10 }}><code>{apiKey.scopes}</code></td>
                  <td style={{ padding: 10 }}>{apiKey.status}</td>
                  <td style={{ padding: 10 }}>{dateTime(apiKey.expiresAt)}</td>
                  <td style={{ padding: 10 }}>{dateTime(apiKey.createdAt)}</td>
                  <td style={{ padding: 10 }}>
                    <button
                      type="button"
                      disabled={apiKey.status === 'REVOKED'}
                      onClick={() => void handleRevoke(apiKey)}
                    >
                      Thu hoi
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
      {toast && <Toast message={toast.message} type={toast.type} onClose={clearToast} />}
    </main>
  )
}
