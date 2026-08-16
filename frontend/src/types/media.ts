// Spiegelt die Backend-Enums (de.mediasystem.backend.model.MediaType / .Status)

export type MediaType = 'ANIME' | 'MANGA' | 'BOOK' | 'SERIES'

export type WatchStatus = 'WATCHING' | 'COMPLETED' | 'PLANNED' | 'DROPPED'

export interface CatalogItem {
  id: string
  title: string
  description: string
  releaseYear: number | null
  mediaType: MediaType
  creator: string | null
  coverColor: string
}

export interface LibraryEntry {
  entryId: string
  item: CatalogItem
  status: WatchStatus
  rating: number | null
  note: string
  updatedAt: string
}

export const MEDIA_TYPE_LABELS: Record<MediaType, string> = {
  ANIME: 'Anime',
  MANGA: 'Manga',
  BOOK: 'Buch',
  SERIES: 'Serie',
}

export const STATUS_LABELS: Record<WatchStatus, string> = {
  WATCHING: 'Watching',
  COMPLETED: 'Completed',
  PLANNED: 'Planned',
  DROPPED: 'Dropped',
}