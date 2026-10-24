import { PageLayout, Stack, UnderlineNav } from '@primer/react'
import { Link, Outlet, useLocation } from 'react-router'
import { useI18n } from '../i18n/useI18n'
import styles from './AppLayout.module.css'
import HeaderSearch from './HeaderSearch'
import UserMenu from './UserMenu'

const navItems = [
  { key: 'dashboard', to: '/' },
  { key: 'search', to: '/search' },
  { key: 'myList', to: '/list' },
] as const

function isActive(pathname: string, to: string): boolean {
  if (to === '/') return pathname === '/'
  if (to === '/list') return pathname === '/list' || pathname.startsWith('/entries/')
  if (to === '/search') return pathname === '/search' || pathname.startsWith('/media/')
  return pathname === to
}

function AppLayout() {
  const { pathname } = useLocation()
  const { t } = useI18n()

  return (
    <div className={styles.shell}>
      <header className={styles.header}>
        <Stack direction="horizontal" align="center" justify="space-between">
          <Link to="/" className={styles.brand}>
            MediaSystem
          </Link>
          <Stack direction="horizontal" align="center" gap="condensed">
            <HeaderSearch />
            <UserMenu />
          </Stack>
        </Stack>
        <UnderlineNav aria-label={t.layout.mainNav} variant="flush">
          {navItems.map((item) => (
            <UnderlineNav.Item
              key={item.to}
              as={Link}
              to={item.to}
              aria-current={isActive(pathname, item.to) ? 'page' : undefined}
            >
              {t.layout[item.key]}
            </UnderlineNav.Item>
          ))}
        </UnderlineNav>
      </header>
      <PageLayout containerWidth="xlarge" className={styles.main}>
        <PageLayout.Content as="main">
          <Outlet />
        </PageLayout.Content>
      </PageLayout>
      {/* RAWG verlangt einen aktiven Link auf jeder Seite, die ihre Daten zeigt, deshalb global hier. */}
      <footer className={styles.footerBar}>
        <p className={styles.footer}>
          {t.layout.footerDataFrom}{' '}
          <Link to="/about" className={styles.footerLink}>
            TMDB
          </Link>
          ,{' '}
          <a href="https://rawg.io" target="_blank" rel="noreferrer" className={styles.footerLink}>
            RAWG
          </a>
          , {t.layout.footerRest} ·{' '}
          <Link to="/about" className={styles.footerLink}>
            {t.layout.about}
          </Link>
        </p>
      </footer>
    </div>
  )
}

export default AppLayout
