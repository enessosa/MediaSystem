import { Banner, Dialog, FormControl, Select, Stack, Textarea } from '@primer/react'
import { useState } from 'react'
import { describeError } from '../api/client'
import { STATUSES, updateEntry, type Status, type UserEntryResponse } from '../api/media'
import { useI18n } from '../i18n/useI18n'

interface EntryEditDialogProps {
  entry: UserEntryResponse
  onClose: () => void
  onSaved: (entry: UserEntryResponse) => void
}

const RATINGS = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]

function EntryEditDialog({ entry, onClose, onSaved }: EntryEditDialogProps) {
  const [status, setStatus] = useState<Status>(entry.status)
  const [rating, setRating] = useState<number | null>(entry.rating)
  const [note, setNote] = useState(entry.note ?? '')
  const [isSaving, setIsSaving] = useState(false)
  const [error, setError] = useState<unknown>(null)
  const { t } = useI18n()

  async function save() {
    setIsSaving(true)
    setError(null)
    try {
      const updated = await updateEntry(entry.id, { status, rating, note: note.trim() || null })
      onSaved(updated)
    } catch (err) {
      setError(err)
      setIsSaving(false)
    }
  }

  return (
    <Dialog
      title={t.entryEdit.title}
      subtitle={entry.title}
      onClose={onClose}
      footerButtons={[
        { content: t.common.cancel, onClick: onClose },
        { content: t.common.save, buttonType: 'primary', onClick: save, loading: isSaving },
      ]}
    >
      <Stack gap="normal">
        {error !== null && (
          <Banner variant="critical" layout="compact" title={t.common.error} hideTitle description={describeError(error, t)} />
        )}
        <FormControl>
          <FormControl.Label>{t.entryEdit.status}</FormControl.Label>
          <Select block value={status} onChange={(event) => setStatus(event.target.value as Status)}>
            {STATUSES.map((s) => (
              <Select.Option key={s} value={s}>
                {t.statuses[s]}
              </Select.Option>
            ))}
          </Select>
        </FormControl>
        <FormControl>
          <FormControl.Label>{t.entryEdit.rating}</FormControl.Label>
          <Select
            block
            value={rating === null ? '' : String(rating)}
            onChange={(event) => setRating(event.target.value === '' ? null : Number(event.target.value))}
          >
            <Select.Option value="">{t.entryEdit.noRating}</Select.Option>
            {RATINGS.map((r) => (
              <Select.Option key={r} value={String(r)}>
                {r} / 10
              </Select.Option>
            ))}
          </Select>
        </FormControl>
        <FormControl>
          <FormControl.Label>{t.entryEdit.note}</FormControl.Label>
          <Textarea block rows={4} value={note} onChange={(event) => setNote(event.target.value)} />
        </FormControl>
      </Stack>
    </Dialog>
  )
}

export default EntryEditDialog
