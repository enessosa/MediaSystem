import { useMemo, useState } from 'react'
import { Link } from 'react-router-dom'
import { useLibrary } from '../context/LibraryContext'
import { MEDIA_TYPE_LABELS, STATUS_LABELS, type MediaType, type WatchStatus } from '../types/media'
import MediaCard from '../components/MediaCard'
import EmptyState from '../components/EmptyState'

function WatchlistsPage() {
  const { entries } = useLibrary()
  const [typeFilter, setTypeFilter] = useState<MediaType | 'ALL'>('ALL')
  const [statusFilter, setStatusFilter] = useState<WatchStatus | 'ALL'>('ALL')

  const filtered = useMemo(
    () =>
      entries.filter(
        (e) =>
          (typeFilter === 'ALL' || e.item.mediaType === typeFilter) &&
          (statusFilter === 'ALL' || e.status === statusFilter),
      ),
    [entries, typeFilter, statusFilter],
  )

  return (
    <div>
      <h1>Watchlists</h1>
      <p style={{ marginTop: 8 }}>Liste öffnen, filtern und sortieren.</p>

      <div style={{ display: 'flex', gap: 8, marginTop: 24 }}>
        <select
          className="input"
          style={{ maxWidth: 160 }}
          value={typeFilter}
          onChange={(e) => setTypeFilter(e.target.value as MediaType | 'ALL')}
        >
          <option value="ALL">Alle Typen</option>
          {Object.entries(MEDIA_TYPE_LABELS).map(([value, label]) => (
            <option key={value} value={value}>
              {label}
            </option>
          ))}
        </select>
        <select
          className="input"
          style={{ maxWidth: 160 }}
          value={statusFilter}
          onChange={(e) => setStatusFilter(e.target.value as WatchStatus | 'ALL')}
        >
          <option value="ALL">Alle Status</option>
          {Object.entries(STATUS_LABELS).map(([value, label]) => (
            <option key={value} value={value}>
              {label}
            </option>
          ))}
        </select>
      </div>

      {entries.length === 0 ? (
        <div style={{ marginTop: 24 }}>
          <EmptyState
            icon="sparkles"
            title="Noch keine Einträge"
            description="Über die Suche oder Discovery etwas hinzufügen."
            action={
              <Link to="/search" className="btn btn--primary" style={{ marginTop: 8 }}>
                Medium suchen
              </Link>
            }
          />
        </div>
      ) : filtered.length === 0 ? (
        <div className="card" style={{ marginTop: 24 }}>
          Keine Einträge für diesen Filter.
        </div>
      ) : (
        <div
          style={{
            marginTop: 24,
            display: 'grid',
            gap: 16,
            gridTemplateColumns: 'repeat(auto-fill, minmax(180px, 1fr))',
          }}
        >
          {filtered.map((entry) => (
            <MediaCard key={entry.entryId} item={entry.item} status={entry.status} rating={entry.rating} />
          ))}
        </div>
      )}
    </div>
  )
}

export default WatchlistsPage
