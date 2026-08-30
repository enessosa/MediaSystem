import { createContext, useContext, useEffect, useMemo, useState, type ReactNode } from 'react'
import {
  addToUserList,
  getUserEntries,
  updateUserEntry,
  type MediaSearchResult,
  type UserEntryResponse,
} from '../api/media'
import { ApiError } from '../api/client'
import type { CatalogItem, LibraryEntry, MediaType, WatchStatus } from '../types/media'

/** Fallback-Farbe pro Medientyp, falls der Provider kein Cover-Bild liefert (z.B. TMDB ohne poster_path). */
const FALLBACK_COLOR: Record<MediaType, string> = {
  ANIME: '#5b2f6b',
  MANGA: '#c9581f',
  BOOK: '#2f6b4f',
  SERIES: '#3f5e6b',
  MOVIE: '#7a1f2b',
}

function toLibraryEntry(response: UserEntryResponse): LibraryEntry {
  const item: CatalogItem = {
    id: String(response.mediaItemid),
    title: response.title,
    description: response.description,
    releaseYear: response.releaseYear,
    mediaType: response.mediaType,
    creator: response.creator,
    coverColor: FALLBACK_COLOR[response.mediaType],
    coverUrl: response.coverUrl ?? undefined,
  }
  return {
    entryId: String(response.id),
    item,
    status: response.status,
    rating: response.rating,
    note: response.note ?? '',
    updatedAt: response.addedAt,
  }
}

interface LibraryContextValue {
  entries: LibraryEntry[]
  isLoading: boolean
  error: string | null
  getEntry: (entryId: string) => LibraryEntry | undefined
  addEntry: (result: MediaSearchResult) => Promise<LibraryEntry>
  updateEntry: (
    entryId: string,
    changes: Partial<Pick<LibraryEntry, 'status' | 'rating' | 'note'>>,
  ) => Promise<void>
}

const LibraryContext = createContext<LibraryContextValue | null>(null)

/**
 * Verwaltet die persönliche Liste (User-Eintrag <-> MediaItem, siehe CLAUDE.md Domain-Glossar).
 * Läuft jetzt komplett gegen das echte Backend (GET/POST/PUT /media/entries) statt localStorage.
 * Kein removeEntry: das Backend hat noch keinen DELETE-Endpoint für UserEntry (siehe PROGRESS.md).
 */
export function LibraryProvider({ children }: { children: ReactNode }) {
  const [entries, setEntries] = useState<LibraryEntry[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    let cancelled = false
    getUserEntries()
      .then((data) => {
        if (!cancelled) setEntries(data.map(toLibraryEntry))
      })
      .catch((err) => {
        if (!cancelled) {
          setError(err instanceof ApiError ? err.message : 'Liste konnte nicht geladen werden.')
        }
      })
      .finally(() => {
        if (!cancelled) setIsLoading(false)
      })
    return () => {
      cancelled = true
    }
  }, [])

  const value = useMemo<LibraryContextValue>(
    () => ({
      entries,
      isLoading,
      error,
      getEntry: (entryId) => entries.find((e) => e.entryId === entryId),
      addEntry: async (result) => {
        const response = await addToUserList(result)
        const entry = toLibraryEntry(response)
        setEntries((prev) => [entry, ...prev])
        return entry
      },
      updateEntry: async (entryId, changes) => {
        const current = entries.find((e) => e.entryId === entryId)
        if (!current) return
        const status: WatchStatus = changes.status ?? current.status
        const rating = changes.rating !== undefined ? changes.rating : current.rating
        const note = changes.note !== undefined ? changes.note : current.note
        const response = await updateUserEntry(Number(entryId), { status, rating, note })
        const updated = toLibraryEntry(response)
        setEntries((prev) => prev.map((e) => (e.entryId === entryId ? updated : e)))
      },
    }),
    [entries, isLoading, error],
  )

  return <LibraryContext.Provider value={value}>{children}</LibraryContext.Provider>
}

export function useLibrary() {
  const ctx = useContext(LibraryContext)
  if (!ctx) {
    throw new Error('useLibrary muss innerhalb von <LibraryProvider> verwendet werden')
  }
  return ctx
}
