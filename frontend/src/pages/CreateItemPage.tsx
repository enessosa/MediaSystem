import { useState, type FormEvent } from 'react'
import { useNavigate } from 'react-router-dom'
import { useLibrary } from '../context/LibraryContext'
import { ApiError } from '../api/client'
import { MEDIA_TYPE_LABELS, type MediaType } from '../types/media'

function CreateItemPage() {
  const { addEntry, updateEntry } = useLibrary()
  const navigate = useNavigate()

  const [title, setTitle] = useState('')
  const [mediaType, setMediaType] = useState<MediaType>('ANIME')
  const [note, setNote] = useState('')
  const [isSaving, setIsSaving] = useState(false)
  const [error, setError] = useState<string | null>(null)

  async function handleSubmit(e: FormEvent) {
    e.preventDefault()
    const trimmedTitle = title.trim()
    if (!trimmedTitle) return

    setIsSaving(true)
    setError(null)
    try {
      // SourceType.MANUAL erlaubt externalId: null (DB-Constraint chk_external_id_present).
      const entry = await addEntry({
        title: trimmedTitle,
        description: '',
        releaseYear: null,
        mediaType,
        creator: null,
        coverUrl: null,
        sourceType: 'MANUAL',
        externalId: null,
      })
      if (note.trim()) {
        await updateEntry(entry.entryId, { note: note.trim() })
      }
      navigate(`/media/${entry.entryId}`)
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Anlegen fehlgeschlagen. Versuch es erneut.')
    } finally {
      setIsSaving(false)
    }
  }

  return (
    <div>
      <h1>Medium anlegen</h1>
      <p style={{ marginTop: 8 }}>Manuelles Anlegen ohne externe Quelle (Source-Typ: MANUAL).</p>

      <form
        onSubmit={handleSubmit}
        className="card"
        style={{
          marginTop: 24,
          maxWidth: 520,
          display: 'flex',
          flexDirection: 'column',
          gap: 16,
        }}
      >
        <div className="field">
          <label htmlFor="ci-title">Titel</label>
          <input
            id="ci-title"
            className="input"
            type="text"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            required
          />
        </div>

        <div className="field">
          <label htmlFor="ci-type">Typ</label>
          <select
            id="ci-type"
            className="input"
            value={mediaType}
            onChange={(e) => setMediaType(e.target.value as MediaType)}
          >
            {Object.entries(MEDIA_TYPE_LABELS).map(([value, label]) => (
              <option key={value} value={value}>
                {label}
              </option>
            ))}
          </select>
        </div>

        <div className="field">
          <label htmlFor="ci-note">Notiz</label>
          <textarea id="ci-note" className="input" rows={3} value={note} onChange={(e) => setNote(e.target.value)} />
        </div>

        {error && <p style={{ color: 'var(--danger, #c0392b)' }}>{error}</p>}

        <button type="submit" className="btn btn--primary" disabled={isSaving}>
          {isSaving ? 'Legt an…' : 'Anlegen'}
        </button>
      </form>
    </div>
  )
}

export default CreateItemPage
