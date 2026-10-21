import { Banner, Dialog, FormControl, Stack, TextInput } from '@primer/react'
import { useState } from 'react'
import { ApiError, describeError } from '../api/client'
import { deleteAccount } from '../api/users'

interface DeleteAccountDialogProps {
  onClose: () => void
  onDeleted: () => void
}

function DeleteAccountDialog({ onClose, onDeleted }: DeleteAccountDialogProps) {
  const [password, setPassword] = useState('')
  const [isDeleting, setIsDeleting] = useState(false)
  const [error, setError] = useState<string | null>(null)

  async function handleDelete() {
    if (password === '') {
      setError('Bitte gib dein Passwort ein.')
      return
    }
    setIsDeleting(true)
    setError(null)
    try {
      await deleteAccount(password)
      onDeleted()
    } catch (err) {
      if (err instanceof ApiError && err.status === 400) setError('Das Passwort ist falsch.')
      // 409: einziger Admin – die Begründung kommt vom Backend.
      else if (err instanceof ApiError && err.status === 409) setError(err.message)
      else setError(describeError(err))
      setIsDeleting(false)
    }
  }

  return (
    <Dialog
      title="Account löschen?"
      onClose={onClose}
      footerButtons={[
        { content: 'Abbrechen', onClick: onClose },
        { content: 'Account endgültig löschen', buttonType: 'danger', onClick: handleDelete, loading: isDeleting },
      ]}
    >
      <Stack gap="normal">
        <p>
          Dein Account und deine komplette Liste (Status, Bewertungen, Notizen) werden dauerhaft gelöscht. Das lässt sich
          nicht rückgängig machen.
        </p>
        {error !== null && <Banner variant="critical" layout="compact" title="Fehler" hideTitle description={error} />}
        <FormControl required>
          <FormControl.Label>Zur Bestätigung dein Passwort</FormControl.Label>
          <TextInput
            type="password"
            autoComplete="current-password"
            block
            value={password}
            onChange={(event) => setPassword(event.target.value)}
          />
        </FormControl>
      </Stack>
    </Dialog>
  )
}

export default DeleteAccountDialog
