import { Banner, Button, FormControl, Heading, Link, Stack, TextInput } from '@primer/react'
import { useState, type FormEvent } from 'react'
import { Navigate, Link as RouterLink, useNavigate } from 'react-router'
import { register } from '../api/auth'
import { ApiError } from '../api/client'
import { useAuth } from '../auth/useAuth'
import type { Translations } from '../i18n/translations'
import { useI18n } from '../i18n/useI18n'
import styles from './AuthPage.module.css'

function errorMessageFor(err: unknown, t: Translations): string {
  if (err instanceof ApiError) {
    if (err.status === 409) return t.register.taken
    if (err.status === 400) return t.register.wrongCodeword
  }
  return t.register.unavailable
}

function RegisterPage() {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [codeword, setCodeword] = useState('')
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const navigate = useNavigate()
  const { user, login } = useAuth()
  const { t } = useI18n()

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setIsSubmitting(true)
    setError(null)

    try {
      await register({ username, password, codeword: codeword.trim() || null })
      await login(username, password)
      navigate('/', { replace: true })
    } catch (err) {
      setError(errorMessageFor(err, t))
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
        {t.register.heading}
      </Heading>

      <form className={styles.box} onSubmit={handleSubmit}>
        <Stack gap="normal">
          {error && (
            <Banner variant="critical" layout="compact" title={t.register.failed} description={error} />
          )}
          <FormControl required>
            <FormControl.Label>{t.register.username}</FormControl.Label>
            <TextInput
              block
              autoComplete="username"
              minLength={4}
              maxLength={15}
              value={username}
              onChange={(event) => setUsername(event.target.value)}
            />
            <FormControl.Caption>{t.register.usernameCaption}</FormControl.Caption>
          </FormControl>
          <FormControl required>
            <FormControl.Label>{t.register.password}</FormControl.Label>
            <TextInput
              block
              type="password"
              autoComplete="new-password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
            />
          </FormControl>
          <FormControl>
            <FormControl.Label>{t.register.codeword}</FormControl.Label>
            <TextInput
              block
              value={codeword}
              onChange={(event) => setCodeword(event.target.value)}
            />
            <FormControl.Caption>{t.register.codewordCaption}</FormControl.Caption>
          </FormControl>
          <Button type="submit" variant="primary" block loading={isSubmitting}>
            {t.register.submit}
          </Button>
        </Stack>
      </form>

      <p className={styles.footer}>
        {t.register.haveAccount}{' '}
        <Link as={RouterLink} to="/login">
          {t.register.signIn}
        </Link>
      </p>
    </div>
  )
}

export default RegisterPage
