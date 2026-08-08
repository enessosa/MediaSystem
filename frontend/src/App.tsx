import { Routes, Route, Navigate } from 'react-router-dom'
import AuthLayout from './layouts/AuthLayout'
import AppLayout from './layouts/AppLayout'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import DashboardPage from './pages/DashboardPage'
import WatchlistsPage from './pages/WatchlistsPage'
import SearchPage from './pages/SearchPage'
import SearchResultsPage from './pages/SearchResultsPage'
import DiscoveryPage from './pages/DiscoveryPage'
import MediaDetailPage from './pages/MediaDetailPage'
import CreateItemPage from './pages/CreateItemPage'
import ProfilePage from './pages/ProfilePage'
import SettingsPage from './pages/SettingsPage'
import NotFoundPage from './pages/NotFoundPage'

function App() {
  return (
    <Routes>
      {/* 0. Auth-Scenes: eigenes Layout ohne Header (Login / Register) */}
      <Route element={<AuthLayout />}>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
      </Route>

      {/* Eingeloggte Scenes: gemeinsamer Header + Navigation */}
      <Route element={<AppLayout />}>
        <Route path="/" element={<DashboardPage />} />
        <Route path="/watchlists" element={<WatchlistsPage />} />
        <Route path="/search" element={<SearchPage />} />
        <Route path="/search/results" element={<SearchResultsPage />} />
        <Route path="/discovery" element={<DiscoveryPage />} />
        <Route path="/media/:id" element={<MediaDetailPage />} />
        <Route path="/create" element={<CreateItemPage />} />
        <Route path="/profile" element={<ProfilePage />} />
        <Route path="/settings" element={<SettingsPage />} />
      </Route>

      {/* Fallback: unbekannte URL */}
      <Route path="/404" element={<NotFoundPage />} />
      <Route path="*" element={<Navigate to="/404" replace />} />
    </Routes>
  )
}

export default App