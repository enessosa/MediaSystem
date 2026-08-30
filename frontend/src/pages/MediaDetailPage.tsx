import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { useLibrary } from '../context/LibraryContext'
import { ApiError } from '../api/client'
import { MEDIA_TYPE_LABELS, STATUS_LABELS, type WatchStatus } from '../types/media'
import Icon from '../components/Icon'
import EmptyState from '../components/EmptyState'

const STATUS_OPTIONS: WatchStatus[] = ['PLANNED', 'WATCHING', 'COMPLETED', 'DROPPED']

function MediaDetailPage() {
  const { id } = useParams()
  const { getEntry, updateEntry } = useLibrary()

  const entry = id ? getEntry(id) : undefined

  const [status, setStatus] = useState<WatchStatus>(entry?.status ?? 'PLANNED')
  const [rating, setRating] = useState(entry?.rating?.toString() ?? '')
  const [note, setNote] = useState(entry?.note ?? '')
  const [isSaving, setIsSaving] = useState(false)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (entry) {
      setStatus(entry.status)
      setRating(entry.rating?.toString() ?? '')
      setNote(entry.note)
    }
  }, [entry])

  if (!entry) {
    return (
      <EmptyState
        icon="search"
        title="Eintrag nicht gefunden"
        description="Dieser Eintrag existiert nicht (mehr) in deiner Liste."
        action={
          <Link to="/watchlists" className="btn btn--primary" style={{ marginTop: 8 }}>
            Zu den Watchlists
          </Link>
        }
      />
    )
  }

  const item = entry.item

  async function handleSave() {
    setIsSaving(true)
    setError(null)
    try {
      await updateEntry(entry!.entryId, {
        status,
        rating: rating.trim() === '' ? null : Number(rating),
        note,
      })
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Speichern fehlgeschlagen. Versuch es erneut.')
    } finally {
      setIsSaving(false)
    }
  }

  return (
    <div>
      <div style={{ display: 'flex', gap: 20, alignItems: 'flex-start', flexWrap: 'wrap' }}>
        <div
          style={{
            width: 140,
            aspectRatio: '2 / 3',
            borderRadius: 'var(--radius)',
            background: item.coverUrl ? undefined : item.coverColor,
            flexShrink: 0,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            color: '#fff',
            overflow: 'hidden',
          }}
        >
          {item.coverUrl ? (
            <img src={item.coverUrl} alt="" style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
          ) : (
            <Icon name="clapper" size={36} />
          )}
        </div>

        <div style={{ flex: 1, minWidth: 240 }}>
          <h1>{item.title}</h1>
          <p style={{ marginTop: 6, fontSize: 14 }}>
            {MEDIA_TYPE_LABELS[item.mediaType]}
            {item.releaseYear && ` · ${item.releaseYear}`}
            {item.creator && ` · ${item.creator}`}
          </p>
          <p style={{ marginTop: 16, maxWidth: 640 }}>{item.description}</p>
        </div>
      </div>

      <div className="card" style={{ marginTop: 32, maxWidth: 480 }}>
        <div className="field" style={{ marginBottom: 16 }}>
          <label htmlFor="md-status">Status</label>
          <select
            id="md-status"
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

        <div className="field" style={{ marginBottom: 16 }}>
          <label htmlFor="md-rating">Rating (1-10)</label>
          <input
            id="md-rating"
            className="input"
            type="number"
            min={1}
            max={10}
            value={rating}
            onChange={(e) => setRating(e.target.value)}
          />
        </div>

        <div className="field" style={{ marginBottom: 16 }}>
          <label htmlFor="md-note">Notiz</label>
          <textarea
            id="md-note"
            className="input"
            rows={3}
            value={note}
            onChange={(e) => setNote(e.target.value)}
          />
        </div>

        {error && <p style={{ color: 'var(--danger, #c0392b)', marginBottom: 16 }}>{error}</p>}

        <button type="button" className="btn btn--primary" onClick={handleSave} disabled={isSaving}>
          {isSaving ? 'Speichert…' : 'Speichern'}
        </button>
      </div>
    </div>
  )
}

export default MediaDetailPage
