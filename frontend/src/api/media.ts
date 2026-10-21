import { apiFetch } from './client'

export type MediaType = 'ANIME' | 'MANGA' | 'BOOK' | 'SERIES' | 'MOVIE' | 'GAME'
export type Status = 'WATCHING' | 'COMPLETED' | 'PLANNED' | 'DROPPED'
export type SourceType = 'ANILIST' | 'TMDB' | 'OPENLIBRARY' | 'RAWG' | 'MANUAL'

export const MEDIA_TYPES: MediaType[] = ['ANIME', 'MANGA', 'SERIES', 'MOVIE', 'BOOK', 'GAME']
export const STATUSES: Status[] = ['WATCHING', 'PLANNED', 'COMPLETED', 'DROPPED']

export const MEDIA_TYPE_LABELS: Record<MediaType, string> = {
  ANIME: 'Anime',
  MANGA: 'Manga',
  SERIES: 'Serie',
  MOVIE: 'Film',
  BOOK: 'Buch',
  GAME: 'Spiel',
}

export const STATUS_LABELS: Record<Status, string> = {
  WATCHING: 'Dabei',
  PLANNED: 'Geplant',
  COMPLETED: 'Abgeschlossen',
  DROPPED: 'Abgebrochen',
}

export interface MediaSearchResult {
  title: string
  description: string | null
  releaseYear: number | null
  mediaType: MediaType
  creator: string | null
  coverUrl: string | null
  sourceType: SourceType
  externalId: string | null
}

export interface UserEntryResponse {
  id: number
  status: Status
  rating: number | null
  note: string | null
  addedAt: string
  mediaItemid: number
  title: string
  description: string | null
  releaseYear: number | null
  mediaType: MediaType
  creator: string | null
  coverUrl: string | null
}

export type AddToListRequest = MediaSearchResult

export interface UpdateUserEntryRequest {
  status: Status
  rating: number | null
  note: string | null
}

export function searchPath(query: string, mediaTypes: MediaType[]): string {
  const params = new URLSearchParams({ q: query })
  if (mediaTypes.length > 0) params.set('mediaTypes', mediaTypes.join(','))
  return `/media/search?${params}`
}

export function mediaDetailPath(sourceType: string, externalId: string): string {
  const params = new URLSearchParams({ sourceType, externalId })
  return `/media/items?${params}`
}

export const ENTRIES_PATH = '/media/entries'

export function addToList(request: AddToListRequest): Promise<UserEntryResponse> {
  return apiFetch<UserEntryResponse>(ENTRIES_PATH, {
    method: 'POST',
    body: JSON.stringify(request),
  })
}

export function updateEntry(id: number, request: UpdateUserEntryRequest): Promise<UserEntryResponse> {
  return apiFetch<UserEntryResponse>(`${ENTRIES_PATH}/${id}`, {
    method: 'PUT',
    body: JSON.stringify(request),
  })
}

export function deleteEntry(id: number): Promise<void> {
  return apiFetch<void>(`${ENTRIES_PATH}/${id}`, { method: 'DELETE' })
}
