import { ImageIcon } from '@primer/octicons-react'
import { useState } from 'react'
import { useI18n } from '../i18n/useI18n'
import styles from './MediaCover.module.css'

interface MediaCoverProps {
  url: string | null
  title: string
  width: number
}

function MediaCover({ url, title, width }: MediaCoverProps) {
  const [failed, setFailed] = useState(false)
  const { t } = useI18n()

  if (!url || failed) {
    return (
      <div className={`${styles.cover} ${styles.placeholder}`} style={{ width }} aria-hidden="true">
        <ImageIcon size={Math.round(width / 3)} />
      </div>
    )
  }

  return (
    <img
      className={styles.cover}
      style={{ width }}
      src={url}
      alt={t.media.coverOf(title)}
      loading="lazy"
      onError={() => setFailed(true)}
    />
  )
}

export default MediaCover
