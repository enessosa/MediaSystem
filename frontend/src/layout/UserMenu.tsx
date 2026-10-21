import { PersonIcon, SignOutIcon } from '@primer/octicons-react'
import { ActionList, ActionMenu, IconButton } from '@primer/react'
import { useNavigate } from 'react-router'
import { useAuth } from '../auth/useAuth'

function UserMenu() {
  const navigate = useNavigate()
  const { user, logout } = useAuth()

  async function handleLogout() {
    await logout()
    navigate('/login')
  }

  return (
    <ActionMenu>
      <ActionMenu.Anchor>
        <IconButton icon={PersonIcon} aria-label="Benutzermenü öffnen" />
      </ActionMenu.Anchor>
      <ActionMenu.Overlay align="end">
        <ActionList>
          <ActionList.Group>
            <ActionList.GroupHeading>Angemeldet als {user?.username}</ActionList.GroupHeading>
          </ActionList.Group>
          <ActionList.Divider />
          <ActionList.Item onSelect={() => navigate('/account')}>
            <ActionList.LeadingVisual>
              <PersonIcon />
            </ActionList.LeadingVisual>
            Account
          </ActionList.Item>
          <ActionList.Divider />
          <ActionList.Item variant="danger" onSelect={handleLogout}>
            <ActionList.LeadingVisual>
              <SignOutIcon />
            </ActionList.LeadingVisual>
            Abmelden
          </ActionList.Item>
        </ActionList>
      </ActionMenu.Overlay>
    </ActionMenu>
  )
}

export default UserMenu
