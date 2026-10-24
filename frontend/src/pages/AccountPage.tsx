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
import { useI18n } from '../i18n/useI18n'
import styles from './AccountPage.module.css'

function CodewordSettings() {
  const { data, error: loadError, isLoading, reload } = useApi<CodewordResponse>(CODEWORD_PATH)
  const [codeword, setCodeword] = useState('')
  const [isSaving, setIsSaving] = useState(false)
  const [result, setResult] = useState<{ ok: true; message: string } | { ok: false; error: unknown } | null>(null)
  const confirm = useConfirm()
  const { t } = useI18n()

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setIsSaving(true)
    setResult(null)
    try {
      await updateCodeword(codeword.trim())
      setCodeword('')
      setResult({ ok: true, message: t.account.codewordSaved })
      reload()
    } catch (error) {
      setResult({ ok: false, error })
    } finally {
      setIsSaving(false)
    }
  }

  async function handleRemove() {
    const confirmed = await confirm({
      title: t.account.codewordRemoveTitle,
      content: t.account.codewordRemoveContent,
      confirmButtonContent: t.common.remove,
      confirmButtonType: 'danger',
      cancelButtonContent: t.common.cancel,
    })
    if (!confirmed) return
    setResult(null)
    try {
      await deleteCodeword()
      setResult({ ok: true, message: t.account.codewordRemoved })
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
      {result?.ok === true && <Banner variant="success" layout="compact" title={t.common.saved} description={result.message} />}
      {result?.ok === false && (
        <Banner variant="critical" layout="compact" title={t.common.error} hideTitle description={describeError(result.error, t)} />
      )}
      <p className={styles.hint}>{t.account.codewordHint}</p>
      <dl className={styles.details}>
        <dt>{t.account.currentCodeword}</dt>
        <dd>{current !== null ? <code>{current}</code> : <span className={styles.missing}>{t.account.noCodeword}</span>}</dd>
      </dl>
      <form onSubmit={handleSubmit}>
        <Stack gap="normal">
          <FormControl required>
            <FormControl.Label>{current !== null ? t.account.newCodeword : t.account.setCodeword}</FormControl.Label>
            <TextInput value={codeword} onChange={(event) => setCodeword(event.target.value)} />
          </FormControl>
          <Stack direction="horizontal" gap="condensed" wrap="wrap">
            <Button type="submit" variant="primary" loading={isSaving} disabled={codeword.trim() === ''}>
              {t.account.saveCodeword}
            </Button>
            {current !== null && (
              <Button variant="danger" onClick={handleRemove}>
                {t.account.removeCodeword}
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
  const { t } = useI18n()

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    if (newPassword !== confirmPassword) {
      setResult({ ok: false, message: t.account.passwordsMismatch })
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
        error instanceof ApiError && error.status === 400 ? t.account.wrongCurrentPassword : describeError(error, t)
      setResult({ ok: false, message })
    } finally {
      setIsSaving(false)
    }
  }

  return (
    <form onSubmit={handleSubmit}>
      <Stack gap="normal">
        {result?.ok === true && (
          <Banner variant="success" layout="compact" title={t.common.saved} description={t.account.passwordChanged} />
        )}
        {result?.ok === false && (
          <Banner variant="critical" layout="compact" title={t.common.error} hideTitle description={result.message} />
        )}
        <FormControl required>
          <FormControl.Label>{t.account.currentPassword}</FormControl.Label>
          <TextInput
            type="password"
            autoComplete="current-password"
            value={currentPassword}
            onChange={(event) => setCurrentPassword(event.target.value)}
          />
        </FormControl>
        <FormControl required>
          <FormControl.Label>{t.account.newPassword}</FormControl.Label>
          <TextInput
            type="password"
            autoComplete="new-password"
            value={newPassword}
            onChange={(event) => setNewPassword(event.target.value)}
          />
        </FormControl>
        <FormControl required>
          <FormControl.Label>{t.account.repeatPassword}</FormControl.Label>
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
            {t.account.changePassword}
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
  const { t } = useI18n()

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
      <PageTitle title={t.account.title} />

      <section className={styles.box} aria-labelledby="account-profile">
        <Heading as="h2" variant="small" id="account-profile" className={styles.boxTitle}>
          {t.account.profile}
        </Heading>
        <dl className={styles.details}>
          <dt>{t.account.username}</dt>
          <dd>{user?.username}</dd>
          {user?.isAdmin && (
            <>
              <dt>{t.account.role}</dt>
              <dd>
                <Label variant="accent">{t.account.admin}</Label>
              </dd>
            </>
          )}
        </dl>
        <Button onClick={handleLogout}>{t.account.logout}</Button>
      </section>

      <section className={styles.box} aria-labelledby="account-password">
        <Heading as="h2" variant="small" id="account-password" className={styles.boxTitle}>
          {t.account.passwordSection}
        </Heading>
        <PasswordForm />
      </section>

      {user?.isAdmin && (
        <section className={styles.box} aria-labelledby="account-admin">
          <Heading as="h2" variant="small" id="account-admin" className={styles.boxTitle}>
            {t.account.codewordSection}
          </Heading>
          <CodewordSettings />
        </section>
      )}

      <section className={`${styles.box} ${styles.dangerBox}`} aria-labelledby="account-delete">
        <Heading as="h2" variant="small" id="account-delete" className={styles.boxTitle}>
          {t.account.deleteSection}
        </Heading>
        <Stack gap="normal">
          <p className={styles.dangerText}>{t.account.deleteText}</p>
          <div>
            <Button variant="danger" onClick={() => setIsDeleteOpen(true)}>
              {t.account.deleteButton}
            </Button>
          </div>
        </Stack>
      </section>

      {isDeleteOpen && <DeleteAccountDialog onClose={() => setIsDeleteOpen(false)} onDeleted={handleDeleted} />}
    </Stack>
  )
}

export default AccountPage
