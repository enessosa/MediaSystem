import { createContext } from 'react'

export interface AuthUser {
  username: string
  isAdmin: boolean
}

export interface AuthContextValue {
  user: AuthUser | null
  login: (identifier: string, password: string) => Promise<void>
  logout: () => Promise<void>
}

export const AuthContext = createContext<AuthContextValue | null>(null)
