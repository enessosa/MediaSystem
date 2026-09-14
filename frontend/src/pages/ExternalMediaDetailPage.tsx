import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { fetchMediaDetail, type MediaSearchResult, type SourceType } from '../api/media'
import { ApiError } from '../api/client'
import { MEDIA_TYPE_LABELS } from '../types/media'
import Icon from '../components/Icon'
import EmptyState from '../components/EmptyState'
import AddToListModal from '../components/AddToListModal'

/**
 * Detailansicht für einen externen Treffer, der noch nicht in der eigenen Liste ist
 * (Aufruf per sourceType+externalId direkt gegen den passenden Provider, GET /media/items).
 * Getrennt von MediaDetailPage, die ausschließlich schon gespeicherte UserEntry-Einträge zeigt.
 */
function ExternalMediaDetailPage() {
  const { sourceType, externalId } = useParams()

  const [result, setResult] = useState<MediaSearchResult | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [notFound, setNotFound] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [showAddModal, setShowAddModal] = useState(false)

  useEffect(() => {
    if (!sourceType || !externalId) {
      return
    }

    let cancelled = false
    // Datenabruf ist der von React dokumentierte legitime Anwendungsfall für Effects
    // (https://react.dev/learn/you-might-not-need-an-effect#fetching-data) - Lade-/Fehlerzustand
    // muss hier direkt gesetzt werden, bevor der eigentliche Fetch losläuft.
    // eslint-disable-next-line react-hooks/set-state-in-effect
    setIsLoading(true)
    setNotFound(false)
    setError(null)

    fetchMediaDetail(sourceType as SourceType, externalId)
      .then((data) => {
        if (!cancelled) setResult(data)
      })
      .catch((err) => {
        if (cancelled) return
        if (err instanceof ApiError && err.status === 404) {
          setNotFound(true)
        } else {
          setError(err instanceof ApiError ? err.message : 'Laden fehlgeschlagen. Versuch es erneut.')
        }
      })
      .finally(() => {
        if (!cancelled) setIsLoading(false)
      })

    return () => {
      cancelled = true
    }
  }, [sourceType, externalId])

  if (isLoading) {
    return <p>Lädt…</p>
  }

  if (notFound) {
    return (
      <EmptyState
        icon="search"
        title="Nicht gefunden"
        description="Dieses Medium konnte beim Anbieter nicht (mehr) gefunden werden."
        action={
          <Link to="/search" className="btn btn--primary" style={{ marginTop: 8 }}>
            Neue Suche
          </Link>
        }
      />
    )
  }

  if (error || !result) {
    return (
      <EmptyState
        icon="search"
        title="Laden fehlgeschlagen"
        description={error ?? 'Unbekannter Fehler.'}
        action={
          <Link to="/search" className="btn" style={{ marginTop: 8 }}>
            Zurück zur Suche
          </Link>
        }
      />
    )
  }

  return (
    <div>
      <div style={{ display: 'flex', gap: 20, alignItems: 'flex-start', flexWrap: 'wrap' }}>
        <div
          style={{
            width: 140,
            aspectRatio: '2 / 3',
            borderRadius: 'var(--radius)',
            background: result.coverUrl ? undefined : '#3f5e6b',
            flexShrink: 0,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            color: '#fff',
            overflow: 'hidden',
          }}
        >
          {result.coverUrl ? (
            <img src={result.coverUrl} alt="" style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
          ) : (
            <Icon name="clapper" size={36} />
          )}
        </div>

        <div style={{ flex: 1, minWidth: 240 }}>
          <h1>{result.title}</h1>
          <p style={{ marginTop: 6, fontSize: 14 }}>
            {MEDIA_TYPE_LABELS[result.mediaType]}
            {result.releaseYear && ` · ${result.releaseYear}`}
            {result.creator && ` · ${result.creator}`}
          </p>
          <p style={{ marginTop: 16, maxWidth: 640 }}>{result.description}</p>

          <button
            type="button"
            className="btn btn--primary"
            style={{ marginTop: 24 }}
            onClick={() => setShowAddModal(true)}
          >
            Zur Liste hinzufügen
          </button>
        </div>
      </div>

      {showAddModal && <AddToListModal result={result} onClose={() => setShowAddModal(false)} />}
    </div>
  )
}

export default ExternalMediaDetailPage
