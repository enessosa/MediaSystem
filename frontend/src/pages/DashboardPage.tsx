function DashboardPage() {
  return (
    <div>
      <h1>Dashboard</h1>
      <p style={{ marginTop: 8 }}>Übersicht &amp; Einstieg.</p>

      <div
        style={{
          marginTop: 24,
          display: 'grid',
          gap: 16,
          gridTemplateColumns: 'repeat(auto-fill, minmax(220px, 1fr))',
        }}
      >
        <div className="card">Zuletzt aktualisiert</div>
        <div className="card">Als Nächstes dran</div>
        <div className="card">Statistiken</div>
      </div>
    </div>
  )
}

export default DashboardPage