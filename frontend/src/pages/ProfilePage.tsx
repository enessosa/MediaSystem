function ProfilePage() {
  return (
    <div>
      <h1>Profil</h1>
      <p style={{ marginTop: 8 }}>Account-Verwaltung.</p>

      <div className="card" style={{ marginTop: 24, maxWidth: 520 }}>
        <h3>Passwort ändern</h3>
        <div className="field" style={{ margin: '12px 0' }}>
          <label htmlFor="pf-old">Aktuelles Passwort</label>
          <input id="pf-old" className="input" type="password" />
        </div>
        <div className="field" style={{ marginBottom: 12 }}>
          <label htmlFor="pf-new">Neues Passwort</label>
          <input id="pf-new" className="input" type="password" />
        </div>
        <button type="button" className="btn btn--primary">
          Speichern
        </button>
      </div>

      <div className="card" style={{ marginTop: 16, maxWidth: 520 }}>
        <h3>Account löschen</h3>
        <p style={{ margin: '8px 0 12px' }}>
          Löscht dein Konto und alle Einträge unwiderruflich.
        </p>
        <button type="button" className="btn btn--danger">
          Account löschen
        </button>
      </div>
    </div>
  )
}

export default ProfilePage