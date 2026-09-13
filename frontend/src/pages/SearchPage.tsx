import { useState, type FormEvent } from 'react'
import { useNavigate } from 'react-router-dom'
import Icon from '../components/Icon'
import MediaTypeFilter from '../components/MediaTypeFilter'
import type { MediaType } from '../types/media'

function SearchPage() {
  const navigate = useNavigate()
  const [query, setQuery] = useState('')
  const [selectedTypes, setSelectedTypes] = useState<MediaType[]>([])

  function toggleType(type: MediaType) {
    setSelectedTypes((prev) => (prev.includes(type) ? prev.filter((t) => t !== type) : [...prev, type]))
  }

  function handleSubmit(e: FormEvent) {
    e.preventDefault()
    const trimmed = query.trim()
    if (!trimmed) {
      navigate('/search/results')
      return
    }
    const params = new URLSearchParams({ q: trimmed })
    if (selectedTypes.length > 0) {
      params.set('mediaTypes', selectedTypes.join(','))
    }
    navigate(`/search/results?${params.toString()}`)
  }

  return (
    <div>
      <h1>Suche</h1>
      <p style={{ marginTop: 8 }}>Medium über externe Quellen suchen (AniList, TMDB, RAWG, OpenLibrary).</p>

      <form onSubmit={handleSubmit} style={{ marginTop: 24, maxWidth: 480 }}>
        <div style={{ display: 'flex', gap: 8 }}>
          <input
            className="input"
            placeholder="Titel eingeben…"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            autoFocus
          />
          <button type="submit" className="btn btn--primary" style={{ display: 'inline-flex', gap: 6 }}>
            <Icon name="search" size={16} />
            Suchen
          </button>
        </div>

        <div style={{ marginTop: 16 }}>
          <MediaTypeFilter selected={selectedTypes} onToggle={toggleType} />
        </div>
      </form>
    </div>
  )
}

export default SearchPage
