import { createContext } from 'react'

export interface AuthUser {
  username: string
  email: string
  isAdmin: boolean
}

export interface AuthContextValue {
  user: AuthUser | null
  /** true, solange beim App-Start noch gefragt wird, ob eine Session besteht. */
  isLoading: boolean
  login: (identifier: string, password: string) => Promise<void>
  logout: () => Promise<void>
}

export const AuthContext = createContext<AuthContextValue | null>(null)
