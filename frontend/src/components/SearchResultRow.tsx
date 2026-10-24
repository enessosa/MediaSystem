import { Link, Stack } from '@primer/react'
import { Link as RouterLink } from 'react-router'
import type { MediaSearchResult } from '../api/media'
import { useI18n } from '../i18n/useI18n'
import AddToListButton from './AddToListButton'
import styles from './ItemList.module.css'
import MediaCover from './MediaCover'
import { externalDetailUrl, mediaMeta, plainText } from './mediaMeta'

function SearchResultRow({ item, entryId }: { item: MediaSearchResult; entryId?: number }) {
  const detailUrl = externalDetailUrl(item)
  const { t } = useI18n()

  return (
    <li className={styles.row}>
      <MediaCover url={item.coverUrl} title={item.title} width={64} />
      <div className={styles.body}>
        <Stack direction="horizontal" align="center" gap="condensed">
          {detailUrl ? (
            <Link as={RouterLink} to={detailUrl} className={styles.title}>
              {item.title}
            </Link>
          ) : (
            <span className={styles.title}>{item.title}</span>
          )}
        </Stack>
        <div className={styles.meta}>{mediaMeta(item, t)}</div>
        {item.description && <p className={styles.description}>{plainText(item.description)}</p>}
      </div>
      <div className={styles.actions}>
        <AddToListButton item={item} entryId={entryId} size="small" />
      </div>
    </li>
  )
}

export default SearchResultRow
