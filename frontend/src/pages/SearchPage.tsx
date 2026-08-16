import { useState, type FormEvent } from 'react'
import { useNavigate } from 'react-router-dom'
import Icon from '../components/Icon'

function SearchPage() {
  const navigate = useNavigate()
  const [query, setQuery] = useState('')

  function handleSubmit(e: FormEvent) {
    e.preventDefault()
    const trimmed = query.trim()
    navigate(`/search/results${trimmed ? `?q=${encodeURIComponent(trimmed)}` : ''}`)
  }

  return (
    <div>
      <h1>Suche</h1>
      <p style={{ marginTop: 8 }}>Medium über externe Quellen suchen (AniList, TMDB, OpenLibrary).</p>

      <form onSubmit={handleSubmit} style={{ display: 'flex', gap: 8, marginTop: 24, maxWidth: 480 }}>
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
      </form>
    </div>
  )
}

export default SearchPage
