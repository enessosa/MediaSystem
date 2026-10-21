import { Banner, Dialog, FormControl, Select, Stack, TextInput, Textarea } from '@primer/react'
import { useState } from 'react'
import { describeError } from '../api/client'
import { addToList, MEDIA_TYPE_LABELS, MEDIA_TYPES, type MediaType, type UserEntryResponse } from '../api/media'

interface ManualAddDialogProps {
  initialTitle?: string
  onClose: () => void
  onCreated: (entry: UserEntryResponse) => void
}

function ManualAddDialog({ initialTitle = '', onClose, onCreated }: ManualAddDialogProps) {
  const [title, setTitle] = useState(initialTitle)
  const [mediaType, setMediaType] = useState<MediaType>('ANIME')
  const [releaseYear, setReleaseYear] = useState('')
  const [creator, setCreator] = useState('')
  const [coverUrl, setCoverUrl] = useState('')
  const [description, setDescription] = useState('')
  const [isSaving, setIsSaving] = useState(false)
  const [error, setError] = useState<unknown>(null)
  const [showTitleError, setShowTitleError] = useState(false)

  async function save() {
    if (title.trim() === '') {
      setShowTitleError(true)
      return
    }
    setIsSaving(true)
    setError(null)
    try {
      const entry = await addToList({
        title: title.trim(),
        mediaType,
        releaseYear: releaseYear === '' ? null : Number(releaseYear),
        creator: creator.trim() || null,
        coverUrl: coverUrl.trim() || null,
        description: description.trim() || null,
        sourceType: 'MANUAL',
        externalId: null,
      })
      onCreated(entry)
    } catch (err) {
      setError(err)
      setIsSaving(false)
    }
  }

  return (
    <Dialog
      title="Medium selbst anlegen"
      subtitle="Für Medien, die die Suche nicht findet."
      width="large"
      onClose={onClose}
      footerButtons={[
        { content: 'Abbrechen', onClick: onClose },
        { content: 'Anlegen', buttonType: 'primary', onClick: save, loading: isSaving },
      ]}
    >
      <Stack gap="normal">
        {error !== null && (
          <Banner variant="critical" layout="compact" title="Fehler" hideTitle description={describeError(error)} />
        )}
        <FormControl required>
          <FormControl.Label>Titel</FormControl.Label>
          <TextInput block value={title} onChange={(event) => setTitle(event.target.value)} />
          {showTitleError && title.trim() === '' && (
            <FormControl.Validation variant="error">Bitte einen Titel angeben.</FormControl.Validation>
          )}
        </FormControl>
        <FormControl required>
          <FormControl.Label>Medientyp</FormControl.Label>
          <Select block value={mediaType} onChange={(event) => setMediaType(event.target.value as MediaType)}>
            {MEDIA_TYPES.map((type) => (
              <Select.Option key={type} value={type}>
                {MEDIA_TYPE_LABELS[type]}
              </Select.Option>
            ))}
          </Select>
        </FormControl>
        <FormControl>
          <FormControl.Label>Erscheinungsjahr</FormControl.Label>
          <TextInput
            block
            type="number"
            min={1800}
            max={2100}
            value={releaseYear}
            onChange={(event) => setReleaseYear(event.target.value)}
          />
        </FormControl>
        <FormControl>
          <FormControl.Label>Urheber</FormControl.Label>
          <TextInput block value={creator} onChange={(event) => setCreator(event.target.value)} />
          <FormControl.Caption>z.B. Studio, Autor, Regie oder Entwickler</FormControl.Caption>
        </FormControl>
        <FormControl>
          <FormControl.Label>Cover-URL</FormControl.Label>
          <TextInput block type="url" value={coverUrl} onChange={(event) => setCoverUrl(event.target.value)} />
        </FormControl>
        <FormControl>
          <FormControl.Label>Beschreibung</FormControl.Label>
          <Textarea block rows={3} value={description} onChange={(event) => setDescription(event.target.value)} />
        </FormControl>
      </Stack>
    </Dialog>
  )
}

export default ManualAddDialog
