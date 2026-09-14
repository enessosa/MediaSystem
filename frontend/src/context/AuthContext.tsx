import { createContext, useContext, useState, type ReactNode } from 'react'
import { loginUser, registerUser, type LoginPayload, type RegisterPayload } from '../api/auth'

const FLAG_KEY = 'ms_authenticated'
const IS_ADMIN_KEY = 'ms_is_admin'

interface AuthContextValue {
  isAuthenticated: boolean
  isAdmin: boolean
  login: (payload: LoginPayload) => Promise<void>
  register: (payload: RegisterPayload) => Promise<void>
  logout: () => void
}

const AuthContext = createContext<AuthContextValue | null>(null)

/**
 * Die echte Auth-Session ist ein HttpOnly-Cookie (Server-Side Session, ADR-005) –
 * JavaScript kann dessen Inhalt nicht lesen. Dieses Flag ist nur ein UX-Hinweis
 * ("hat der Nutzer zuletzt erfolgreich eingeloggt"), keine Sicherheitsgrenze.
 * Die eigentliche Prüfung passiert bei jedem Request serverseitig.
 */
export function AuthProvider({ children }: { children: ReactNode }) {
  const [isAuthenticated, setIsAuthenticated] = useState(
    () => sessionStorage.getItem(FLAG_KEY) === 'true',
  )
  const [isAdmin, setIsAdmin] = useState(
    () => sessionStorage.getItem(IS_ADMIN_KEY) === 'true',
  )

  async function login(payload: LoginPayload) {
    const response = await loginUser(payload)
    const admin = response.authorities.some((a) => a.authority === 'ROLE_ADMIN')
    sessionStorage.setItem(FLAG_KEY, 'true')
    sessionStorage.setItem(IS_ADMIN_KEY, String(admin))
    setIsAuthenticated(true)
    setIsAdmin(admin)
  }

  async function register(payload: RegisterPayload) {
    await registerUser(payload)
  }

  function logout() {
    sessionStorage.removeItem(FLAG_KEY)
    sessionStorage.removeItem(IS_ADMIN_KEY)
    setIsAuthenticated(false)
    setIsAdmin(false)
  }

  return (
    <AuthContext.Provider value={{ isAuthenticated, isAdmin, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

// eslint-disable-next-line react-refresh/only-export-components -- Hook gehört fachlich zum Context, kein separates File nötig für ein Ein-Personen-Projekt.
export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) {
    throw new Error('useAuth muss innerhalb von <AuthProvider> verwendet werden')
  }
  return ctx
}