import { Spinner, Stack } from '@primer/react'

function Loading({ label = 'Wird geladen...' }: { label?: string }) {
  return (
    <Stack align="center" padding="spacious">
      <Spinner srText={label} />
    </Stack>
  )
}

export default Loading
