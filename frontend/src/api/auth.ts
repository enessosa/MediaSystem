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

export function registerUser(payload: RegisterPayload) {
  return apiFetch<RegisteredUser>('/auth/register', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}

export function loginUser(payload: LoginPayload) {
  return apiFetch<unknown>('/auth/login', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}