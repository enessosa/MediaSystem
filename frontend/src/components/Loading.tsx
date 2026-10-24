import { Spinner, Stack } from '@primer/react'
import { useI18n } from '../i18n/useI18n'

function Loading({ label }: { label?: string }) {
  const { t } = useI18n()
  return (
    <Stack align="center" padding="spacious">
      <Spinner srText={label ?? t.common.loading} />
    </Stack>
  )
}

export default Loading
