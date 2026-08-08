function DiscoveryPage() {
  return (
    <div>
      <h1>Discovery</h1>
      <p style={{ marginTop: 8 }}>Empfehlungen &amp; Stöbern.</p>

      <div
        style={{
          marginTop: 24,
          display: 'grid',
          gap: 16,
          gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))',
        }}
      >
        <div className="card">Empfehlung 1</div>
        <div className="card">Empfehlung 2</div>
        <div className="card">Empfehlung 3</div>
      </div>
    </div>
  )
}

export default DiscoveryPage