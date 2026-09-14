import { apiFetch } from './client'

export interface RegisterPayload {
  username: string
  email: string
  password: string
  codeword?: string | null
}

export interface LoginPayload {
  identifier: string
  password: string
}

export interface RegisteredUser {
  id: number
  username: string
  email: string
}

/** Spring Security liefert beim Login das rohe Authentication-Objekt inkl. Rollen zurück. */
export interface LoginResponse {
  authorities: { authority: string }[]
}

export function registerUser(payload: RegisterPayload) {
  return apiFetch<RegisteredUser>('/auth/register', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export function loginUser(payload: LoginPayload) {
  return apiFetch<LoginResponse>('/auth/login', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}