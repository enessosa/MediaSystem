import { PageLayout, Stack, UnderlineNav } from '@primer/react'
import { Link, Outlet, useLocation } from 'react-router'
import styles from './AppLayout.module.css'
import HeaderSearch from './HeaderSearch'
import UserMenu from './UserMenu'

const navItems = [
  { label: 'Dashboard', to: '/' },
  { label: 'Suche', to: '/search' },
  { label: 'Meine Liste', to: '/list' },
]

function isActive(pathname: string, to: string): boolean {
  if (to === '/') return pathname === '/'
  if (to === '/list') return pathname === '/list' || pathname.startsWith('/entries/')
  if (to === '/search') return pathname === '/search' || pathname.startsWith('/media/')
  return pathname === to
}

function AppLayout() {
  const { pathname } = useLocation()

  return (
    <div>
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
        <UnderlineNav aria-label="Hauptnavigation" variant="flush">
          {navItems.map((item) => (
            <UnderlineNav.Item
              key={item.to}
              as={Link}
              to={item.to}
              aria-current={isActive(pathname, item.to) ? 'page' : undefined}
            >
              {item.label}
            </UnderlineNav.Item>
          ))}
        </UnderlineNav>
      </header>
      <PageLayout containerWidth="xlarge">
        <PageLayout.Content as="main">
          <Outlet />
        </PageLayout.Content>
      </PageLayout>
    </div>
  )
}

export default AppLayout
