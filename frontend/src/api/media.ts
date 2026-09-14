import { apiFetch } from './client'
import type { MediaType, WatchStatus } from '../types/media'

export type SourceType = 'ANILIST' | 'TMDB' | 'RAWG' | 'OPENLIBRARY' | 'MANUAL'

export interface MediaSearchResult {
  title: string
  description: string
  releaseYear: number | null
  mediaType: MediaType
  creator: string | null
  coverUrl: string | null
  sourceType: SourceType
  /** null bei SourceType.MANUAL erlaubt (DB-Constraint: external_id nur für nicht-manuelle Quellen Pflicht). */
  externalId: string | null
}

/** Spiegelt de.mediasystem.backend.api.dto.UserEntryResponse */
export interface UserEntryResponse {
  id: number
  status: WatchStatus
  rating: number | null
  note: string | null
  addedAt: string
  mediaItemid: number
  title: string
  description: string
  releaseYear: number | null
  mediaType: MediaType
  creator: string | null
  coverUrl: string | null
}

export interface UpdateUserEntryPayload {
  status: WatchStatus
  rating: number | null
  note: string | null
}

export function searchMedia(query: string, mediaTypes?: MediaType[]) {
  const params = new URLSearchParams({ q: query })
  if (mediaTypes && mediaTypes.length > 0) {
    params.set('mediaTypes', mediaTypes.join(','))
  }
  return apiFetch<MediaSearchResult[]>(`/media/search?${params.toString()}`)
}

export function getUserEntries() {
  return apiFetch<UserEntryResponse[]>('/media/entries')
}

/** Lädt die Detailansicht eines externen Treffers direkt vom passenden Provider (GET /media/items). */
export function fetchMediaDetail(sourceType: SourceType, externalId: string) {
  const params = new URLSearchParams({ sourceType, externalId })
  return apiFetch<MediaSearchResult>(`/media/items?${params.toString()}`)
}

export function addToUserList(result: MediaSearchResult) {
  return apiFetch<UserEntryResponse>('/media/entries', {
    method: 'POST',
    body: JSON.stringify(result),
  })
}

export function updateUserEntry(entryId: number, payload: UpdateUserEntryPayload) {
  return apiFetch<UserEntryResponse>(`/media/entries/${entryId}`, {
    method: 'PUT',
    body: JSON.stringify(payload),
  })
}

export function deleteUserEntry(entryId: number) {
  return apiFetch<void>(`/media/entries/${entryId}`, { method: 'DELETE' })
}
