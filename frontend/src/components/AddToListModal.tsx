import { useState } from 'react'
import { STATUS_LABELS, type CatalogItem, type WatchStatus } from '../types/media'
import { useLibrary } from '../context/LibraryContext'

const STATUS_OPTIONS: WatchStatus[] = ['PLANNED', 'WATCHING', 'COMPLETED', 'DROPPED']

interface AddToListModalProps {
  item: CatalogItem
  onClose: () => void
}

function AddToListModal({ item, onClose }: AddToListModalProps) {
  const { addEntry } = useLibrary()
  const [status, setStatus] = useState<WatchStatus>('PLANNED')

  function handleAdd() {
    addEntry(item, status)
    onClose()
  }

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="card modal-content" onClick={(e) => e.stopPropagation()}>
        <h2>Zur Liste hinzufügen</h2>
        <p style={{ margin: '12px 0 16px' }}>
          <strong>{item.title}</strong> mit welchem Status anlegen?
        </p>
        <div className="field" style={{ marginBottom: 20 }}>
          <label htmlFor="add-status">Status</label>
          <select
            id="add-status"
            className="input"
            value={status}
            onChange={(e) => setStatus(e.target.value as WatchStatus)}
          >
            {STATUS_OPTIONS.map((s) => (
              <option key={s} value={s}>
                {STATUS_LABELS[s]}
              </option>
            ))}
          </select>
        </div>
        <div style={{ display: 'flex', gap: 8 }}>
          <button type="button" className="btn" onClick={onClose}>
            Abbrechen
          </button>
          <button type="button" className="btn btn--primary" onClick={handleAdd}>
            Hinzufügen
          </button>
        </div>
      </div>
    </div>
  )
}

export default AddToListModal
