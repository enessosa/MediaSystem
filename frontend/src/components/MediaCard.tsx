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
  MOVIE: 'clapper',
}

interface MediaCardProps {
  item: CatalogItem
  status?: WatchStatus
  rating?: number | null
  action?: ReactNode
  /** Ziel für Cover/Titel-Link. Ohne Detailseite (z.B. noch nicht hinzugefügte Suchergebnisse) weglassen. */
  linkTo?: string
}

function MediaCard({ item, status, rating, action, linkTo }: MediaCardProps) {
  const cover = item.coverUrl ? (
    <img src={item.coverUrl} alt="" className="media-card__cover-img" />
  ) : (
    <Icon name={TYPE_ICON[item.mediaType]} size={28} className="media-card__cover-icon" />
  )

  return (
    <div className="media-card">
      {linkTo ? (
        <Link
          to={linkTo}
          className="media-card__cover"
          style={item.coverUrl ? undefined : { background: item.coverColor }}
        >
          {cover}
          {status && <StatusBadge status={status} />}
        </Link>
      ) : (
        <div className="media-card__cover" style={item.coverUrl ? undefined : { background: item.coverColor }}>
          {cover}
          {status && <StatusBadge status={status} />}
        </div>
      )}

      <div className="media-card__body">
        {linkTo ? (
          <Link to={linkTo} className="media-card__title">
            {item.title}
          </Link>
        ) : (
          <span className="media-card__title">{item.title}</span>
        )}
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
