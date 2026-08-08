import { useState } from 'react'
import { Link } from 'react-router-dom'

function LoginPage() {
  // Sub-Flow "reset password" lebt in dieser Scene (kein eigener Screen)
  const [mode, setMode] = useState<'login' | 'reset'>('login')

  if (mode === 'reset') {
    return (
      <>
        <h2 style={{ textAlign: 'center' }}>Passwort zurücksetzen</h2>
        <div className="field">
          <label htmlFor="reset-email">E-Mail</label>
          <input id="reset-email" className="input" type="email" />
        </div>
        <button type="button" className="btn btn--primary">
          Reset-Link senden
        </button>
        <button
          type="button"
          className="btn"
          onClick={() => setMode('login')}
        >
          Zurück zum Login
        </button>
      </>
    )
  }

  return (
    <>
      <h2 style={{ textAlign: 'center' }}>Login</h2>

      <div className="field">
        <label htmlFor="login-email">E-Mail</label>
        <input id="login-email" className="input" type="email" />
      </div>

      <div className="field">
        <label htmlFor="login-password">Passwort</label>
        <input id="login-password" className="input" type="password" />
      </div>

      <button type="button" className="btn btn--primary">
        Einloggen
      </button>

      <button
        type="button"
        className="btn"
        style={{ border: 'none', background: 'transparent' }}
        onClick={() => setMode('reset')}
      >
        Passwort vergessen?
      </button>

      <p style={{ textAlign: 'center', fontSize: 14 }}>
        Noch kein Konto? <Link to="/register">Registrieren</Link>
      </p>
    </>
  )
}

export default LoginPage