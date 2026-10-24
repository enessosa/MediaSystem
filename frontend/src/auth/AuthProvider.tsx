import { useCallback, useEffect, useMemo, useState, type ReactNode } from 'react'
import { login as loginRequest, logout as logoutRequest } from '../api/auth'
import { setUnauthorizedHandler } from '../api/client'
import { getCurrentUser, type MeResponse } from '../api/users'
import { AuthContext, type AuthUser } from './authContext'

function toAuthUser(me: MeResponse): AuthUser {
  return { username: me.username, isAdmin: me.admin }
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(null)
  const [isLoading, setIsLoading] = useState(true)

  // Beim Start den Server fragen, ob das Session-Cookie noch gültig ist (überlebt Reload und neue Tabs).
  useEffect(() => {
    let ignore = false
    getCurrentUser()
      .then((me) => {
        if (!ignore) setUser(toAuthUser(me))
      })
      .catch(() => {
        if (!ignore) setUser(null)
      })
      .finally(() => {
        if (!ignore) setIsLoading(false)
      })
    return () => {
      ignore = true
    }
  }, [])

  useEffect(() => {
    setUnauthorizedHandler(() => setUser(null))
  }, [])

  const login = useCallback(async (identifier: string, password: string) => {
    await loginRequest({ identifier, password })
    setUser(toAuthUser(await getCurrentUser()))
  }, [])

  const logout = useCallback(async () => {
    try {
      await logoutRequest()
    } catch {
      // Session wird serverseitig trotzdem beendet; lokal immer abmelden.
    }
    setUser(null)
  }, [])

  const value = useMemo(() => ({ user, isLoading, login, logout }), [user, isLoading, login, logout])

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}
