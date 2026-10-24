import { KebabHorizontalIcon, PencilIcon, StarFillIcon, TrashIcon } from '@primer/octicons-react'
import { ActionList, ActionMenu, IconButton, Link, Stack } from '@primer/react'
import { Link as RouterLink } from 'react-router'
import type { UserEntryResponse } from '../api/media'
import { useI18n } from '../i18n/useI18n'
import styles from './ItemList.module.css'
import MediaCover from './MediaCover'
import { mediaMeta } from './mediaMeta'
import StatusLabel from './StatusLabel'

interface EntryRowProps {
  entry: UserEntryResponse
  compact?: boolean
  onEdit?: (entry: UserEntryResponse) => void
  onDelete?: (entry: UserEntryResponse) => void
}

function EntryRow({ entry, compact = false, onEdit, onDelete }: EntryRowProps) {
  const { t } = useI18n()

  return (
    <li className={styles.row}>
      <MediaCover url={entry.coverUrl} title={entry.title} width={compact ? 40 : 64} />
      <div className={styles.body}>
        <Stack direction="horizontal" align="center" gap="condensed" wrap="wrap">
          <Link as={RouterLink} to={`/entries/${entry.id}`} className={styles.title}>
            {entry.title}
          </Link>
          <StatusLabel status={entry.status} />
          {entry.rating !== null && (
            <span className={styles.meta}>
              <StarFillIcon size={12} /> {entry.rating}/10
            </span>
          )}
        </Stack>
        <div className={styles.meta}>{mediaMeta(entry, t)}</div>
        {!compact && entry.note && <p className={styles.description}>{entry.note}</p>}
      </div>
      {(onEdit || onDelete) && (
        <div className={styles.actions}>
          <ActionMenu>
            <ActionMenu.Anchor>
              <IconButton icon={KebabHorizontalIcon} variant="invisible" aria-label={t.media.actionsFor(entry.title)} />
            </ActionMenu.Anchor>
            <ActionMenu.Overlay align="end">
              <ActionList>
                {onEdit && (
                  <ActionList.Item onSelect={() => onEdit(entry)}>
                    <ActionList.LeadingVisual>
                      <PencilIcon />
                    </ActionList.LeadingVisual>
                    {t.common.edit}
                  </ActionList.Item>
                )}
                {onDelete && (
                  <ActionList.Item variant="danger" onSelect={() => onDelete(entry)}>
                    <ActionList.LeadingVisual>
                      <TrashIcon />
                    </ActionList.LeadingVisual>
                    {t.common.remove}
                  </ActionList.Item>
                )}
              </ActionList>
            </ActionMenu.Overlay>
          </ActionMenu>
        </div>
      )}
    </li>
  )
}

export default EntryRow
