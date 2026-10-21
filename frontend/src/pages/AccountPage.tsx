import { Banner, Button, FormControl, Heading, Label, Stack, TextInput, useConfirm } from '@primer/react'
import { useState, type FormEvent } from 'react'
import { useNavigate } from 'react-router'
import { CODEWORD_PATH, deleteCodeword, updateCodeword, type CodewordResponse } from '../api/admin'
import { ApiError, describeError } from '../api/client'
import { changePassword } from '../api/users'
import { useAuth } from '../auth/useAuth'
import DeleteAccountDialog from '../components/DeleteAccountDialog'
import ErrorBanner from '../components/ErrorBanner'
import Loading from '../components/Loading'
import PageTitle from '../components/PageTitle'
import { useApi } from '../hooks/useApi'
import styles from './AccountPage.module.css'

function CodewordSettings() {
  const { data, error: loadError, isLoading, reload } = useApi<CodewordResponse>(CODEWORD_PATH)
  const [codeword, setCodeword] = useState('')
  const [isSaving, setIsSaving] = useState(false)
  const [result, setResult] = useState<{ ok: true; message: string } | { ok: false; error: unknown } | null>(null)
  const confirm = useConfirm()

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setIsSaving(true)
    setResult(null)
    try {
      await updateCodeword(codeword.trim())
      setCodeword('')
      setResult({ ok: true, message: 'Das neue Codewort gilt ab sofort.' })
      reload()
    } catch (error) {
      setResult({ ok: false, error })
    } finally {
      setIsSaving(false)
    }
  }

  async function handleRemove() {
    const confirmed = await confirm({
      title: 'Codewort entfernen?',
      content: 'Danach kann sich jeder registrieren, auch über 10 Nutzer hinaus.',
      confirmButtonContent: 'Entfernen',
      confirmButtonType: 'danger',
      cancelButtonContent: 'Abbrechen',
    })
    if (!confirmed) return
    setResult(null)
    try {
      await deleteCodeword()
      setResult({ ok: true, message: 'Das Codewort wurde entfernt, die Registrierung ist jetzt offen.' })
      reload()
    } catch (error) {
      setResult({ ok: false, error })
    }
  }

  if (isLoading) return <Loading />
  if (loadError !== null) return <ErrorBanner error={loadError} onRetry={reload} />

  const current = data?.codeword ?? null

  return (
    <Stack gap="normal">
      {result?.ok === true && <Banner variant="success" layout="compact" title="Gespeichert" description={result.message} />}
      {result?.ok === false && (
        <Banner variant="critical" layout="compact" title="Fehler" hideTitle description={describeError(result.error)} />
      )}
      <p className={styles.hint}>
        Die ersten 10 Nutzer können sich frei registrieren. Danach ist das Codewort Pflicht.
      </p>
      <dl className={styles.details}>
        <dt>Aktuelles Codewort</dt>
        <dd>{current !== null ? <code>{current}</code> : <span className={styles.missing}>Kein Codewort festgelegt</span>}</dd>
      </dl>
      <form onSubmit={handleSubmit}>
        <Stack gap="normal">
          <FormControl required>
            <FormControl.Label>{current !== null ? 'Neues Codewort' : 'Codewort festlegen'}</FormControl.Label>
            <TextInput value={codeword} onChange={(event) => setCodeword(event.target.value)} />
          </FormControl>
          <Stack direction="horizontal" gap="condensed" wrap="wrap">
            <Button type="submit" variant="primary" loading={isSaving} disabled={codeword.trim() === ''}>
              Codewort speichern
            </Button>
            {current !== null && (
              <Button variant="danger" onClick={handleRemove}>
                Codewort entfernen
              </Button>
            )}
          </Stack>
        </Stack>
      </form>
    </Stack>
  )
}

function PasswordForm() {
  const [currentPassword, setCurrentPassword] = useState('')
  const [newPassword, setNewPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  const [isSaving, setIsSaving] = useState(false)
  const [result, setResult] = useState<{ ok: true } | { ok: false; message: string } | null>(null)

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    if (newPassword !== confirmPassword) {
      setResult({ ok: false, message: 'Die beiden neuen Passwörter stimmen nicht überein.' })
      return
    }
    setIsSaving(true)
    setResult(null)
    try {
      await changePassword(currentPassword, newPassword)
      setCurrentPassword('')
      setNewPassword('')
      setConfirmPassword('')
      setResult({ ok: true })
    } catch (error) {
      const message =
        error instanceof ApiError && error.status === 400 ? 'Das aktuelle Passwort ist falsch.' : describeError(error)
      setResult({ ok: false, message })
    } finally {
      setIsSaving(false)
    }
  }

  return (
    <form onSubmit={handleSubmit}>
      <Stack gap="normal">
        {result?.ok === true && (
          <Banner variant="success" layout="compact" title="Gespeichert" description="Dein Passwort wurde geändert." />
        )}
        {result?.ok === false && (
          <Banner variant="critical" layout="compact" title="Fehler" hideTitle description={result.message} />
        )}
        <FormControl required>
          <FormControl.Label>Aktuelles Passwort</FormControl.Label>
          <TextInput
            type="password"
            autoComplete="current-password"
            value={currentPassword}
            onChange={(event) => setCurrentPassword(event.target.value)}
          />
        </FormControl>
        <FormControl required>
          <FormControl.Label>Neues Passwort</FormControl.Label>
          <TextInput
            type="password"
            autoComplete="new-password"
            value={newPassword}
            onChange={(event) => setNewPassword(event.target.value)}
          />
        </FormControl>
        <FormControl required>
          <FormControl.Label>Neues Passwort wiederholen</FormControl.Label>
          <TextInput
            type="password"
            autoComplete="new-password"
            value={confirmPassword}
            onChange={(event) => setConfirmPassword(event.target.value)}
          />
        </FormControl>
        <div>
          <Button
            type="submit"
            variant="primary"
            loading={isSaving}
            disabled={currentPassword === '' || newPassword === '' || confirmPassword === ''}
          >
            Passwort ändern
          </Button>
        </div>
      </Stack>
    </form>
  )
}

function AccountPage() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()
  const [isDeleteOpen, setIsDeleteOpen] = useState(false)

  async function handleLogout() {
    await logout()
    navigate('/login')
  }

  async function handleDeleted() {
    // Die Session hat das Backend schon beendet – logout() räumt nur noch den lokalen Zustand auf.
    await logout()
    navigate('/login', { replace: true })
  }

  return (
    <Stack gap="spacious">
      <PageTitle title="Account" />

      <section className={styles.box} aria-labelledby="account-profile">
        <Heading as="h2" variant="small" id="account-profile" className={styles.boxTitle}>
          Profil
        </Heading>
        <dl className={styles.details}>
          <dt>Benutzername</dt>
          <dd>{user?.username}</dd>
          <dt>E-Mail</dt>
          <dd>{user?.email}</dd>
          {user?.isAdmin && (
            <>
              <dt>Rolle</dt>
              <dd>
                <Label variant="accent">Admin</Label>
              </dd>
            </>
          )}
        </dl>
        <Button onClick={handleLogout}>Abmelden</Button>
      </section>

      <section className={styles.box} aria-labelledby="account-password">
        <Heading as="h2" variant="small" id="account-password" className={styles.boxTitle}>
          Passwort ändern
        </Heading>
        <PasswordForm />
      </section>

      {user?.isAdmin && (
        <section className={styles.box} aria-labelledby="account-admin">
          <Heading as="h2" variant="small" id="account-admin" className={styles.boxTitle}>
            Admin: Signup-Codewort
          </Heading>
          <CodewordSettings />
        </section>
      )}

      <section className={`${styles.box} ${styles.dangerBox}`} aria-labelledby="account-delete">
        <Heading as="h2" variant="small" id="account-delete" className={styles.boxTitle}>
          Account löschen
        </Heading>
        <Stack gap="normal">
          <p className={styles.dangerText}>
            Löscht deinen Account und deine komplette Liste dauerhaft. Das lässt sich nicht rückgängig machen.
          </p>
          <div>
            <Button variant="danger" onClick={() => setIsDeleteOpen(true)}>
              Account löschen
            </Button>
          </div>
        </Stack>
      </section>

      {isDeleteOpen && <DeleteAccountDialog onClose={() => setIsDeleteOpen(false)} onDeleted={handleDeleted} />}
    </Stack>
  )
}

export default AccountPage
