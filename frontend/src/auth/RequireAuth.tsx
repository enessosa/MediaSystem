import { Navigate, Outlet, useLocation } from 'react-router'
import { useAuth } from './useAuth'

function RequireAuth() {
  const { user } = useAuth()
  const location = useLocation()

  if (!user) {
    return <Navigate to="/login" replace state={{ from: location.pathname + location.search }} />
  }
  return <Outlet />
}

export default RequireAuth
