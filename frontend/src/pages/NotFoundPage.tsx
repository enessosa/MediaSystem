import { Blankslate } from '@primer/react/experimental'
import { useNavigate } from 'react-router'
import { useI18n } from '../i18n/useI18n'
import styles from './TextPage.module.css'

function NotFoundPage() {
  const navigate = useNavigate()
  const { t } = useI18n()

  return (
    <div className={styles.page}>
      <Blankslate spacious>
        <Blankslate.Heading as="h1">{t.notFound.heading}</Blankslate.Heading>
        <Blankslate.Description>{t.notFound.description}</Blankslate.Description>
        <Blankslate.PrimaryAction onClick={() => navigate('/')}>{t.notFound.toHome}</Blankslate.PrimaryAction>
      </Blankslate>
    </div>
  )
}

export default NotFoundPage
