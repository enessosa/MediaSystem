import { Banner, Dialog, FormControl, Select, Stack, Textarea } from '@primer/react'
import { useState } from 'react'
import { describeError } from '../api/client'
import { STATUS_LABELS, STATUSES, updateEntry, type Status, type UserEntryResponse } from '../api/media'

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
      title="Eintrag bearbeiten"
      subtitle={entry.title}
      onClose={onClose}
      footerButtons={[
        { content: 'Abbrechen', onClick: onClose },
        { content: 'Speichern', buttonType: 'primary', onClick: save, loading: isSaving },
      ]}
    >
      <Stack gap="normal">
        {error !== null && (
          <Banner variant="critical" layout="compact" title="Fehler" hideTitle description={describeError(error)} />
        )}
        <FormControl>
          <FormControl.Label>Status</FormControl.Label>
          <Select block value={status} onChange={(event) => setStatus(event.target.value as Status)}>
            {STATUSES.map((s) => (
              <Select.Option key={s} value={s}>
                {STATUS_LABELS[s]}
              </Select.Option>
            ))}
          </Select>
        </FormControl>
        <FormControl>
          <FormControl.Label>Bewertung</FormControl.Label>
          <Select
            block
            value={rating === null ? '' : String(rating)}
            onChange={(event) => setRating(event.target.value === '' ? null : Number(event.target.value))}
          >
            <Select.Option value="">Keine Bewertung</Select.Option>
            {RATINGS.map((r) => (
              <Select.Option key={r} value={String(r)}>
                {r} / 10
              </Select.Option>
            ))}
          </Select>
        </FormControl>
        <FormControl>
          <FormControl.Label>Notiz</FormControl.Label>
          <Textarea block rows={4} value={note} onChange={(event) => setNote(event.target.value)} />
        </FormControl>
      </Stack>
    </Dialog>
  )
}

export default EntryEditDialog
