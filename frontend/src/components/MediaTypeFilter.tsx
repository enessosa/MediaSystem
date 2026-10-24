import { Checkbox, FormControl, Stack } from '@primer/react'
import { MEDIA_TYPES, type MediaType } from '../api/media'
import { useI18n } from '../i18n/useI18n'
import styles from './MediaTypeFilter.module.css'

interface MediaTypeFilterProps {
  selected: MediaType[]
  onChange: (selected: MediaType[]) => void
  /** Typen, die gerade nicht durchsuchbar sind (kein API-Key) – werden ausgegraut. */
  unavailable?: MediaType[]
}

function MediaTypeFilter({ selected, onChange, unavailable = [] }: MediaTypeFilterProps) {
  const { t } = useI18n()

  function toggle(type: MediaType) {
    onChange(selected.includes(type) ? selected.filter((t) => t !== type) : [...selected, type])
  }

  return (
    <fieldset className={styles.fieldset}>
      <legend className={styles.legend}>{t.search.mediaType}</legend>
      <Stack direction="horizontal" wrap="wrap" gap="normal">
        {MEDIA_TYPES.map((type) => (
          <FormControl key={type} disabled={unavailable.includes(type)}>
            <Checkbox checked={selected.includes(type)} onChange={() => toggle(type)} />
            <FormControl.Label>
              {t.mediaTypes[type]}
              {unavailable.includes(type) && t.search.disabled}
            </FormControl.Label>
          </FormControl>
        ))}
      </Stack>
    </fieldset>
  )
}

export default MediaTypeFilter
