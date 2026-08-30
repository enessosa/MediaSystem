const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080'

export class ApiError extends Error {
  status: number

  constructor(status: number, message: string) {
    super(message)
    this.status = status
  }
}

/**
 * Liest den XSRF-TOKEN-Cookie aus (von SecurityConfig/CsrfCookieFilter bei jedem Request gesetzt,
 * kein HttpOnly, damit JS ihn lesen kann). Muss bei state-verändernden Requests (POST/PUT/DELETE)
 * als X-XSRF-TOKEN-Header mitgeschickt werden, sonst 403 (/auth/** ist davon explizit ausgenommen).
 */
function readCsrfCookie(): string | null {
  const match = document.cookie.match(/(?:^|;\s*)XSRF-TOKEN=([^;]+)/)
  return match ? decodeURIComponent(match[1]) : null
}

/**
 * Kleiner fetch-Wrapper für Aufrufe gegen das Backend.
 * - schickt Cookies mit (`credentials: 'include'`), damit die Server-Side Session (ADR-005) funktioniert
 * - schickt den CSRF-Token-Header mit, falls ein Cookie vorhanden ist (nötig für POST/PUT/DELETE außerhalb /auth/**)
 * - Backend liefert Fehler mal als JSON (Bean-Validation), mal als reiner Text (GlobalExceptionHandler) -> beides abfangen
 */
export async function apiFetch<T>(path: string, options: RequestInit = {}): Promise<T> {
  const csrfToken = readCsrfCookie()

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
    const message = await extractErrorMessage(response)
    throw new ApiError(response.status, message)
  }

  if (response.status === 204) {
    return undefined as T
  }

  return (await response.json()) as T
}

async function extractErrorMessage(response: Response): Promise<string> {
  const contentType = response.headers.get('content-type') ?? ''
  try {
    if (contentType.includes('application/json')) {
      const body = await response.json()
      if (Array.isArray(body?.errors) && body.errors.length > 0) {
        return body.errors.map((e: { defaultMessage?: string }) => e.defaultMessage).join(', ')
      }
      return body?.message ?? response.statusText
    }
    const text = await response.text()
    return text || response.statusText
  } catch {
    return response.statusText
  }
}
