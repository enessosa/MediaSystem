import type { Translations } from '../i18n/translations'

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080'

export class ApiError extends Error {
  status: number

  constructor(status: number, message: string) {
    super(message)
    this.status = status
  }
}

let onUnauthorized: () => void = () => {}

export function setUnauthorizedHandler(handler: () => void) {
  onUnauthorized = handler
}

function readCsrfToken(): string | null {
  const match = document.cookie.match(/(?:^|;\s*)XSRF-TOKEN=([^;]+)/)
  return match ? decodeURIComponent(match[1]) : null
}

export async function apiFetch<T>(path: string, options: RequestInit = {}): Promise<T> {
  const csrfToken = readCsrfToken()

  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    credentials: 'include',
    headers: {
      'Content-Type': 'application/json',
      ...(csrfToken ? { 'X-XSRF-TOKEN': csrfToken } : {}),
      ...options.headers,
    },
  })

  if (!response.ok) {
    if ((response.status === 401 || response.status === 403) && !path.startsWith('/auth/')) {
      onUnauthorized()
    }
    const message = await response.text()
    throw new ApiError(response.status, message || response.statusText)
  }

  if (response.status === 204) {
    return undefined as T
  }

  return (await response.json()) as T
}

export function describeError(err: unknown, t: Translations): string {
  if (err instanceof ApiError) {
    // 503: Suche für die gewählten Typen deaktiviert – das Backend liefert eine passende Meldung mit.
    if (err.status === 503 && err.message) return err.message
    if (err.status === 502) return t.errors.serviceUnavailable
    if (err.status === 404) return t.errors.notFound
    if (err.status === 401 || err.status === 403) return t.errors.sessionExpired
  }
  return t.errors.generic
}
