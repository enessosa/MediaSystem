import { apiFetch } from './client'

export function updateCodeword(codeword: string): Promise<void> {
  return apiFetch<void>('/admin/codeword', {
    method: 'PUT',
    body: JSON.stringify({ codeword }),
  })
}
