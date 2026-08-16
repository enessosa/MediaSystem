import { createContext, useContext, useState, type ReactNode } from 'react'
import { loginUser, registerUser, type LoginPayload, type RegisterPayload } from '../api/auth'

const FLAG_KEY = 'ms_authenticated'

interface AuthContextValue {
  isAuthenticated: boolean
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

  async function login(payload: LoginPayload) {
    await loginUser(payload)
    sessionStorage.setItem(FLAG_KEY, 'true')
    setIsAuthenticated(true)
  }

  async function register(payload: RegisterPayload) {
    await registerUser(payload)
  }

  function logout() {
    sessionStorage.removeItem(FLAG_KEY)
    setIsAuthenticated(false)
  }

  return (
    <AuthContext.Provider value={{ isAuthenticated, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) {
    throw new Error('useAuth muss innerhalb von <AuthProvider> verwendet werden')
  }
  return ctx
}