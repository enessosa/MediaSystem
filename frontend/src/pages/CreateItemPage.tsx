import { useState, type FormEvent } from 'react'
import { useNavigate } from 'react-router-dom'
import { useLibrary } from '../context/LibraryContext'
import { MEDIA_TYPE_LABELS, type CatalogItem, type MediaType } from '../types/media'

const COLORS = ['#5b2f6b', '#3f5e6b', '#8a4b1f', '#2f6b4f', '#7a1f2b']

function CreateItemPage() {
  const { addEntry, updateEntry, getEntryByItemId } = useLibrary()
  const navigate = useNavigate()

  const [title, setTitle] = useState('')
  const [mediaType, setMediaType] = useState<MediaType>('ANIME')
  const [note, setNote] = useState('')

  function handleSubmit(e: FormEvent) {
    e.preventDefault()
    const trimmedTitle = title.trim()
    if (!trimmedTitle) return

    const item: CatalogItem = {
      id: `manual-${crypto.randomUUID()}`,
      title: trimmedTitle,
      description: '',
      releaseYear: null,
      mediaType,
      creator: null,
      coverColor: COLORS[Math.floor(Math.random() * COLORS.length)],
    }

    addEntry(item, 'PLANNED')
    if (note.trim()) {
      const entry = getEntryByItemId(item.id)
      if (entry) updateEntry(entry.entryId, { note: note.trim() })
    }
    navigate(`/media/${item.id}`)
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

        <button type="submit" className="btn btn--primary">
          Anlegen
        </button>
      </form>
    </div>
  )
}

export default CreateItemPage