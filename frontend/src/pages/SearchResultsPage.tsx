import { useMemo, useState } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import { CATALOG } from '../data/catalog'
import { useLibrary } from '../context/LibraryContext'
import type { CatalogItem } from '../types/media'
import MediaCard from '../components/MediaCard'
import AddToListModal from '../components/AddToListModal'
import EmptyState from '../components/EmptyState'

function SearchResultsPage() {
  const [searchParams] = useSearchParams()
  const query = searchParams.get('q')?.trim() ?? ''
  const { getEntryByItemId } = useLibrary()

  // Simuliert Treffer aus den Provider-APIs, bis der echte Such-Endpoint steht (AniListProvider ist backend-seitig schon fertig).
  const results = useMemo(
    () =>
      query
        ? CATALOG.filter((item) => item.title.toLowerCase().includes(query.toLowerCase()))
        : CATALOG,
    [query],
  )

  const [selectedItem, setSelectedItem] = useState<CatalogItem | null>(null)

  return (
    <div>
      <h1>Suchergebnisse</h1>
      <p style={{ marginTop: 8 }}>
        {query ? (
          <>
            Treffer für <strong>„{query}“</strong>
          </>
        ) : (
          'Treffer aus den Provider-APIs.'
        )}
      </p>

      {results.length === 0 ? (
        <div style={{ marginTop: 24 }}>
          <EmptyState
            icon="search"
            title="Keine Treffer"
            description="Versuch einen anderen Suchbegriff."
            action={
              <Link to="/search" className="btn" style={{ marginTop: 8 }}>
                Neue Suche
              </Link>
            }
          />
        </div>
      ) : (
        <div
          style={{
            marginTop: 24,
            display: 'grid',
            gap: 16,
            gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))',
          }}
        >
          {results.map((item) => {
            const existingEntry = getEntryByItemId(item.id)
            return (
              <MediaCard
                key={item.id}
                item={item}
                status={existingEntry?.status}
                action={
                  existingEntry ? (
                    <span style={{ fontSize: 13, fontWeight: 600, color: 'var(--text)' }}>Schon in deiner Liste</span>
                  ) : (
                    <button
                      type="button"
                      className="btn btn--primary"
                      style={{ width: '100%' }}
                      onClick={() => setSelectedItem(item)}
                    >
                      Zur Liste hinzufügen
                    </button>
                  )
                }
              />
            )
          })}
        </div>
      )}

      {selectedItem && <AddToListModal item={selectedItem} onClose={() => setSelectedItem(null)} />}
    </div>
  )
}

export default SearchResultsPage
