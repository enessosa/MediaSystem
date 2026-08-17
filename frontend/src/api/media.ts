import { apiFetch } from './client'
import type { MediaType } from '../types/media'

export type SourceType = 'ANILIST' | 'TMDB' | 'OPENLIBRARY' | 'MANUAL'

export interface MediaSearchResult {
  title: string
  description: string
  releaseYear: number | null
  mediaType: MediaType
  creator: string | null
  coverUrl: string
  sourceType: SourceType
  externalId: string
}

export function searchMedia(query: string) {
  return apiFetch<MediaSearchResult[]>(`/media/search?q=${encodeURIComponent(query)}`)
}