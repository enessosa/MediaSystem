import { Banner, Button, FormControl, Heading, Label, Stack, TextInput } from '@primer/react'
import { useState, type FormEvent } from 'react'
import { useNavigate } from 'react-router'
import { updateCodeword } from '../api/admin'
import { describeError } from '../api/client'
import { useAuth } from '../auth/useAuth'
import PageTitle from '../components/PageTitle'
import styles from './AccountPage.module.css'

function CodewordForm() {
  const [codeword, setCodeword] = useState('')
  const [isSaving, setIsSaving] = useState(false)
  const [result, setResult] = useState<{ ok: true } | { ok: false; error: unknown } | null>(null)

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setIsSaving(true)
    setResult(null)
    try {
      await updateCodeword(codeword.trim())
      setCodeword('')
      setResult({ ok: true })
    } catch (error) {
      setResult({ ok: false, error })
    } finally {
      setIsSaving(false)
    }
  }

  return (
    <form onSubmit={handleSubmit}>
      <Stack gap="normal">
        {result?.ok === true && (
          <Banner variant="success" layout="compact" title="Gespeichert" description="Das neue Codewort gilt ab sofort." />
        )}
        {result?.ok === false && (
          <Banner variant="critical" layout="compact" title="Fehler" hideTitle description={describeError(result.error)} />
        )}
        <FormControl required>
          <FormControl.Label>Neues Codewort</FormControl.Label>
          <TextInput value={codeword} onChange={(event) => setCodeword(event.target.value)} />
          <FormControl.Caption>
            Ab dem 11. Nutzer muss bei der Registrierung dieses Codewort angegeben werden.
          </FormControl.Caption>
        </FormControl>
        <div>
          <Button type="submit" variant="primary" loading={isSaving} disabled={codeword.trim() === ''}>
            Codewort speichern
          </Button>
        </div>
      </Stack>
    </form>
  )
}

function AccountPage() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  async function handleLogout() {
    await logout()
    navigate('/login')
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

      {user?.isAdmin && (
        <section className={styles.box} aria-labelledby="account-admin">
          <Heading as="h2" variant="small" id="account-admin" className={styles.boxTitle}>
            Admin: Signup-Codewort
          </Heading>
          <CodewordForm />
        </section>
      )}
    </Stack>
  )
}

export default AccountPage
