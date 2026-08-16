import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import Icon from '../components/Icon'
import './AppLayout.css'

const navItems = [
  { to: '/', label: 'Dashboard', end: true },
  { to: '/watchlists', label: 'Watchlists' },
  { to: '/search', label: 'Suche' },
  { to: '/discovery', label: 'Discovery' },
]

function AppLayout() {
  const navigate = useNavigate()
  const { logout } = useAuth()

  // logout = Header-Aktion, keine eigene Scene
  function handleLogout() {
    logout()
    navigate('/login', { replace: true })
  }

  return (
    <div className="app-shell">
      <header className="app-header">
        <NavLink to="/" className="app-logo">
          MediaSystem
        </NavLink>

        <nav className="app-nav">
          {navItems.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              end={item.end}
              className={({ isActive }) =>
                'app-nav__link' + (isActive ? ' app-nav__link--active' : '')
              }
            >
              {item.label}
            </NavLink>
          ))}
        </nav>

        <div className="app-header__actions">
          <NavLink to="/create" className="btn btn--primary app-header__create">
            <Icon name="plus" size={16} />
            Anlegen
          </NavLink>
          <NavLink to="/profile" className="app-nav__link">
            Profil
          </NavLink>
          <NavLink to="/settings" className="app-nav__link">
            Einstellungen
          </NavLink>
          <button type="button" className="btn" onClick={handleLogout}>
            <Icon name="logout" size={16} />
          </button>
        </div>
      </header>

      <main className="app-main">
        {/* Hier wird die aktive Scene gerendert */}
        <Outlet />
      </main>
    </div>
  )
}

export default AppLayout