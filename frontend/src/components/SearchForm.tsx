import { SearchIcon } from '@primer/octicons-react'
import { Button, Stack, TextInput } from '@primer/react'
import { useState, type FormEvent } from 'react'

interface SearchFormProps {
  initialQuery?: string
  onSearch: (query: string) => void
  showButton?: boolean
}

function SearchForm({ initialQuery = '', onSearch, showButton = false }: SearchFormProps) {
  const [query, setQuery] = useState(initialQuery)

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
            aria-label="Medien suchen"
            placeholder="Anime, Serien, Filme, Bücher, Spiele..."
            leadingVisual={SearchIcon}
            size="large"
            block
            value={query}
            onChange={(event) => setQuery(event.target.value)}
          />
        </Stack.Item>
        {showButton && (
          <Button type="submit" variant="primary" size="large">
            Suchen
          </Button>
        )}
      </Stack>
    </form>
  )
}

export default SearchForm
