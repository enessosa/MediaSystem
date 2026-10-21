import { useMemo } from 'react'
import { ENTRIES_PATH, mediaKey, type UserEntryResponse } from '../api/media'
import { useApi } from './useApi'

/**
 * Welche Medien schon in der eigenen Liste sind: mediaKey(sourceType, externalId) → Eintrags-ID.
 * Selbst angelegte Einträge (ohne externalId) können nie einem Suchergebnis entsprechen und fehlen deshalb.
 */
export function useListedEntries(): Map<string, number> {
  const { data: entries } = useApi<UserEntryResponse[]>(ENTRIES_PATH)

  return useMemo(() => {
    const listed = new Map<string, number>()
    for (const entry of entries ?? []) {
      if (entry.externalId !== null) listed.set(mediaKey(entry.sourceType, entry.externalId), entry.id)
    }
    return listed
  }, [entries])
}
