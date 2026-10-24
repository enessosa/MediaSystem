import { InfoIcon, PersonIcon, SignOutIcon } from '@primer/octicons-react'
import { ActionList, ActionMenu, IconButton } from '@primer/react'
import { useNavigate } from 'react-router'
import flagDe from '../assets/flag-de.svg'
import flagGb from '../assets/flag-gb.svg'
import { useAuth } from '../auth/useAuth'
import { LANGUAGES, translations, type Language } from '../i18n/translations'
import { useI18n } from '../i18n/useI18n'
import styles from './UserMenu.module.css'

const FLAGS: Record<Language, string> = { en: flagGb, de: flagDe }

function UserMenu() {
  const navigate = useNavigate()
  const { user, logout } = useAuth()
  const { t, language, setLanguage } = useI18n()

  async function handleLogout() {
    await logout()
    navigate('/login')
  }

  return (
    <ActionMenu>
      <ActionMenu.Anchor>
        <IconButton icon={PersonIcon} aria-label={t.layout.openUserMenu} />
      </ActionMenu.Anchor>
      <ActionMenu.Overlay align="end">
        <ActionList>
          <ActionList.Group>
            <ActionList.GroupHeading>{t.layout.signedInAs(user?.username ?? '')}</ActionList.GroupHeading>
          </ActionList.Group>
          <ActionList.Divider />
          <ActionList.Item onSelect={() => navigate('/account')}>
            <ActionList.LeadingVisual>
              <PersonIcon />
            </ActionList.LeadingVisual>
            {t.layout.account}
          </ActionList.Item>
          <ActionList.Item onSelect={() => navigate('/about')}>
            <ActionList.LeadingVisual>
              <InfoIcon />
            </ActionList.LeadingVisual>
            {t.layout.about}
          </ActionList.Item>
          <ActionList.Divider />
          <ActionList.Group selectionVariant="single">
            <ActionList.GroupHeading>{t.layout.language}</ActionList.GroupHeading>
            {LANGUAGES.map((lang) => (
              <ActionList.Item key={lang} selected={lang === language} onSelect={() => setLanguage(lang)}>
                <ActionList.LeadingVisual>
                  <img src={FLAGS[lang]} alt="" className={styles.flag} />
                </ActionList.LeadingVisual>
                {/* Sprachnamen immer in der eigenen Sprache, damit man zurückfindet. */}
                {translations[lang].languageName}
              </ActionList.Item>
            ))}
          </ActionList.Group>
          <ActionList.Divider />
          <ActionList.Item variant="danger" onSelect={handleLogout}>
            <ActionList.LeadingVisual>
              <SignOutIcon />
            </ActionList.LeadingVisual>
            {t.layout.logout}
          </ActionList.Item>
        </ActionList>
      </ActionMenu.Overlay>
    </ActionMenu>
  )
}

export default UserMenu
