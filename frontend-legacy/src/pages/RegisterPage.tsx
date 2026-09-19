import { useState, type FormEvent } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { ApiError } from '../api/client'
import FormError from '../components/FormError'

function RegisterPage() {
  const { register } = useAuth()
  const navigate = useNavigate()

  const [username, setUsername] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [codeword, setCodeword] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [isSubmitting, setIsSubmitting] = useState(false)

  async function handleSubmit(e: FormEvent) {
    e.preventDefault()
    setError(null)
    setIsSubmitting(true)
    try {
      await register({ username, email, password, codeword: codeword || null })
      navigate('/login', { state: { justRegistered: true } })
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Registrierung fehlgeschlagen. Versuch es erneut.')
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
      <h2 style={{ textAlign: 'center' }}>Registrieren</h2>

      <FormError message={error} />

      <div className="field">
        <label htmlFor="reg-username">Benutzername</label>
        <input
          id="reg-username"
          className="input"
          type="text"
          minLength={4}
          maxLength={15}
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          required
        />
      </div>

      <div className="field">
        <label htmlFor="reg-email">E-Mail</label>
        <input
          id="reg-email"
          className="input"
          type="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />
      </div>

      <div className="field">
        <label htmlFor="reg-password">Passwort</label>
        <input
          id="reg-password"
          className="input"
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
      </div>

      {/* Codewort: ab dem 11. Signup Pflicht (Signup-Cap). Optional, wird nur
          geprüft, wenn die ersten 10 Plätze schon belegt sind. */}
      <div className="field">
        <label htmlFor="reg-code">Codewort (falls erforderlich)</label>
        <input
          id="reg-code"
          className="input"
          type="text"
          value={codeword}
          onChange={(e) => setCodeword(e.target.value)}
        />
      </div>

      <button type="submit" className="btn btn--primary" disabled={isSubmitting}>
        {isSubmitting ? 'Wird erstellt…' : 'Konto erstellen'}
      </button>

      <p style={{ textAlign: 'center', fontSize: 14 }}>
        Schon registriert? <Link to="/login">Zum Login</Link>
      </p>
    </form>
  )
}

export default RegisterPage
