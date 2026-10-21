import { Banner, Button, FormControl, Heading, Link, Stack, TextInput } from '@primer/react'
import { useState, type FormEvent } from 'react'
import { Navigate, Link as RouterLink, useNavigate } from 'react-router'
import { register } from '../api/auth'
import { ApiError } from '../api/client'
import { useAuth } from '../auth/useAuth'
import styles from './AuthPage.module.css'

function errorMessageFor(err: unknown): string {
  if (err instanceof ApiError) {
    if (err.status === 409) return 'Benutzername oder E-Mail ist bereits vergeben.'
    if (err.status === 400) return 'Das Codewort ist falsch oder fehlt.'
  }
  return 'Registrierung gerade nicht möglich. Bitte später erneut versuchen.'
}

function RegisterPage() {
  const [username, setUsername] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [codeword, setCodeword] = useState('')
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const navigate = useNavigate()
  const { user, login } = useAuth()

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setIsSubmitting(true)
    setError(null)

    try {
      await register({ username, email, password, codeword: codeword.trim() || null })
      await login(username, password)
      navigate('/', { replace: true })
    } catch (err) {
      setError(errorMessageFor(err))
    } finally {
      setIsSubmitting(false)
    }
  }

  if (user) {
    return <Navigate to="/" replace />
  }

  return (
    <div className={styles.page}>
      <Heading as="h1" className={styles.title}>
        Konto erstellen
      </Heading>

      <form className={styles.box} onSubmit={handleSubmit}>
        <Stack gap="normal">
          {error && (
            <Banner variant="critical" layout="compact" title="Registrierung fehlgeschlagen" description={error} />
          )}
          <FormControl required>
            <FormControl.Label>Benutzername</FormControl.Label>
            <TextInput
              block
              autoComplete="username"
              minLength={4}
              maxLength={15}
              value={username}
              onChange={(event) => setUsername(event.target.value)}
            />
            <FormControl.Caption>4 bis 15 Zeichen</FormControl.Caption>
          </FormControl>
          <FormControl required>
            <FormControl.Label>E-Mail</FormControl.Label>
            <TextInput
              block
              type="email"
              autoComplete="email"
              value={email}
              onChange={(event) => setEmail(event.target.value)}
            />
          </FormControl>
          <FormControl required>
            <FormControl.Label>Passwort</FormControl.Label>
            <TextInput
              block
              type="password"
              autoComplete="new-password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
            />
          </FormControl>
          <FormControl>
            <FormControl.Label>Codewort</FormControl.Label>
            <TextInput
              block
              value={codeword}
              onChange={(event) => setCodeword(event.target.value)}
            />
            <FormControl.Caption>Nur nötig, wenn du ein Codewort vom Admin bekommen hast.</FormControl.Caption>
          </FormControl>
          <Button type="submit" variant="primary" block loading={isSubmitting}>
            Konto erstellen
          </Button>
        </Stack>
      </form>

      <p className={styles.footer}>
        Schon ein Konto?{' '}
        <Link as={RouterLink} to="/login">
          Anmelden
        </Link>
      </p>
    </div>
  )
}

export default RegisterPage
