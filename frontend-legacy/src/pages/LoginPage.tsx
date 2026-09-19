import { useState, type FormEvent } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { ApiError } from '../api/client'
import FormError from '../components/FormError'

function LoginPage() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()

  const [identifier, setIdentifier] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [isSubmitting, setIsSubmitting] = useState(false)

  const state = location.state as { from?: Location; justRegistered?: boolean } | null
  const redirectTo = state?.from?.pathname ?? '/'

  async function handleSubmit(e: FormEvent) {
    e.preventDefault()
    setError(null)
    setIsSubmitting(true)
    try {
      await login({ identifier, password })
      navigate(redirectTo, { replace: true })
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Login fehlgeschlagen. Versuch es erneut.')
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
      <h2 style={{ textAlign: 'center' }}>Login</h2>

      <FormError message={error} />
      {!error && state?.justRegistered && (
        <p style={{ textAlign: 'center', fontSize: 14, color: '#0f7b3f' }}>
          Konto erstellt — jetzt einloggen.
        </p>
      )}

      <div className="field">
        <label htmlFor="login-identifier">Username oder E-Mail</label>
        <input
          id="login-identifier"
          className="input"
          type="text"
          value={identifier}
          onChange={(e) => setIdentifier(e.target.value)}
          required
        />
      </div>

      <div className="field">
        <label htmlFor="login-password">Passwort</label>
        <input
          id="login-password"
          className="input"
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
      </div>

      <button type="submit" className="btn btn--primary" disabled={isSubmitting}>
        {isSubmitting ? 'Einloggen…' : 'Einloggen'}
      </button>

      <p style={{ textAlign: 'center', fontSize: 14 }}>
        Noch kein Konto? <Link to="/register">Registrieren</Link>
      </p>
    </form>
  )
}

export default LoginPage
