import { useEffect, useState } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import { searchMedia, type MediaSearchResult } from '../api/media'
import { ApiError } from '../api/client'
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
    coverUrl: result.coverUrl ?? undefined,
  }
}

function SearchResultsPage() {
  const [searchParams] = useSearchParams()
  const query = searchParams.get('q')?.trim() ?? ''

  const [results, setResults] = useState<MediaSearchResult[]>([])
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (!query) {
      return
    }

    let cancelled = false
    // Datenabruf ist der von React dokumentierte legitime Anwendungsfall für Effects
    // (https://react.dev/learn/you-might-not-need-an-effect#fetching-data) - Lade-/Fehlerzustand
    // muss hier direkt gesetzt werden, bevor der eigentliche Fetch losläuft.
    // eslint-disable-next-line react-hooks/set-state-in-effect
    setIsLoading(true)
    setError(null)

    searchMedia(query)
      .then((data) => {
        if (!cancelled) setResults(data)
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

  const [selectedResult, setSelectedResult] = useState<MediaSearchResult | null>(null)

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

      {query && error && (
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

      {!isLoading && !error && query && results.length > 0 && (
        <div
          style={{
            marginTop: 24,
            display: 'grid',
            gap: 16,
            gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))',
          }}
        >
          {results.map((result) => (
            <MediaCard
              key={`${result.sourceType}-${result.externalId}`}
              item={toCatalogItem(result)}
              action={
                <button
                  type="button"
                  className="btn btn--primary"
                  style={{ width: '100%' }}
                  onClick={() => setSelectedResult(result)}
                >
                  Zur Liste hinzufügen
                </button>
              }
            />
          ))}
        </div>
      )}

      {selectedResult && <AddToListModal result={selectedResult} onClose={() => setSelectedResult(null)} />}
    </div>
  )
}

export default SearchResultsPage
