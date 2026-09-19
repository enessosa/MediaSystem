import { MEDIA_TYPE_LABELS, type MediaType } from '../types/media'

const ALL_MEDIA_TYPES = Object.keys(MEDIA_TYPE_LABELS) as MediaType[]

interface MediaTypeFilterProps {
  selected: MediaType[]
  onToggle: (type: MediaType) => void
}

function MediaTypeFilter({ selected, onToggle }: MediaTypeFilterProps) {
  return (
    <div style={{ display: 'flex', flexWrap: 'wrap', gap: 12 }}>
      {ALL_MEDIA_TYPES.map((type) => (
        <label key={type} style={{ display: 'flex', alignItems: 'center', gap: 4, fontSize: 13 }}>
          <input type="checkbox" checked={selected.includes(type)} onChange={() => onToggle(type)} />
          {MEDIA_TYPE_LABELS[type]}
        </label>
      ))}
    </div>
  )
}

export default MediaTypeFilter
