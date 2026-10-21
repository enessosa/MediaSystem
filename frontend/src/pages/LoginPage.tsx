import { Banner, Button, FormControl, Heading, Link, Stack, TextInput } from '@primer/react'
import { useState, type FormEvent } from 'react'
import { Navigate, Link as RouterLink, useLocation, useNavigate } from 'react-router'
import { ApiError } from '../api/client'
import { useAuth } from '../auth/useAuth'
import styles from './AuthPage.module.css'

function LoginPage() {
  const [identifier, setIdentifier] = useState('')
  const [password, setPassword] = useState('')
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const navigate = useNavigate()
  const location = useLocation()
  const { user, login } = useAuth()
  const redirectTo = (location.state as { from?: string } | null)?.from ?? '/'

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setIsSubmitting(true)
    setError(null)

    try {
      await login(identifier, password)
      navigate(redirectTo, { replace: true })
    } catch (err) {
      if (err instanceof ApiError && err.status === 401) {
        setError('Benutzername oder Passwort ist falsch.')
      } else {
        setError('Anmeldung gerade nicht möglich. Bitte später erneut versuchen.')
      }
    } finally {
      setIsSubmitting(false)
    }
  }

  if (user) {
    return <Navigate to={redirectTo} replace />
  }

  return (
    <div className={styles.page}>
      <Heading as="h1" className={styles.title}>
        Bei MediaSystem anmelden
      </Heading>

      <form className={styles.box} onSubmit={handleSubmit}>
        <Stack gap="normal">
          {error && (
            <Banner variant="critical" layout="compact" title="Anmeldung fehlgeschlagen" description={error} />
          )}
          <FormControl required>
            <FormControl.Label>Benutzername oder E-Mail</FormControl.Label>
            <TextInput
              block
              autoComplete="username"
              value={identifier}
              onChange={(event) => setIdentifier(event.target.value)}
            />
          </FormControl>
          <FormControl required>
            <FormControl.Label>Passwort</FormControl.Label>
            <TextInput
              block
              type="password"
              autoComplete="current-password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
            />
          </FormControl>
          <Button type="submit" variant="primary" block loading={isSubmitting}>
            Anmelden
          </Button>
        </Stack>
      </form>

      <p className={styles.footer}>
        Neu bei MediaSystem?{' '}
        <Link as={RouterLink} to="/register">
          Konto erstellen
        </Link>
      </p>
    </div>
  )
}

export default LoginPage
