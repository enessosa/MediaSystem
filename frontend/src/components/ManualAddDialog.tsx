import { Banner, Dialog, FormControl, Select, Stack, TextInput, Textarea } from '@primer/react'
import { useState } from 'react'
import { describeError } from '../api/client'
import { addToList, MEDIA_TYPES, type MediaType, type UserEntryResponse } from '../api/media'
import { useI18n } from '../i18n/useI18n'

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
  const { t } = useI18n()

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
      title={t.manualAdd.title}
      subtitle={t.manualAdd.subtitle}
      width="large"
      onClose={onClose}
      footerButtons={[
        { content: t.common.cancel, onClick: onClose },
        { content: t.manualAdd.create, buttonType: 'primary', onClick: save, loading: isSaving },
      ]}
    >
      <Stack gap="normal">
        {error !== null && (
          <Banner variant="critical" layout="compact" title={t.common.error} hideTitle description={describeError(error, t)} />
        )}
        <FormControl required>
          <FormControl.Label>{t.manualAdd.titleLabel}</FormControl.Label>
          <TextInput block value={title} onChange={(event) => setTitle(event.target.value)} />
          {showTitleError && title.trim() === '' && (
            <FormControl.Validation variant="error">{t.manualAdd.titleRequired}</FormControl.Validation>
          )}
        </FormControl>
        <FormControl required>
          <FormControl.Label>{t.manualAdd.mediaType}</FormControl.Label>
          <Select block value={mediaType} onChange={(event) => setMediaType(event.target.value as MediaType)}>
            {MEDIA_TYPES.map((type) => (
              <Select.Option key={type} value={type}>
                {t.mediaTypes[type]}
              </Select.Option>
            ))}
          </Select>
        </FormControl>
        <FormControl>
          <FormControl.Label>{t.manualAdd.releaseYear}</FormControl.Label>
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
          <FormControl.Label>{t.manualAdd.creator}</FormControl.Label>
          <TextInput block value={creator} onChange={(event) => setCreator(event.target.value)} />
          <FormControl.Caption>{t.manualAdd.creatorCaption}</FormControl.Caption>
        </FormControl>
        <FormControl>
          <FormControl.Label>{t.manualAdd.coverUrl}</FormControl.Label>
          <TextInput block type="url" value={coverUrl} onChange={(event) => setCoverUrl(event.target.value)} />
        </FormControl>
        <FormControl>
          <FormControl.Label>{t.manualAdd.description}</FormControl.Label>
          <Textarea block rows={3} value={description} onChange={(event) => setDescription(event.target.value)} />
        </FormControl>
      </Stack>
    </Dialog>
  )
}

export default ManualAddDialog
