import { apiFetch } from './client'

export const CODEWORD_PATH = '/admin/codeword'

/** null, wenn kein Codewort gesetzt ist – dann ist die Registrierung offen. */
export interface CodewordResponse {
  codeword: string | null
}

export function updateCodeword(codeword: string): Promise<void> {
  return apiFetch<void>(CODEWORD_PATH, {
    method: 'PUT',
    body: JSON.stringify({ codeword }),
  })
}

export function deleteCodeword(): Promise<void> {
  return apiFetch<void>(CODEWORD_PATH, { method: 'DELETE' })
}
