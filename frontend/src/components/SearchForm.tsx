import { SearchIcon } from '@primer/octicons-react'
import { Button, Stack, TextInput } from '@primer/react'
import { useState, type FormEvent } from 'react'
import { useI18n } from '../i18n/useI18n'

interface SearchFormProps {
  initialQuery?: string
  onSearch: (query: string) => void
  showButton?: boolean
}

function SearchForm({ initialQuery = '', onSearch, showButton = false }: SearchFormProps) {
  const [query, setQuery] = useState(initialQuery)
  const { t } = useI18n()

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    const trimmed = query.trim()
    if (trimmed !== '') onSearch(trimmed)
  }

  return (
    <form role="search" onSubmit={handleSubmit}>
      <Stack direction="horizontal" gap="condensed">
        <Stack.Item grow>
          <TextInput
            aria-label={t.search.label}
            placeholder={t.search.placeholder}
            leadingVisual={SearchIcon}
            size="large"
            block
            value={query}
            onChange={(event) => setQuery(event.target.value)}
          />
        </Stack.Item>
        {showButton && (
          <Button type="submit" variant="primary" size="large">
            {t.search.submit}
          </Button>
        )}
      </Stack>
    </form>
  )
}

export default SearchForm
