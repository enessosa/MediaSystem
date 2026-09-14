import { useState, type FormEvent } from 'react'
import { useAuth } from '../context/AuthContext'
import { updateCodeword } from '../api/admin'
import { ApiError } from '../api/client'
import FormError from '../components/FormError'

function SettingsPage() {
  const { isAdmin } = useAuth()

  const [codeword, setCodeword] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [success, setSuccess] = useState(false)
  const [isSubmitting, setIsSubmitting] = useState(false)

  async function handleCodewordSubmit(e: FormEvent) {
    e.preventDefault()
    setError(null)
    setSuccess(false)
    setIsSubmitting(true)
    try {
      await updateCodeword(codeword)
      setSuccess(true)
      setCodeword('')
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Codewort konnte nicht gespeichert werden.')
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <div>
      <h1>Einstellungen</h1>

      <div className="card" style={{ marginTop: 24, maxWidth: 520 }}>
        <h3>Passwort ändern</h3>
        <div className="field" style={{ margin: '12px 0' }}>
          <label htmlFor="set-new">Neues Passwort</label>
          <input id="set-new" className="input" type="password" />
        </div>
        <button type="button" className="btn btn--primary">
          Speichern
        </button>
      </div>

      {isAdmin && (
        <form className="card" style={{ marginTop: 16, maxWidth: 520 }} onSubmit={handleCodewordSubmit}>
          <h3>Admin: Codewort setzen</h3>
          <p style={{ margin: '8px 0 12px' }}>
            Pflicht-Code ab dem 11. Signup.
          </p>
          <FormError message={error} />
          {success && (
            <p style={{ margin: '0 0 12px', fontSize: 14, color: '#0f7b3f' }}>
              Codewort gespeichert.
            </p>
          )}
          <div className="field" style={{ marginBottom: 12 }}>
            <label htmlFor="set-code">Codewort</label>
            <input
              id="set-code"
              className="input"
              type="text"
              value={codeword}
              onChange={(e) => setCodeword(e.target.value)}
              required
            />
          </div>
          <button type="submit" className="btn btn--primary" disabled={isSubmitting}>
            {isSubmitting ? 'Speichern…' : 'Codewort speichern'}
          </button>
        </form>
      )}

      <div className="card" style={{ marginTop: 16, maxWidth: 520 }}>
        <h3>Account löschen</h3>
        <button
          type="button"
          className="btn btn--danger"
          style={{ marginTop: 12 }}
        >
          Account löschen
        </button>
      </div>
    </div>
  )
}

export default SettingsPage
