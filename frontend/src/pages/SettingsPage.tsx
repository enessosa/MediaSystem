function SettingsPage() {
  // Sichtbarkeit des Admin-Bereichs später an die Admin-Rolle koppeln
  const isAdmin = true

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
        <div className="card" style={{ marginTop: 16, maxWidth: 520 }}>
          <h3>Admin: Codewort setzen</h3>
          <p style={{ margin: '8px 0 12px' }}>
            Pflicht-Code ab dem 11. Signup (setCodeword).
          </p>
          <div className="field" style={{ marginBottom: 12 }}>
            <label htmlFor="set-code">Codewort</label>
            <input id="set-code" className="input" type="text" />
          </div>
          <button type="button" className="btn btn--primary">
            Codewort speichern
          </button>
        </div>
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