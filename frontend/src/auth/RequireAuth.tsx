import { Navigate, Outlet, useLocation } from 'react-router'
import Loading from '../components/Loading'
import { useI18n } from '../i18n/useI18n'
import { useAuth } from './useAuth'

function RequireAuth() {
  const { user, isLoading } = useAuth()
  const location = useLocation()
  const { t } = useI18n()

  if (isLoading) {
    return <Loading label={t.layout.checkingSession} />
  }
  if (!user) {
    return <Navigate to="/login" replace state={{ from: location.pathname + location.search }} />
  }
  return <Outlet />
}

export default RequireAuth
