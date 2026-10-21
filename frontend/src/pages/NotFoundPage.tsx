import { Blankslate } from '@primer/react/experimental'
import { useNavigate } from 'react-router'
import styles from './TextPage.module.css'

function NotFoundPage() {
  const navigate = useNavigate()

  return (
    <div className={styles.page}>
      <Blankslate spacious>
        <Blankslate.Heading as="h1">Seite nicht gefunden</Blankslate.Heading>
        <Blankslate.Description>Diese Adresse gibt es nicht (mehr).</Blankslate.Description>
        <Blankslate.PrimaryAction onClick={() => navigate('/')}>Zur Startseite</Blankslate.PrimaryAction>
      </Blankslate>
    </div>
  )
}

export default NotFoundPage
