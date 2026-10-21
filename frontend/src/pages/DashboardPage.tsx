import { Button, Heading, Stack } from '@primer/react'
import { Blankslate } from '@primer/react/experimental'
import { ListUnorderedIcon } from '@primer/octicons-react'
import { Link as RouterLink, useNavigate } from 'react-router'
import { ENTRIES_PATH, type UserEntryResponse } from '../api/media'
import { useAuth } from '../auth/useAuth'
import EntryRow from '../components/EntryRow'
import ErrorBanner from '../components/ErrorBanner'
import listStyles from '../components/ItemList.module.css'
import Loading from '../components/Loading'
import SearchForm from '../components/SearchForm'
import { useApi } from '../hooks/useApi'
import styles from './DashboardPage.module.css'

const PREVIEW_SIZE = 5

function newestFirst(a: UserEntryResponse, b: UserEntryResponse): number {
  return b.addedAt.localeCompare(a.addedAt)
}

function DashboardPage() {
  const { user } = useAuth()
  const navigate = useNavigate()
  const { data: entries, error, isLoading, reload } = useApi<UserEntryResponse[]>(ENTRIES_PATH)

  const recent = entries ? [...entries].sort(newestFirst).slice(0, PREVIEW_SIZE) : []

  return (
    <Stack gap="spacious">
      <Heading as="h1">Willkommen zurück, {user?.username}</Heading>

      <section aria-labelledby="dashboard-search">
        <Heading as="h2" id="dashboard-search" variant="small" className={styles.sectionTitle}>
          Neues Medium finden
        </Heading>
        <SearchForm showButton onSearch={(query) => navigate(`/search?q=${encodeURIComponent(query)}`)} />
      </section>

      <section aria-labelledby="dashboard-list">
        <Stack direction="horizontal" align="center" justify="space-between" className={styles.sectionTitle}>
          <Heading as="h2" id="dashboard-list" variant="small">
            Zuletzt hinzugefügt
          </Heading>
          {entries && entries.length > 0 && (
            <Button as={RouterLink} to="/list" size="small" variant="invisible">
              Alle {entries.length} anzeigen
            </Button>
          )}
        </Stack>

        {isLoading && <Loading />}
        {error !== null && <ErrorBanner error={error} onRetry={reload} />}
        {entries && entries.length === 0 && (
          <Blankslate border>
            <Blankslate.Visual>
              <ListUnorderedIcon size="medium" />
            </Blankslate.Visual>
            <Blankslate.Heading>Deine Liste ist noch leer</Blankslate.Heading>
            <Blankslate.Description>Such oben nach einem Anime, Film, Buch oder Spiel und füge es hinzu.</Blankslate.Description>
          </Blankslate>
        )}
        {recent.length > 0 && (
          <ul className={listStyles.list}>
            {recent.map((entry) => (
              <EntryRow key={entry.id} entry={entry} compact />
            ))}
          </ul>
        )}
      </section>
    </Stack>
  )
}

export default DashboardPage
