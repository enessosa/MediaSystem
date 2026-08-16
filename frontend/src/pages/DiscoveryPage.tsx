import { useState } from 'react'
import { CATALOG } from '../data/catalog'
import { useLibrary } from '../context/LibraryContext'
import type { CatalogItem } from '../types/media'
import MediaCard from '../components/MediaCard'
import AddToListModal from '../components/AddToListModal'

function DiscoveryPage() {
  const { getEntryByItemId } = useLibrary()
  const [selectedItem, setSelectedItem] = useState<CatalogItem | null>(null)

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

      {selectedItem && <AddToListModal item={selectedItem} onClose={() => setSelectedItem(null)} />}
    </div>
  )
}

export default DiscoveryPage