import { useState } from 'react'

function SearchResultsPage() {
  // Add-to-List-Modal = State über dieser Scene, kein eigener Screen
  const [modalOpen, setModalOpen] = useState(false)

  return (
    <div>
      <h1>Suchergebnisse</h1>
      {/* States dieser Scene: Rate Limit / API down / keine Treffer */}
      <p style={{ marginTop: 8 }}>Treffer aus den Provider-APIs.</p>

      <div
        style={{
          marginTop: 24,
          display: 'grid',
          gap: 16,
          gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))',
        }}
      >
        <div className="card">
          <h3>Beispiel-Treffer</h3>
          <button
            type="button"
            className="btn btn--primary"
            style={{ marginTop: 12 }}
            onClick={() => setModalOpen(true)}
          >
            Zur Liste hinzufügen
          </button>
        </div>
      </div>

      {modalOpen && (
        <div className="modal-overlay" onClick={() => setModalOpen(false)}>
          <div
            className="card modal-content"
            onClick={(e) => e.stopPropagation()}
          >
            <h2>Zur Liste hinzufügen</h2>
            <p style={{ margin: '12px 0' }}>Status wählen, dann speichern.</p>
            <div style={{ display: 'flex', gap: 8 }}>
              <button
                type="button"
                className="btn"
                onClick={() => setModalOpen(false)}
              >
                Abbrechen
              </button>
              <button type="button" className="btn btn--primary">
                Hinzufügen
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default SearchResultsPage