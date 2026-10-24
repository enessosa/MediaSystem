import { SearchIcon } from '@primer/octicons-react'
import { Button, Dialog, VisuallyHidden } from '@primer/react'
import { useState } from 'react'
import { useNavigate } from 'react-router'
import SearchForm from '../components/SearchForm'
import { useI18n } from '../i18n/useI18n'
import styles from './HeaderSearch.module.css'

function HeaderSearch() {
  const navigate = useNavigate()
  const [isOpen, setIsOpen] = useState(false)
  const { t } = useI18n()

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
        {t.search.headerTrigger}
      </Button>

      {isOpen && (
        <Dialog
          align="top"
          width="xlarge"
          onClose={() => setIsOpen(false)}
          renderHeader={({ dialogLabelId }) => (
            <VisuallyHidden id={dialogLabelId}>{t.search.label}</VisuallyHidden>
          )}
        >
          <SearchForm onSearch={handleSearch} />
        </Dialog>
      )}
    </>
  )
}

export default HeaderSearch
