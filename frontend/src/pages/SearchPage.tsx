import { PlusIcon, SearchIcon } from '@primer/octicons-react'
import { Banner, Button, Pagination, Stack } from '@primer/react'
import { Blankslate } from '@primer/react/experimental'
import { useState, type MouseEvent } from 'react'
import { useNavigate, useSearchParams } from 'react-router'
import {
  AVAILABLE_TYPES_PATH,
  MEDIA_TYPE_LABELS,
  MEDIA_TYPES,
  searchPath,
  type MediaSearchResult,
  type MediaType,
} from '../api/media'
import ErrorBanner from '../components/ErrorBanner'
import listStyles from '../components/ItemList.module.css'
import Loading from '../components/Loading'
import ManualAddDialog from '../components/ManualAddDialog'
import MediaTypeFilter from '../components/MediaTypeFilter'
import PageTitle from '../components/PageTitle'
import SearchForm from '../components/SearchForm'
import SearchResultRow from '../components/SearchResultRow'
import { useApi } from '../hooks/useApi'

const PAGE_SIZE = 20

function parseTypes(value: string | null): MediaType[] {
  if (!value) return []
  return value.split(',').filter((t): t is MediaType => MEDIA_TYPES.includes(t as MediaType))
}

function SearchPage() {
  const [searchParams, setSearchParams] = useSearchParams()
  const navigate = useNavigate()
  const [isManualOpen, setIsManualOpen] = useState(false)

  const query = searchParams.get('q') ?? ''
  const types = parseTypes(searchParams.get('types'))
  const requestedPage = Number(searchParams.get('page') ?? '1')
  const { data: results, error, isLoading, reload } = useApi<MediaSearchResult[]>(
    query ? searchPath(query, types) : null,
  )
  const { data: availableTypes } = useApi<MediaType[]>(AVAILABLE_TYPES_PATH)
  const unavailable = availableTypes ? MEDIA_TYPES.filter((t) => !availableTypes.includes(t)) : []

  const pageCount = results ? Math.max(1, Math.ceil(results.length / PAGE_SIZE)) : 1
  const page = Number.isInteger(requestedPage) ? Math.min(Math.max(requestedPage, 1), pageCount) : 1
  const pageResults = results ? results.slice((page - 1) * PAGE_SIZE, page * PAGE_SIZE) : []

  function updateParams(nextQuery: string, nextTypes: MediaType[]) {
    const params: Record<string, string> = { q: nextQuery }
    if (nextTypes.length > 0) params.types = nextTypes.join(',')
    setSearchParams(params)
  }

  function pageHref(n: number): string {
    const params = new URLSearchParams(searchParams)
    params.set('page', String(n))
    return `?${params}`
  }

  function goToPage(event: MouseEvent, n: number) {
    event.preventDefault()
    const params = new URLSearchParams(searchParams)
    params.set('page', String(n))
    setSearchParams(params)
    window.scrollTo({ top: 0 })
  }

  return (
    <Stack gap="normal">
      <PageTitle
        title="Suche"
        actions={
          <Button leadingVisual={PlusIcon} onClick={() => setIsManualOpen(true)}>
            Selbst anlegen
          </Button>
        }
      />

      <SearchForm key={query} initialQuery={query} showButton onSearch={(q) => updateParams(q, types)} />
      <MediaTypeFilter selected={types} onChange={(next) => updateParams(query, next)} unavailable={unavailable} />
      {unavailable.length > 0 && (
        <Banner
          variant="warning"
          title="Eingeschränkte Suche"
          hideTitle
          description={`Nicht durchsuchbar: ${unavailable.map((t) => MEDIA_TYPE_LABELS[t]).join(', ')} – dafür ist auf diesem Server kein API-Key konfiguriert.`}
        />
      )}

      {!query && (
        <Blankslate border>
          <Blankslate.Visual>
            <SearchIcon size="medium" />
          </Blankslate.Visual>
          <Blankslate.Heading>Wonach suchst du?</Blankslate.Heading>
          <Blankslate.Description>
            Durchsucht AniList, TMDB, RAWG und OpenLibrary gleichzeitig. Mit den Häkchen kannst du die Suche
            auf bestimmte Medientypen einschränken.
          </Blankslate.Description>
        </Blankslate>
      )}
      {isLoading && <Loading label="Suche läuft..." />}
      {error !== null && <ErrorBanner error={error} onRetry={reload} />}
      {results && results.length === 0 && (
        <Blankslate border>
          <Blankslate.Heading>Keine Treffer für „{query}“</Blankslate.Heading>
          <Blankslate.Description>Versuch einen anderen Begriff oder leg das Medium selbst an.</Blankslate.Description>
          <Blankslate.PrimaryAction onClick={() => setIsManualOpen(true)}>Selbst anlegen</Blankslate.PrimaryAction>
        </Blankslate>
      )}
      {results && results.length > 0 && (
        <>
          <ul className={listStyles.list} aria-label="Suchergebnisse">
            {pageResults.map((item, index) => (
              <SearchResultRow key={`${item.sourceType}-${item.externalId ?? index}`} item={item} />
            ))}
          </ul>
          {pageCount > 1 && (
            <Pagination pageCount={pageCount} currentPage={page} hrefBuilder={pageHref} onPageChange={goToPage} />
          )}
        </>
      )}

      {isManualOpen && (
        <ManualAddDialog
          initialTitle={query}
          onClose={() => setIsManualOpen(false)}
          onCreated={(entry) => navigate(`/entries/${entry.id}`)}
        />
      )}
    </Stack>
  )
}

export default SearchPage
