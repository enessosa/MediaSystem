import { PlusIcon } from '@primer/octicons-react'
import { Button, FormControl, SegmentedControl, Select, Stack, useConfirm } from '@primer/react'
import { Blankslate } from '@primer/react/experimental'
import { useState } from 'react'
import { useNavigate, useSearchParams } from 'react-router'
import {
  deleteEntry,
  ENTRIES_PATH,
  MEDIA_TYPE_LABELS,
  MEDIA_TYPES,
  STATUS_LABELS,
  STATUSES,
  type MediaType,
  type Status,
  type UserEntryResponse,
} from '../api/media'
import EntryEditDialog from '../components/EntryEditDialog'
import EntryRow from '../components/EntryRow'
import ErrorBanner from '../components/ErrorBanner'
import listStyles from '../components/ItemList.module.css'
import Loading from '../components/Loading'
import ManualAddDialog from '../components/ManualAddDialog'
import PageTitle from '../components/PageTitle'
import { useApi } from '../hooks/useApi'

const STATUS_FILTERS: (Status | null)[] = [null, ...STATUSES]

type SortOrder = 'newest' | 'rating-desc' | 'rating-asc'

const SORT_LABELS: Record<SortOrder, string> = {
  newest: 'Zuletzt hinzugefügt',
  'rating-desc': 'Beste Bewertung zuerst',
  'rating-asc': 'Schlechteste Bewertung zuerst',
}

function compareEntries(order: SortOrder) {
  return (a: UserEntryResponse, b: UserEntryResponse): number => {
    if (order !== 'newest') {
      if (a.rating === null && b.rating !== null) return 1
      if (b.rating === null && a.rating !== null) return -1
      if (a.rating !== null && b.rating !== null && a.rating !== b.rating) {
        return order === 'rating-desc' ? b.rating - a.rating : a.rating - b.rating
      }
    }
    return b.addedAt.localeCompare(a.addedAt)
  }
}

function MyListPage() {
  const { data: entries, error, isLoading, reload } = useApi<UserEntryResponse[]>(ENTRIES_PATH)
  const [searchParams, setSearchParams] = useSearchParams()
  const [editing, setEditing] = useState<UserEntryResponse | null>(null)
  const [isManualOpen, setIsManualOpen] = useState(false)
  const [actionError, setActionError] = useState<unknown>(null)
  const confirm = useConfirm()
  const navigate = useNavigate()

  const statusFilter = STATUSES.find((s) => s === searchParams.get('status')) ?? null
  const typeFilter = MEDIA_TYPES.find((t) => t === searchParams.get('type')) ?? null
  const sortOrder = (Object.keys(SORT_LABELS) as SortOrder[]).find((o) => o === searchParams.get('sort')) ?? 'newest'

  function setFilter(name: 'status' | 'type' | 'sort', value: string | null) {
    const next = new URLSearchParams(searchParams)
    if (value) next.set(name, value)
    else next.delete(name)
    setSearchParams(next)
  }

  async function handleDelete(entry: UserEntryResponse) {
    const confirmed = await confirm({
      title: 'Eintrag entfernen?',
      content: `„${entry.title}“ wird aus deiner Liste entfernt. Status, Bewertung und Notiz gehen verloren.`,
      confirmButtonContent: 'Entfernen',
      confirmButtonType: 'danger',
      cancelButtonContent: 'Abbrechen',
    })
    if (!confirmed) return
    setActionError(null)
    try {
      await deleteEntry(entry.id)
      reload()
    } catch (err) {
      setActionError(err)
    }
  }

  const byType = (entries ?? []).filter((e) => typeFilter === null || e.mediaType === typeFilter)
  const visible = byType
    .filter((e) => statusFilter === null || e.status === statusFilter)
    .sort(compareEntries(sortOrder))

  return (
    <Stack gap="normal">
      <PageTitle
        title="Meine Liste"
        actions={
          <Button leadingVisual={PlusIcon} onClick={() => setIsManualOpen(true)}>
            Selbst anlegen
          </Button>
        }
      />

      {isLoading && <Loading />}
      {error !== null && <ErrorBanner error={error} onRetry={reload} />}
      {actionError !== null && <ErrorBanner error={actionError} />}

      {entries && entries.length === 0 && (
        <Blankslate border spacious>
          <Blankslate.Heading>Deine Liste ist noch leer</Blankslate.Heading>
          <Blankslate.Description>Über die Suche findest du Anime, Serien, Filme, Bücher und Spiele.</Blankslate.Description>
          <Blankslate.PrimaryAction onClick={() => navigate('/search')}>Zur Suche</Blankslate.PrimaryAction>
        </Blankslate>
      )}

      {entries && entries.length > 0 && (
        <>
          <Stack direction={{ narrow: 'vertical', regular: 'horizontal' }} justify="space-between" gap="normal">
            <SegmentedControl
              aria-label="Nach Status filtern"
              onChange={(index) => setFilter('status', STATUS_FILTERS[index])}
            >
              {STATUS_FILTERS.map((status) => (
                <SegmentedControl.Button key={status ?? 'all'} selected={status === statusFilter}>
                  {`${status ? STATUS_LABELS[status] : 'Alle'} (${byType.filter((e) => status === null || e.status === status).length})`}
                </SegmentedControl.Button>
              ))}
            </SegmentedControl>
            <Stack direction="horizontal" gap="condensed" wrap="wrap">
              <FormControl>
                <FormControl.Label visuallyHidden>Medientyp</FormControl.Label>
                <Select value={typeFilter ?? ''} onChange={(event) => setFilter('type', event.target.value || null)}>
                  <Select.Option value="">Alle Medientypen</Select.Option>
                  {MEDIA_TYPES.map((type: MediaType) => (
                    <Select.Option key={type} value={type}>
                      {MEDIA_TYPE_LABELS[type]}
                    </Select.Option>
                  ))}
                </Select>
              </FormControl>
              <FormControl>
                <FormControl.Label visuallyHidden>Sortierung</FormControl.Label>
                <Select
                  value={sortOrder}
                  onChange={(event) => setFilter('sort', event.target.value === 'newest' ? null : event.target.value)}
                >
                  {(Object.keys(SORT_LABELS) as SortOrder[]).map((order) => (
                    <Select.Option key={order} value={order}>
                      {SORT_LABELS[order]}
                    </Select.Option>
                  ))}
                </Select>
              </FormControl>
            </Stack>
          </Stack>

          {visible.length === 0 ? (
            <Blankslate border>
              <Blankslate.Heading>Keine Einträge für diesen Filter</Blankslate.Heading>
            </Blankslate>
          ) : (
            <ul className={listStyles.list} aria-label="Einträge">
              {visible.map((entry) => (
                <EntryRow key={entry.id} entry={entry} onEdit={setEditing} onDelete={handleDelete} />
              ))}
            </ul>
          )}
        </>
      )}

      {editing && (
        <EntryEditDialog
          entry={editing}
          onClose={() => setEditing(null)}
          onSaved={() => {
            setEditing(null)
            reload()
          }}
        />
      )}
      {isManualOpen && (
        <ManualAddDialog onClose={() => setIsManualOpen(false)} onCreated={(entry) => navigate(`/entries/${entry.id}`)} />
      )}
    </Stack>
  )
}

export default MyListPage
