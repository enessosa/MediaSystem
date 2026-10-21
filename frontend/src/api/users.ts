import { apiFetch } from './client'

export interface MeResponse {
  id: number
  username: string
  email: string
  admin: boolean
}

export function getCurrentUser(): Promise<MeResponse> {
  return apiFetch<MeResponse>('/users/me')
}
