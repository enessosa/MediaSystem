import type { ReactNode } from 'react'
import Icon from './Icon'
import './EmptyState.css'

interface EmptyStateProps {
  icon?: 'search' | 'sparkles'
  title: string
  description?: string
  action?: ReactNode
}

function EmptyState({ icon = 'sparkles', title, description, action }: EmptyStateProps) {
  return (
    <div className="empty-state">
      <div className="empty-state__icon">
        <Icon name={icon} size={22} />
      </div>
      <h3>{title}</h3>
      {description && <p>{description}</p>}
      {action}
    </div>
  )
}

export default EmptyState