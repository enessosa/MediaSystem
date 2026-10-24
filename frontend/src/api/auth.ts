import { apiFetch } from './client'

export interface LoginRequest {
  identifier: string
  password: string
}

export interface LoginResponse {
  name: string
  authorities: { authority: string }[]
}

export interface RegisterRequest {
  username: string
  password: string
  codeword: string | null
}

export interface RegisterResponse {
  id: number
  username: string
  createdAt: string
}

export function login(request: LoginRequest): Promise<LoginResponse> {
  return apiFetch<LoginResponse>('/auth/login', {
    method: 'POST',
    body: JSON.stringify(request),
  })
}

export function register(request: RegisterRequest): Promise<RegisterResponse> {
  return apiFetch<RegisterResponse>('/auth/register', {
    method: 'POST',
    body: JSON.stringify(request),
  })
}

export function logout(): Promise<void> {
  return apiFetch<void>('/logout', { method: 'POST', redirect: 'manual' })
}
