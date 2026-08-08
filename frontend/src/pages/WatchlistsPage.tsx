function WatchlistsPage() {
  return (
    <div>
      <h1>Watchlists</h1>
      <p style={{ marginTop: 8 }}>Liste öffnen, filtern und sortieren.</p>

      {/* Filter/Sort-Leiste (kein eigener Screen, State dieser Scene) */}
      <div style={{ display: 'flex', gap: 8, marginTop: 24 }}>
        <select className="input" style={{ maxWidth: 160 }}>
          <option>Alle Typen</option>
          <option>Anime</option>
          <option>Manga</option>
          <option>Buch</option>
          <option>Serie</option>
        </select>
        <select className="input" style={{ maxWidth: 160 }}>
          <option>Alle Status</option>
          <option>Watching</option>
          <option>Completed</option>
          <option>Planned</option>
          <option>Dropped</option>
        </select>
      </div>

      {/* Leerzustand als State dieser Scene */}
      <div className="card" style={{ marginTop: 24 }}>
        Noch keine Einträge – über die Suche etwas hinzufügen.
      </div>
    </div>
  )
}

export default WatchlistsPage