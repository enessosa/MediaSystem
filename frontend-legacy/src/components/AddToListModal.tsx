import { useState } from 'react'
import { STATUS_LABELS, type WatchStatus } from '../types/media'
import { useLibrary } from '../context/LibraryContext'
import { ApiError } from '../api/client'
import type { MediaSearchResult } from '../api/media'

const STATUS_OPTIONS: WatchStatus[] = ['PLANNED', 'WATCHING', 'COMPLETED', 'DROPPED']

interface AddToListModalProps {
  result: MediaSearchResult
  onClose: () => void
}

function AddToListModal({ result, onClose }: AddToListModalProps) {
  const { addEntry, updateEntry } = useLibrary()
  const [status, setStatus] = useState<WatchStatus>('PLANNED')
  const [isSaving, setIsSaving] = useState(false)
  const [error, setError] = useState<string | null>(null)

  async function handleAdd() {
    setIsSaving(true)
    setError(null)
    try {
      // Backend legt neue Einträge immer mit Status PLANNED an; ein abweichender
      // Status wird direkt danach per Update nachgezogen.
      const entry = await addEntry(result)
      if (status !== 'PLANNED') {
        await updateEntry(entry.entryId, { status })
      }
      onClose()
    } catch (err) {
      if (err instanceof ApiError && err.status === 409) {
        setError('Ist schon in deiner Liste.')
      } else {
        setError('Hinzufügen fehlgeschlagen. Versuch es erneut.')
      }
    } finally {
      setIsSaving(false)
    }
  }

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="card modal-content" onClick={(e) => e.stopPropagation()}>
        <h2>Zur Liste hinzufügen</h2>
        <p style={{ margin: '12px 0 16px' }}>
          <strong>{result.title}</strong> mit welchem Status anlegen?
        </p>
        <div className="field" style={{ marginBottom: 20 }}>
          <label htmlFor="add-status">Status</label>
          <select
            id="add-status"
            className="input"
            value={status}
            onChange={(e) => setStatus(e.target.value as WatchStatus)}
          >
            {STATUS_OPTIONS.map((s) => (
              <option key={s} value={s}>
                {STATUS_LABELS[s]}
              </option>
            ))}
          </select>
        </div>
        {error && (
          <p style={{ color: 'var(--danger, #c0392b)', marginBottom: 16 }}>{error}</p>
        )}
        <div style={{ display: 'flex', gap: 8 }}>
          <button type="button" className="btn" onClick={onClose} disabled={isSaving}>
            Abbrechen
          </button>
          <button type="button" className="btn btn--primary" onClick={handleAdd} disabled={isSaving}>
            {isSaving ? 'Speichert…' : 'Hinzufügen'}
          </button>
        </div>
      </div>
    </div>
  )
}

export default AddToListModal
