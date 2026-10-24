import { useEffect, useMemo, useState, type ReactNode } from 'react'
import { LanguageContext } from './languageContext'
import { LANGUAGES, translations, type Language } from './translations'

const STORAGE_KEY = 'mediasystem.language'
const DEFAULT_LANGUAGE: Language = 'en'

// Die Sprache ist eine reine Browser-Einstellung, deshalb localStorage statt Backend.
// try/catch, weil localStorage z.B. im privaten Modus Fehler werfen kann.
function readStoredLanguage(): Language {
  try {
    const stored = localStorage.getItem(STORAGE_KEY)
    return LANGUAGES.find((l) => l === stored) ?? DEFAULT_LANGUAGE
  } catch {
    return DEFAULT_LANGUAGE
  }
}

export function LanguageProvider({ children }: { children: ReactNode }) {
  const [language, setLanguageState] = useState<Language>(readStoredLanguage)

  // <html lang> mitziehen, damit Screenreader und Browser-Übersetzer die richtige Sprache annehmen.
  useEffect(() => {
    document.documentElement.lang = language
  }, [language])

  const value = useMemo(
    () => ({
      language,
      t: translations[language],
      setLanguage: (next: Language) => {
        setLanguageState(next)
        try {
          localStorage.setItem(STORAGE_KEY, next)
        } catch {
          // Ohne Speicher gilt die Auswahl nur bis zum Neuladen.
        }
      },
    }),
    [language],
  )

  return <LanguageContext.Provider value={value}>{children}</LanguageContext.Provider>
}
