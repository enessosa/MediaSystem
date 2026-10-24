import { ArrowLeftIcon, PencilIcon, StarFillIcon, TrashIcon } from '@primer/octicons-react'
import { Button, Heading, Label, Stack, useConfirm } from '@primer/react'
import { useState, type ReactNode } from 'react'
import { useNavigate, useParams } from 'react-router'
import {
  deleteEntry,
  ENTRIES_PATH,
  mediaDetailPath,
  mediaKey,
  type MediaSearchResult,
  type SourceType,
  type UserEntryResponse,
} from '../api/media'
import AddToListButton from '../components/AddToListButton'
import EntryEditDialog from '../components/EntryEditDialog'
import ErrorBanner from '../components/ErrorBanner'
import Loading from '../components/Loading'
import MediaCover from '../components/MediaCover'
import { mediaMeta, plainText } from '../components/mediaMeta'
import StatusLabel from '../components/StatusLabel'
import { useApi } from '../hooks/useApi'
import { useListedEntries } from '../hooks/useListedEntries'
import { useI18n } from '../i18n/useI18n'
import NotFoundPage from './NotFoundPage'
import styles from './MediaDetailPage.module.css'

interface DetailViewProps {
  title: string
  meta: string
  coverUrl: string | null
  description: string | null
  source?: SourceType
  children?: ReactNode
}

function DetailView({ title, meta, coverUrl, description, source, children }: DetailViewProps) {
  const navigate = useNavigate()
  const { t } = useI18n()

  return (
    <Stack gap="normal">
      <div>
        <Button variant="invisible" leadingVisual={ArrowLeftIcon} onClick={() => navigate(-1)}>
          {t.common.back}
        </Button>
      </div>
      <div className={styles.layout}>
        <MediaCover url={coverUrl} title={title} width={200} />
        <div className={styles.body}>
          <Stack gap="normal">
            <div>
              <Heading as="h1">{title}</Heading>
              <Stack direction="horizontal" align="center" gap="condensed">
                <span className={styles.meta}>{meta}</span>
                {source && <Label>{t.sources[source]}</Label>}
              </Stack>
            </div>
            {children}
            {description && <p className={styles.description}>{plainText(description)}</p>}
          </Stack>
        </div>
      </div>
    </Stack>
  )
}

function ExternalMediaDetail({ sourceType, externalId }: { sourceType: string; externalId: string }) {
  const { data: item, error, isLoading, reload } = useApi<MediaSearchResult>(mediaDetailPath(sourceType, externalId))
  const listed = useListedEntries()
  const { t } = useI18n()

  if (isLoading) return <Loading />
  if (error !== null) return <ErrorBanner error={error} onRetry={reload} />
  if (!item) return null

  return (
    <DetailView
      title={item.title}
      meta={mediaMeta(item, t)}
      coverUrl={item.coverUrl}
      description={item.description}
      source={item.sourceType}
    >
      <div>
        <AddToListButton item={item} entryId={listed.get(mediaKey(item.sourceType, externalId))} />
      </div>
    </DetailView>
  )
}

function EntryDetail({ entryId }: { entryId: number }) {
  const { data: entries, error, isLoading, reload } = useApi<UserEntryResponse[]>(ENTRIES_PATH)
  const [isEditing, setIsEditing] = useState(false)
  const [actionError, setActionError] = useState<unknown>(null)
  const confirm = useConfirm()
  const navigate = useNavigate()
  const { t } = useI18n()

  if (isLoading) return <Loading />
  if (error !== null) return <ErrorBanner error={error} onRetry={reload} />

  const entry = entries?.find((e) => e.id === entryId)
  if (!entry) return <NotFoundPage />

  async function handleDelete(target: UserEntryResponse) {
    const confirmed = await confirm({
      title: t.media.removeTitle,
      content: t.media.removeContent(target.title),
      confirmButtonContent: t.common.remove,
      confirmButtonType: 'danger',
      cancelButtonContent: t.common.cancel,
    })
    if (!confirmed) return
    try {
      await deleteEntry(target.id)
      navigate('/list', { replace: true })
    } catch (err) {
      setActionError(err)
    }
  }

  return (
    <DetailView
      title={entry.title}
      meta={mediaMeta(entry, t)}
      coverUrl={entry.coverUrl}
      description={entry.description}
      source={entry.sourceType}
    >
      {actionError !== null && <ErrorBanner error={actionError} />}
      <div className={styles.entryBox}>
        <Stack gap="condensed">
          <Stack direction="horizontal" align="center" justify="space-between" wrap="wrap" gap="condensed">
            <Stack direction="horizontal" align="center" gap="condensed">
              <StatusLabel status={entry.status} />
              {entry.rating !== null && (
                <span>
                  <StarFillIcon size={14} /> {entry.rating}/10
                </span>
              )}
              <span className={styles.meta}>
                {t.media.addedOn(new Date(entry.addedAt).toLocaleDateString(t.locale))}
              </span>
            </Stack>
            <Stack direction="horizontal" gap="condensed">
              <Button size="small" leadingVisual={PencilIcon} onClick={() => setIsEditing(true)}>
                {t.common.edit}
              </Button>
              <Button size="small" variant="danger" leadingVisual={TrashIcon} onClick={() => handleDelete(entry)}>
                {t.common.remove}
              </Button>
            </Stack>
          </Stack>
          {entry.note && <p className={styles.note}>{entry.note}</p>}
        </Stack>
      </div>

      {isEditing && (
        <EntryEditDialog
          entry={entry}
          onClose={() => setIsEditing(false)}
          onSaved={() => {
            setIsEditing(false)
            reload()
          }}
        />
      )}
    </DetailView>
  )
}

function MediaDetailPage() {
  const { entryId, sourceType, externalId } = useParams()

  if (entryId !== undefined) {
    const id = Number(entryId)
    return Number.isInteger(id) ? <EntryDetail entryId={id} /> : <NotFoundPage />
  }
  if (sourceType && externalId) {
    return <ExternalMediaDetail sourceType={sourceType} externalId={externalId} />
  }
  return <NotFoundPage />
}

export default MediaDetailPage
