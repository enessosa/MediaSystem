import { BaseStyles } from '@primer/react'
import { BrowserRouter, Route, Routes } from 'react-router'
import { AuthProvider } from './auth/AuthProvider'
import RequireAuth from './auth/RequireAuth'
import AppLayout from './layout/AppLayout'
import PublicLayout from './layout/PublicLayout'
import AccountPage from './pages/AccountPage'
import DashboardPage from './pages/DashboardPage'
import LoginPage from './pages/LoginPage'
import MediaDetailPage from './pages/MediaDetailPage'
import MyListPage from './pages/MyListPage'
import NotFoundPage from './pages/NotFoundPage'
import RegisterPage from './pages/RegisterPage'
import SearchPage from './pages/SearchPage'

function App() {
  return (
    <BaseStyles>
      <AuthProvider>
        <BrowserRouter>
          <Routes>
            <Route element={<RequireAuth />}>
              <Route element={<AppLayout />}>
                <Route path="/" element={<DashboardPage />} />
                <Route path="/search" element={<SearchPage />} />
                <Route path="/list" element={<MyListPage />} />
                <Route path="/entries/:entryId" element={<MediaDetailPage />} />
                <Route path="/media/:sourceType/:externalId" element={<MediaDetailPage />} />
                <Route path="/account" element={<AccountPage />} />
              </Route>
            </Route>
            <Route element={<PublicLayout />}>
              <Route path="/login" element={<LoginPage />} />
              <Route path="/register" element={<RegisterPage />} />
              <Route path="*" element={<NotFoundPage />} />
            </Route>
          </Routes>
        </BrowserRouter>
      </AuthProvider>
    </BaseStyles>
  )
}

export default App
