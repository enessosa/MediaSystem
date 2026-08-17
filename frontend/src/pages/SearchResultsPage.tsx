import { useEffect, useState } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import { searchMedia, type MediaSearchResult } from '../api/media'
import { ApiError } from '../api/client'
import { useLibrary } from '../context/LibraryContext'
import type { CatalogItem } from '../types/media'
import MediaCard from '../components/MediaCard'
import AddToListModal from '../components/AddToListModal'
import EmptyState from '../components/EmptyState'

function toCatalogItem(result: MediaSearchResult): CatalogItem {
  return {
    id: `${result.sourceType.toLowerCase()}-${result.externalId}`,
    title: result.title,
    description: result.description,
    releaseYear: result.releaseYear,
    mediaType: result.mediaType,
    creator: result.creator,
    coverColor: '#3f5e6b',
    coverUrl: result.coverUrl,
  }
}

function SearchResultsPage() {
  const [searchParams] = useSearchParams()
  const query = searchParams.get('q')?.trim() ?? ''
  const { getEntryByItemId } = useLibrary()

  const [results, setResults] = useState<CatalogItem[]>([])
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (!query) {
      setResults([])
      setError(null)
      return
    }

    let cancelled = false
    setIsLoading(true)
    setError(null)

    searchMedia(query)
      .then((data) => {
        if (!cancelled) setResults(data.map(toCatalogItem))
      })
      .catch((err) => {
        if (!cancelled) {
          setError(err instanceof ApiError ? err.message : 'Suche fehlgeschlagen. Versuch es erneut.')
        }
      })
      .finally(() => {
        if (!cancelled) setIsLoading(false)
      })

    return () => {
      cancelled = true
    }
  }, [query])

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
          'Gib oben einen Suchbegriff ein.'
        )}
      </p>

      {isLoading && <p style={{ marginTop: 24 }}>Suche läuft…</p>}

      {error && (
        <div style={{ marginTop: 24 }}>
          <EmptyState
            icon="search"
            title="Suche fehlgeschlagen"
            description={error}
            action={
              <Link to="/search" className="btn" style={{ marginTop: 8 }}>
                Neue Suche
              </Link>
            }
          />
        </div>
      )}

      {!isLoading && !error && query && results.length === 0 && (
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
      )}

      {!isLoading && !error && results.length > 0 && (
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