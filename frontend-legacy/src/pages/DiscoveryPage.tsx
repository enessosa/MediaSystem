import { useState } from 'react'
import { CATALOG } from '../data/catalog'
import type { CatalogItem } from '../types/media'
import type { MediaSearchResult, SourceType } from '../api/media'
import MediaCard from '../components/MediaCard'
import AddToListModal from '../components/AddToListModal'

/** CATALOG ist ein statischer Beispiel-Katalog (kein Backend-Endpoint dafür) mit ids im Schema `<sourceType>-<externalId>`. */
function toSearchResult(item: CatalogItem): MediaSearchResult {
  const [prefix, ...rest] = item.id.split('-')
  return {
    title: item.title,
    description: item.description,
    releaseYear: item.releaseYear,
    mediaType: item.mediaType,
    creator: item.creator,
    coverUrl: item.coverUrl ?? null,
    sourceType: prefix.toUpperCase() as SourceType,
    externalId: rest.join('-'),
  }
}

function DiscoveryPage() {
  const [selectedResult, setSelectedResult] = useState<MediaSearchResult | null>(null)

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
        {CATALOG.map((item) => {
          const searchResult = toSearchResult(item)
          return (
            <MediaCard
              key={item.id}
              item={item}
              linkTo={`/media/external/${searchResult.sourceType}/${encodeURIComponent(searchResult.externalId ?? '')}`}
              action={
                <button
                  type="button"
                  className="btn btn--primary"
                  style={{ width: '100%' }}
                  onClick={() => setSelectedResult(searchResult)}
                >
                  Zur Liste hinzufügen
                </button>
              }
            />
          )
        })}
      </div>

      {selectedResult && <AddToListModal result={selectedResult} onClose={() => setSelectedResult(null)} />}
    </div>
  )
}

export default DiscoveryPage
