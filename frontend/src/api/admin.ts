import { apiFetch } from './client'

export function updateCodeword(codeword: string) {
  return apiFetch<void>('/admin/codeword', {
    method: 'PUT',
    body: JSON.stringify({ codeword }),
  })
}
