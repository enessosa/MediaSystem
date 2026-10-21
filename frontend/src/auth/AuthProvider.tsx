import { useCallback, useEffect, useMemo, useState, type ReactNode } from 'react'
import { login as loginRequest, logout as logoutRequest } from '../api/auth'
import { setUnauthorizedHandler } from '../api/client'
import { AuthContext, type AuthUser } from './authContext'

const STORAGE_KEY = 'mediasystem.user'

function readStoredUser(): AuthUser | null {
  try {
    const raw = sessionStorage.getItem(STORAGE_KEY)
    return raw ? (JSON.parse(raw) as AuthUser) : null
  } catch {
    return null
  }
}

function storeUser(user: AuthUser | null) {
  try {
    if (user) sessionStorage.setItem(STORAGE_KEY, JSON.stringify(user))
    else sessionStorage.removeItem(STORAGE_KEY)
  } catch {
    // Speichern ist nur Komfort (überlebt Reload) – ohne Storage funktioniert die App trotzdem.
  }
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(readStoredUser)

  const updateUser = useCallback((next: AuthUser | null) => {
    storeUser(next)
    setUser(next)
  }, [])

  useEffect(() => {
    setUnauthorizedHandler(() => updateUser(null))
  }, [updateUser])

  const login = useCallback(
    async (identifier: string, password: string) => {
      const response = await loginRequest({ identifier, password })
      updateUser({
        username: response.name,
        isAdmin: response.authorities.some((a) => a.authority === 'ROLE_ADMIN'),
      })
    },
    [updateUser],
  )

  const logout = useCallback(async () => {
    try {
      await logoutRequest()
    } catch {
      // Session wird serverseitig trotzdem beendet; lokal immer abmelden.
    }
    updateUser(null)
  }, [updateUser])

  const value = useMemo(() => ({ user, login, logout }), [user, login, logout])

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}
