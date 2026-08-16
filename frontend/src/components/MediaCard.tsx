import type { ReactNode } from 'react'
import { Link } from 'react-router-dom'
import { MEDIA_TYPE_LABELS, type CatalogItem, type WatchStatus } from '../types/media'
import Icon from './Icon'
import StatusBadge from './StatusBadge'
import './MediaCard.css'

const TYPE_ICON: Record<CatalogItem['mediaType'], 'clapper' | 'book' | 'tv'> = {
  ANIME: 'clapper',
  MANGA: 'book',
  BOOK: 'book',
  SERIES: 'tv',
}

interface MediaCardProps {
  item: CatalogItem
  status?: WatchStatus
  rating?: number | null
  action?: ReactNode
}

function MediaCard({ item, status, rating, action }: MediaCardProps) {
  return (
    <div className="media-card">
      <Link to={`/media/${item.id}`} className="media-card__cover" style={{ background: item.coverColor }}>
        <Icon name={TYPE_ICON[item.mediaType]} size={28} className="media-card__cover-icon" />
        {status && <StatusBadge status={status} />}
      </Link>

      <div className="media-card__body">
        <Link to={`/media/${item.id}`} className="media-card__title">
          {item.title}
        </Link>
        <div className="media-card__meta">
          <span>{MEDIA_TYPE_LABELS[item.mediaType]}</span>
          {item.releaseYear && (
            <>
              <span aria-hidden="true">·</span>
              <span>{item.releaseYear}</span>
            </>
          )}
          {typeof rating === 'number' && (
            <>
              <span aria-hidden="true">·</span>
              <span className="media-card__rating">
                <Icon name="star" size={13} /> {rating}/10
              </span>
            </>
          )}
        </div>
        {action && <div className="media-card__action">{action}</div>}
      </div>
    </div>
  )
}

export default MediaCard
