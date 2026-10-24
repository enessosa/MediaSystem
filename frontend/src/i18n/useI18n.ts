import { useContext } from 'react'
import { LanguageContext } from './languageContext'

export function useI18n() {
  const context = useContext(LanguageContext)
  if (!context) {
    throw new Error('useI18n muss innerhalb von <LanguageProvider> verwendet werden')
  }
  return context
}
