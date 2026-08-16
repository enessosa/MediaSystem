import { STATUS_LABELS, type WatchStatus } from '../types/media'
import './StatusBadge.css'

function StatusBadge({ status }: { status: WatchStatus }) {
  return <span className={`status-badge status-badge--${status.toLowerCase()}`}>{STATUS_LABELS[status]}</span>
}

export default StatusBadge
