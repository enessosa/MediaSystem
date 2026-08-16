import { createContext, useContext, useEffect, useMemo, useState, type ReactNode } from 'react'
import type { CatalogItem, LibraryEntry, WatchStatus } from '../types/media'

const STORAGE_KEY = 'ms_library'

interface LibraryContextValue {
  entries: LibraryEntry[]
  getEntryByItemId: (itemId: string) => LibraryEntry | undefined
  getEntry: (entryId: string) => LibraryEntry | undefined
  addEntry: (item: CatalogItem, status: WatchStatus) => void
  updateEntry: (entryId: string, changes: Partial<Pick<LibraryEntry, 'status' | 'rating' | 'note'>>) => void
  removeEntry: (entryId: string) => void
}

const LibraryContext = createContext<LibraryContextValue | null>(null)

function loadInitial(): LibraryEntry[] {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    return raw ? (JSON.parse(raw) as LibraryEntry[]) : []
  } catch {
    return []
  }
}

/**
 * Verwaltet die persönliche Liste (User-Eintrag <-> MediaItem, siehe CLAUDE.md Domain-Glossar).
 * Solange es dafür noch keinen Backend-Endpoint gibt, wird lokal im Browser gespeichert
 * (localStorage), damit die App über Reloads hinweg nutzbar bleibt. Ersetzen, sobald
 * "Medium hinzufügen" serverseitig existiert.
 */
export function LibraryProvider({ children }: { children: ReactNode }) {
  const [entries, setEntries] = useState<LibraryEntry[]>(loadInitial)

  useEffect(() => {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(entries))
  }, [entries])

  const value = useMemo<LibraryContextValue>(
    () => ({
      entries,
      getEntryByItemId: (itemId) => entries.find((e) => e.item.id === itemId),
      getEntry: (entryId) => entries.find((e) => e.entryId === entryId),
      addEntry: (item, status) => {
        setEntries((prev) => {
          if (prev.some((e) => e.item.id === item.id)) {
            return prev
          }
          const newEntry: LibraryEntry = {
            entryId: crypto.randomUUID(),
            item,
            status,
            rating: null,
            note: '',
            updatedAt: new Date().toISOString(),
          }
          return [newEntry, ...prev]
        })
      },
      updateEntry: (entryId, changes) => {
        setEntries((prev) =>
          prev.map((e) =>
            e.entryId === entryId ? { ...e, ...changes, updatedAt: new Date().toISOString() } : e,
          ),
        )
      },
      removeEntry: (entryId) => {
        setEntries((prev) => prev.filter((e) => e.entryId !== entryId))
      },
    }),
    [entries],
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