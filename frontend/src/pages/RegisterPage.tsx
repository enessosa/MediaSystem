import { Link } from 'react-router-dom'

function RegisterPage() {
  return (
    <>
      <h2 style={{ textAlign: 'center' }}>Registrieren</h2>

      <div className="field">
        <label htmlFor="reg-username">Benutzername</label>
        <input id="reg-username" className="input" type="text" />
      </div>

      <div className="field">
        <label htmlFor="reg-email">E-Mail</label>
        <input id="reg-email" className="input" type="email" />
      </div>

      <div className="field">
        <label htmlFor="reg-password">Passwort</label>
        <input id="reg-password" className="input" type="password" />
      </div>

      {/* Codewort: ab dem 11. Signup Pflicht (Signup-Cap).
          Feld kann bedingt eingeblendet werden, sobald das Backend
          meldet, dass die 10 Plätze voll sind. */}
      <div className="field">
        <label htmlFor="reg-code">Codewort (falls erforderlich)</label>
        <input id="reg-code" className="input" type="text" />
      </div>

      <button type="button" className="btn btn--primary">
        Konto erstellen
      </button>

      <p style={{ textAlign: 'center', fontSize: 14 }}>
        Schon registriert? <Link to="/login">Zum Login</Link>
      </p>
    </>
  )
}

export default RegisterPage