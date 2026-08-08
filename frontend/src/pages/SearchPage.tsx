import { useNavigate } from 'react-router-dom'

function SearchPage() {
  const navigate = useNavigate()

  return (
    <div>
      <h1>Suche</h1>
      <p style={{ marginTop: 8 }}>Medium über externe Quellen suchen.</p>

      <div style={{ display: 'flex', gap: 8, marginTop: 24 }}>
        <input className="input" placeholder="Titel eingeben…" />
        <button
          type="button"
          className="btn btn--primary"
          onClick={() => navigate('/search/results')}
        >
          Suchen
        </button>
      </div>
    </div>
  )
}

export default SearchPage