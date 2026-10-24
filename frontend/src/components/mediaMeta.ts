import type { MediaSearchResult, MediaType } from '../api/media'
import type { Translations } from '../i18n/translations'

interface MetaSource {
  mediaType: MediaType
  releaseYear: number | null
  creator: string | null
}

export function mediaMeta({ mediaType, releaseYear, creator }: MetaSource, t: Translations): string {
  return [t.mediaTypes[mediaType], releaseYear, creator].filter(Boolean).join(' · ')
}

export function externalDetailUrl(item: MediaSearchResult): string | null {
  if (!item.externalId) return null
  return `/media/${item.sourceType}/${encodeURIComponent(item.externalId)}`
}

export function plainText(html: string): string {
  return new DOMParser().parseFromString(html, 'text/html').body.textContent ?? ''
}
