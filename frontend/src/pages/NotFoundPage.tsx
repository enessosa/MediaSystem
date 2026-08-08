import { Link } from 'react-router-dom'

function NotFoundPage() {
  return (
    <div style={{ textAlign: 'center', padding: '80px 24px' }}>
      <h1>404</h1>
      <p style={{ margin: '12px 0 24px' }}>Diese Seite gibt es nicht.</p>
      <Link to="/" className="btn btn--primary">
        Zum Dashboard
      </Link>
    </div>
  )
}

export default NotFoundPage