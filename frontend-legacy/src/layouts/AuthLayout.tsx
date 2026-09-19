import { Outlet } from 'react-router-dom'
import './AuthLayout.css'

function AuthLayout() {
  return (
    <div className="auth-shell">
      <div className="auth-card card">
        <div className="auth-brand">MediaSystem</div>
        {/* Login- / Register-Scene wird hier gerendert */}
        <Outlet />
      </div>
    </div>
  )
}

export default AuthLayout