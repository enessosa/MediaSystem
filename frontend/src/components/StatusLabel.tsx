import { Label, type LabelProps } from '@primer/react'
import { STATUS_LABELS, type Status } from '../api/media'

const VARIANTS: Record<Status, LabelProps['variant']> = {
  WATCHING: 'accent',
  PLANNED: 'secondary',
  COMPLETED: 'success',
  DROPPED: 'danger',
}

function StatusLabel({ status }: { status: Status }) {
  return <Label variant={VARIANTS[status]}>{STATUS_LABELS[status]}</Label>
}

export default StatusLabel
