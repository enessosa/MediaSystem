import { Banner } from '@primer/react'
import { describeError } from '../api/client'

interface ErrorBannerProps {
  error: unknown
  onRetry?: () => void
}

function ErrorBanner({ error, onRetry }: ErrorBannerProps) {
  return (
    <Banner
      variant="critical"
      title="Fehler"
      hideTitle
      description={describeError(error)}
      primaryAction={onRetry && <Banner.PrimaryAction onClick={onRetry}>Erneut versuchen</Banner.PrimaryAction>}
    />
  )
}

export default ErrorBanner
