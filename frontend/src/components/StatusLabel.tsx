import { Label, type LabelProps } from '@primer/react'
import type { Status } from '../api/media'
import { useI18n } from '../i18n/useI18n'

const VARIANTS: Record<Status, LabelProps['variant']> = {
  WATCHING: 'accent',
  PLANNED: 'secondary',
  COMPLETED: 'success',
  DROPPED: 'danger',
}

function StatusLabel({ status }: { status: Status }) {
  const { t } = useI18n()
  return <Label variant={VARIANTS[status]}>{t.statuses[status]}</Label>
}

export default StatusLabel
