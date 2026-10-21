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

export function changePassword(currentPassword: string, newPassword: string): Promise<void> {
  return apiFetch<void>('/users/me/password', {
    method: 'PUT',
    body: JSON.stringify({ currentPassword, newPassword }),
  })
}

export function deleteAccount(password: string): Promise<void> {
  return apiFetch<void>('/users/me', {
    method: 'DELETE',
    body: JSON.stringify({ password }),
  })
}
