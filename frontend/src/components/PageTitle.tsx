import { PageHeader } from '@primer/react'
import type { ReactNode } from 'react'

interface PageTitleProps {
  title: string
  description?: ReactNode
  actions?: ReactNode
}

function PageTitle({ title, description, actions }: PageTitleProps) {
  return (
    <PageHeader aria-label={title}>
      <PageHeader.TitleArea>
        <PageHeader.Title as="h1">{title}</PageHeader.Title>
      </PageHeader.TitleArea>
      {actions && <PageHeader.Actions>{actions}</PageHeader.Actions>}
      {description && <PageHeader.Description>{description}</PageHeader.Description>}
    </PageHeader>
  )
}

export default PageTitle
