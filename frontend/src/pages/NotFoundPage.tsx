import { Link } from 'react-router-dom'
import EmptyState from '../components/EmptyState'

function NotFoundPage() {
  return (
    <div style={{ padding: '80px 24px' }}>
      <EmptyState
        icon="search"
        title="404 – Diese Seite gibt es nicht"
        action={
          <Link to="/" className="btn btn--primary" style={{ marginTop: 8 }}>
            Zum Dashboard
          </Link>
        }
      />
    </div>
  )
}

export default NotFoundPage