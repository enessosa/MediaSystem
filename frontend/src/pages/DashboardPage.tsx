import { Link } from 'react-router-dom'
import { useLibrary } from '../context/LibraryContext'
import MediaCard from '../components/MediaCard'
import EmptyState from '../components/EmptyState'

function DashboardPage() {
  const { entries } = useLibrary()

  const recentlyUpdated = [...entries]
    .sort((a, b) => b.updatedAt.localeCompare(a.updatedAt))
    .slice(0, 4)
  const upNext = entries.filter((e) => e.status === 'PLANNED').slice(0, 4)

  const stats = {
    total: entries.length,
    watching: entries.filter((e) => e.status === 'WATCHING').length,
    completed: entries.filter((e) => e.status === 'COMPLETED').length,
    planned: entries.filter((e) => e.status === 'PLANNED').length,
  }

  return (
    <div>
      <h1>Dashboard</h1>
      <p style={{ marginTop: 8 }}>Übersicht &amp; Einstieg.</p>

      <div
        style={{
          marginTop: 24,
          display: 'grid',
          gap: 12,
          gridTemplateColumns: 'repeat(auto-fill, minmax(140px, 1fr))',
        }}
      >
        <div className="card">
          <div style={{ fontSize: 26, fontWeight: 700, color: 'var(--text-h)' }}>{stats.total}</div>
          <div style={{ fontSize: 13, marginTop: 4 }}>In deiner Liste</div>
        </div>
        <div className="card">
          <div style={{ fontSize: 26, fontWeight: 700, color: 'var(--text-h)' }}>{stats.watching}</div>
          <div style={{ fontSize: 13, marginTop: 4 }}>Watching</div>
        </div>
        <div className="card">
          <div style={{ fontSize: 26, fontWeight: 700, color: 'var(--text-h)' }}>{stats.completed}</div>
          <div style={{ fontSize: 13, marginTop: 4 }}>Completed</div>
        </div>
        <div className="card">
          <div style={{ fontSize: 26, fontWeight: 700, color: 'var(--text-h)' }}>{stats.planned}</div>
          <div style={{ fontSize: 13, marginTop: 4 }}>Planned</div>
        </div>
      </div>

      <section style={{ marginTop: 40 }}>
        <h2>Zuletzt aktualisiert</h2>
        {recentlyUpdated.length === 0 ? (
          <EmptyState
            icon="sparkles"
            title="Noch nichts in deiner Liste"
            description="Sobald du etwas hinzufügst, taucht es hier auf."
            action={
              <Link to="/search" className="btn btn--primary" style={{ marginTop: 8 }}>
                Medium suchen
              </Link>
            }
          />
        ) : (
          <div
            style={{
              marginTop: 16,
              display: 'grid',
              gap: 16,
              gridTemplateColumns: 'repeat(auto-fill, minmax(180px, 1fr))',
            }}
          >
            {recentlyUpdated.map((entry) => (
              <MediaCard key={entry.entryId} item={entry.item} status={entry.status} rating={entry.rating} />
            ))}
          </div>
        )}
      </section>

      {upNext.length > 0 && (
        <section style={{ marginTop: 40 }}>
          <h2>Als Nächstes dran</h2>
          <div
            style={{
              marginTop: 16,
              display: 'grid',
              gap: 16,
              gridTemplateColumns: 'repeat(auto-fill, minmax(180px, 1fr))',
            }}
          >
            {upNext.map((entry) => (
              <MediaCard key={entry.entryId} item={entry.item} status={entry.status} rating={entry.rating} />
            ))}
          </div>
        </section>
      )}
    </div>
  )
}

export default DashboardPage