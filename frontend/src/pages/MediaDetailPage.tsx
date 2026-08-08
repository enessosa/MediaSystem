import { useParams } from 'react-router-dom'

function MediaDetailPage() {
  // ":id" aus der URL – z.B. /media/42
  const { id } = useParams()

  return (
    <div>
      <h1>Media Detail</h1>
      <p style={{ marginTop: 8 }}>Eintrag #{id}</p>

      {/* In dieser Scene: edit item, updateStatus, updateRating,
          Notiz bearbeiten, delete item. */}
      <div className="card" style={{ marginTop: 24 }}>
        <div className="field" style={{ marginBottom: 16 }}>
          <label>Status</label>
          <select className="input">
            <option>Watching</option>
            <option>Completed</option>
            <option>Planned</option>
            <option>Dropped</option>
          </select>
        </div>

        <div className="field" style={{ marginBottom: 16 }}>
          <label>Rating</label>
          <input className="input" type="number" min={0} max={10} />
        </div>

        <div className="field" style={{ marginBottom: 16 }}>
          <label>Notiz</label>
          <textarea className="input" rows={3} />
        </div>

        <div style={{ display: 'flex', gap: 8 }}>
          <button type="button" className="btn btn--primary">
            Speichern
          </button>
          <button type="button" className="btn btn--danger">
            Eintrag löschen
          </button>
        </div>
      </div>
    </div>
  )
}

export default MediaDetailPage