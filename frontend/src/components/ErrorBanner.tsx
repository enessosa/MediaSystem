import { Banner } from '@primer/react'
import { describeError } from '../api/client'
import { useI18n } from '../i18n/useI18n'

interface ErrorBannerProps {
  error: unknown
  onRetry?: () => void
}

function ErrorBanner({ error, onRetry }: ErrorBannerProps) {
  const { t } = useI18n()
  return (
    <Banner
      variant="critical"
      title={t.common.error}
      hideTitle
      description={describeError(error, t)}
      primaryAction={onRetry && <Banner.PrimaryAction onClick={onRetry}>{t.common.retry}</Banner.PrimaryAction>}
    />
  )
}

export default ErrorBanner
