import { Banner, Dialog, FormControl, Stack, TextInput } from '@primer/react'
import { useState } from 'react'
import { ApiError, describeError } from '../api/client'
import { deleteAccount } from '../api/users'
import { useI18n } from '../i18n/useI18n'

interface DeleteAccountDialogProps {
  onClose: () => void
  onDeleted: () => void
}

function DeleteAccountDialog({ onClose, onDeleted }: DeleteAccountDialogProps) {
  const [password, setPassword] = useState('')
  const [isDeleting, setIsDeleting] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const { t } = useI18n()

  async function handleDelete() {
    if (password === '') {
      setError(t.deleteAccount.enterPassword)
      return
    }
    setIsDeleting(true)
    setError(null)
    try {
      await deleteAccount(password)
      onDeleted()
    } catch (err) {
      if (err instanceof ApiError && err.status === 400) setError(t.deleteAccount.wrongPassword)
      // 409: einziger Admin – die Begründung kommt vom Backend.
      else if (err instanceof ApiError && err.status === 409) setError(err.message)
      else setError(describeError(err, t))
      setIsDeleting(false)
    }
  }

  return (
    <Dialog
      title={t.deleteAccount.title}
      onClose={onClose}
      footerButtons={[
        { content: t.common.cancel, onClick: onClose },
        { content: t.deleteAccount.confirm, buttonType: 'danger', onClick: handleDelete, loading: isDeleting },
      ]}
    >
      <Stack gap="normal">
        <p>{t.deleteAccount.text}</p>
        {error !== null && <Banner variant="critical" layout="compact" title={t.common.error} hideTitle description={error} />}
        <FormControl required>
          <FormControl.Label>{t.deleteAccount.passwordLabel}</FormControl.Label>
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
