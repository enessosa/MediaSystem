import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { CATALOG } from '../data/catalog'
import { useLibrary } from '../context/LibraryContext'
import { MEDIA_TYPE_LABELS, STATUS_LABELS, type WatchStatus } from '../types/media'
import Icon from '../components/Icon'
import EmptyState from '../components/EmptyState'
import AddToListModal from '../components/AddToListModal'

const STATUS_OPTIONS: WatchStatus[] = ['PLANNED', 'WATCHING', 'COMPLETED', 'DROPPED']

function MediaDetailPage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const { getEntryByItemId, updateEntry, removeEntry } = useLibrary()

  const item = CATALOG.find((c) => c.id === id)
  const entry = id ? getEntryByItemId(id) : undefined

  const [status, setStatus] = useState<WatchStatus>(entry?.status ?? 'PLANNED')
  const [rating, setRating] = useState(entry?.rating?.toString() ?? '')
  const [note, setNote] = useState(entry?.note ?? '')
  const [showAddModal, setShowAddModal] = useState(false)

  useEffect(() => {
    if (entry) {
      setStatus(entry.status)
      setRating(entry.rating?.toString() ?? '')
      setNote(entry.note)
    }
  }, [entry])

  if (!item) {
    return (
      <EmptyState
        icon="search"
        title="Eintrag nicht gefunden"
        description="Dieses Medium existiert nicht (mehr)."
        action={
          <Link to="/discovery" className="btn btn--primary" style={{ marginTop: 8 }}>
            Zur Discovery
          </Link>
        }
      />
    )
  }

  function handleSave() {
    if (!entry) return
    updateEntry(entry.entryId, {
      status,
      rating: rating.trim() === '' ? null : Number(rating),
      note,
    })
  }

  function handleDelete() {
    if (!entry) return
    removeEntry(entry.entryId)
    navigate('/watchlists')
  }

  return (
    <div>
      <div style={{ display: 'flex', gap: 20, alignItems: 'flex-start', flexWrap: 'wrap' }}>
        <div
          style={{
            width: 140,
            aspectRatio: '2 / 3',
            borderRadius: 'var(--radius)',
            background: item.coverColor,
            flexShrink: 0,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            color: '#fff',
          }}
        >
          <Icon name="clapper" size={36} />
        </div>

        <div style={{ flex: 1, minWidth: 240 }}>
          <h1>{item.title}</h1>
          <p style={{ marginTop: 6, fontSize: 14 }}>
            {MEDIA_TYPE_LABELS[item.mediaType]}
            {item.releaseYear && ` · ${item.releaseYear}`}
            {item.creator && ` · ${item.creator}`}
          </p>
          <p style={{ marginTop: 16, maxWidth: 640 }}>{item.description}</p>

          {!entry && (
            <button
              type="button"
              className="btn btn--primary"
              style={{ marginTop: 20 }}
              onClick={() => setShowAddModal(true)}
            >
              Zur Liste hinzufügen
            </button>
          )}
        </div>
      </div>

      {entry && (
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
            <label htmlFor="md-rating">Rating (0-10)</label>
            <input
              id="md-rating"
              className="input"
              type="number"
              min={0}
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

          <div style={{ display: 'flex', gap: 8 }}>
            <button type="button" className="btn btn--primary" onClick={handleSave}>
              Speichern
            </button>
            <button type="button" className="btn btn--danger" onClick={handleDelete}>
              Eintrag löschen
            </button>
          </div>
        </div>
      )}

      {showAddModal && <AddToListModal item={item} onClose={() => setShowAddModal(false)} />}
    </div>
  )
}

export default MediaDetailPage