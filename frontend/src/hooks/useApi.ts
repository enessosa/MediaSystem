import { useCallback, useEffect, useState } from 'react'
import { apiFetch } from '../api/client'

interface ApiState<T> {
  path: string
  version: number
  data: T | null
  error: unknown
}

export function useApi<T>(path: string | null) {
  const [version, setVersion] = useState(0)
  const [state, setState] = useState<ApiState<T> | null>(null)

  useEffect(() => {
    if (path === null) return
    let active = true
    apiFetch<T>(path).then(
      (data) => active && setState({ path, version, data, error: null }),
      (error: unknown) => active && setState({ path, version, data: null, error }),
    )
    return () => {
      active = false
    }
  }, [path, version])

  const reload = useCallback(() => setVersion((v) => v + 1), [])

  const samePath = state !== null && state.path === path
  const isCurrent = samePath && state.version === version
  const data = samePath ? state.data : null

  return {
    data,
    error: isCurrent ? state.error : null,
    isLoading: path !== null && !isCurrent && data === null,
    reload,
  }
}
