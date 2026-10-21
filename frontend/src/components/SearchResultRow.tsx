import { Link, Stack } from '@primer/react'
import { Link as RouterLink } from 'react-router'
import type { MediaSearchResult } from '../api/media'
import AddToListButton from './AddToListButton'
import styles from './ItemList.module.css'
import MediaCover from './MediaCover'
import { externalDetailUrl, mediaMeta, plainText } from './mediaMeta'

function SearchResultRow({ item }: { item: MediaSearchResult }) {
  const detailUrl = externalDetailUrl(item)

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
        <div className={styles.meta}>{mediaMeta(item)}</div>
        {item.description && <p className={styles.description}>{plainText(item.description)}</p>}
      </div>
      <div className={styles.actions}>
        <AddToListButton item={item} size="small" />
      </div>
    </li>
  )
}

export default SearchResultRow
