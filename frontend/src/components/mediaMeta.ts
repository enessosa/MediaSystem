import { MEDIA_TYPE_LABELS, type MediaSearchResult, type MediaType } from '../api/media'

interface MetaSource {
  mediaType: MediaType
  releaseYear: number | null
  creator: string | null
}

export function mediaMeta({ mediaType, releaseYear, creator }: MetaSource): string {
  return [MEDIA_TYPE_LABELS[mediaType], releaseYear, creator].filter(Boolean).join(' · ')
}

export function externalDetailUrl(item: MediaSearchResult): string | null {
  if (!item.externalId) return null
  return `/media/${item.sourceType}/${encodeURIComponent(item.externalId)}`
}

export function plainText(html: string): string {
  return new DOMParser().parseFromString(html, 'text/html').body.textContent ?? ''
}
