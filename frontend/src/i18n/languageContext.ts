import { createContext } from 'react'
import type { Language, Translations } from './translations'

export interface LanguageContextValue {
  language: Language
  setLanguage: (language: Language) => void
  /** Alle Texte der Oberfläche in der aktuellen Sprache, z.B. `t.common.save`. */
  t: Translations
}

export const LanguageContext = createContext<LanguageContextValue | null>(null)
