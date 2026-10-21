import { Navigate, Outlet, useLocation } from 'react-router'
import Loading from '../components/Loading'
import { useAuth } from './useAuth'

function RequireAuth() {
  const { user, isLoading } = useAuth()
  const location = useLocation()

  if (isLoading) {
    return <Loading label="Anmeldung wird geprüft..." />
  }
  if (!user) {
    return <Navigate to="/login" replace state={{ from: location.pathname + location.search }} />
  }
  return <Outlet />
}

export default RequireAuth
