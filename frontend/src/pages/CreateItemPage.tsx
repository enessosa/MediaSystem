function CreateItemPage() {
  return (
    <div>
      <h1>Medium anlegen</h1>
      <p style={{ marginTop: 8 }}>
        Manuelles Anlegen ohne externe Quelle (create item).
      </p>

      <div
        className="card"
        style={{
          marginTop: 24,
          maxWidth: 520,
          display: 'flex',
          flexDirection: 'column',
          gap: 16,
        }}
      >
        <div className="field">
          <label htmlFor="ci-title">Titel</label>
          <input id="ci-title" className="input" type="text" />
        </div>

        <div className="field">
          <label htmlFor="ci-type">Typ</label>
          <select id="ci-type" className="input">
            <option>Anime</option>
            <option>Manga</option>
            <option>Buch</option>
            <option>Serie</option>
          </select>
        </div>

        <div className="field">
          <label htmlFor="ci-note">Notiz</label>
          <textarea id="ci-note" className="input" rows={3} />
        </div>

        <button type="button" className="btn btn--primary">
          Anlegen
        </button>
      </div>
    </div>
  )
}

export default CreateItemPage