import { Heading, Link, Stack } from '@primer/react'
import tmdbLogo from '../assets/tmdb-logo.svg'
import PageTitle from '../components/PageTitle'
import { useI18n } from '../i18n/useI18n'
import styles from './AboutPage.module.css'

// Quellenangaben, die die Nutzungsbedingungen von TMDB und RAWG verlangen.
// TMDB: Logo + Pflichtsatz in einem "About"/"Credits"-Bereich, Logo kleiner als der App-Name.
// RAWG: Quelle nennen + aktiver Link (zusätzlich in der Fußzeile jeder Seite, siehe AppLayout).
function AboutPage() {
  const { t } = useI18n()

  return (
    <Stack gap="spacious">
      <PageTitle title={t.about.title} description={t.about.description} />

      <section className={styles.box} aria-labelledby="about-sources">
        <Heading as="h2" variant="small" id="about-sources" className={styles.boxTitle}>
          {t.about.sources}
        </Heading>
        <Stack gap="normal">
          <p className={styles.text}>{t.about.sourcesIntro}</p>

          <div className={styles.source}>
            <Link href="https://www.themoviedb.org" target="_blank" rel="noreferrer">
              <img src={tmdbLogo} alt="TMDB" className={styles.tmdbLogo} />
            </Link>
            <p className={styles.text}>
              {/* Pflichtsatz aus den TMDB Terms, bewusst in beiden Sprachen im englischen Original. */}
              {t.about.tmdb} This product uses TMDB and the TMDB APIs but is not endorsed, certified, or otherwise
              approved by TMDB.
            </p>
          </div>

          <div className={styles.source}>
            <Link href="https://rawg.io" target="_blank" rel="noreferrer" className={styles.sourceName}>
              RAWG
            </Link>
            <p className={styles.text}>{t.about.rawg}</p>
          </div>

          <div className={styles.source}>
            <Link href="https://anilist.co" target="_blank" rel="noreferrer" className={styles.sourceName}>
              AniList
            </Link>
            <p className={styles.text}>{t.about.anilist}</p>
          </div>

          <div className={styles.source}>
            <Link href="https://openlibrary.org" target="_blank" rel="noreferrer" className={styles.sourceName}>
              Open Library
            </Link>
            <p className={styles.text}>{t.about.openLibrary}</p>
          </div>
        </Stack>
      </section>

      <section className={styles.box} aria-labelledby="about-project">
        <Heading as="h2" variant="small" id="about-project" className={styles.boxTitle}>
          {t.about.project}
        </Heading>
        <p className={styles.text}>
          {t.about.projectBefore}{' '}
          <Link href="https://github.com/enessosa/MediaSystem" target="_blank" rel="noreferrer">
            GitHub
          </Link>
          {t.about.projectAfter}
        </p>
      </section>
    </Stack>
  )
}

export default AboutPage
