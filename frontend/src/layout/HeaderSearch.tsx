import { SearchIcon } from '@primer/octicons-react'
import { Button, Dialog, VisuallyHidden } from '@primer/react'
import { useState } from 'react'
import { useNavigate } from 'react-router'
import SearchForm from '../components/SearchForm'
import styles from './HeaderSearch.module.css'

function HeaderSearch() {
  const navigate = useNavigate()
  const [isOpen, setIsOpen] = useState(false)

  function handleSearch(query: string) {
    setIsOpen(false)
    navigate(`/search?q=${encodeURIComponent(query)}`)
  }

  return (
    <>
      <Button
        className={styles.trigger}
        leadingVisual={SearchIcon}
        alignContent="start"
        onClick={() => setIsOpen(true)}
      >
        Medien suchen...
      </Button>

      {isOpen && (
        <Dialog
          align="top"
          width="xlarge"
          onClose={() => setIsOpen(false)}
          renderHeader={({ dialogLabelId }) => (
            <VisuallyHidden id={dialogLabelId}>Medien suchen</VisuallyHidden>
          )}
        >
          <SearchForm onSearch={handleSearch} />
        </Dialog>
      )}
    </>
  )
}

export default HeaderSearch
