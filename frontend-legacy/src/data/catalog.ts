import type { CatalogItem } from '../types/media'

/**
 * Beispiel-Katalog, simuliert Treffer aus den externen Provider-APIs
 * (AniList/TMDB/OpenLibrary). Ersetzen, sobald der echte Such-Endpoint
 * im Backend steht (AniListProvider.searchMedia ist serverseitig schon
 * fertig, hängt aber noch an keinem Controller).
 */
export const CATALOG: CatalogItem[] = [
  {
    id: 'anilist-20',
    title: 'Naruto',
    description:
      'Ein junger Ninja mit einem versiegelten Fuchsdämon in sich kämpft darum, von seinem Dorf anerkannt zu werden und eines Tages Hokage zu werden.',
    releaseYear: 2002,
    mediaType: 'ANIME',
    creator: 'Masashi Kishimoto',
    coverColor: '#e07a3f',
  },
  {
    id: 'anilist-30011',
    title: 'Naruto',
    description:
      'Die Manga-Vorlage: Zwölf Jahre nach dem Angriff des neunschwänzigen Fuchses kämpft sich Naruto durch die Ninja-Akademie.',
    releaseYear: 1999,
    mediaType: 'MANGA',
    creator: 'Masashi Kishimoto',
    coverColor: '#c9581f',
  },
  {
    id: 'anilist-101922',
    title: 'Attack on Titan: The Final Season',
    description:
      'Die Menschheit steht kurz davor, ihre letzte Freiheit zu verlieren – oder ihren letzten Feind zu besiegen.',
    releaseYear: 2020,
    mediaType: 'ANIME',
    creator: null,
    coverColor: '#3f5e6b',
  },
  {
    id: 'tmdb-1399',
    title: 'Game of Thrones',
    description:
      'Adelshäuser kämpfen um den Eisernen Thron, während im Norden eine uralte Bedrohung erwacht.',
    releaseYear: 2011,
    mediaType: 'SERIES',
    creator: null,
    coverColor: '#4a4638',
  },
  {
    id: 'tmdb-66732',
    title: 'Stranger Things',
    description:
      'In einer Kleinstadt in den 80ern verschwindet ein Junge spurlos – der Anfang übernatürlicher Ereignisse.',
    releaseYear: 2016,
    mediaType: 'SERIES',
    creator: null,
    coverColor: '#7a1f2b',
  },
  {
    id: 'openlibrary-hobbit',
    title: 'Der Hobbit',
    description:
      'Bilbo Beutlin wird von dem Zauberer Gandalf und dreizehn Zwergen auf ein unerwartetes Abenteuer mitgenommen.',
    releaseYear: 1937,
    mediaType: 'BOOK',
    creator: 'J.R.R. Tolkien',
    coverColor: '#2f6b4f',
  },
  {
    id: 'openlibrary-martian',
    title: 'Der Marsianer',
    description:
      'Ein Astronaut wird auf dem Mars zurückgelassen und muss mit reinem Erfindungsgeist ums Überleben kämpfen.',
    releaseYear: 2011,
    mediaType: 'BOOK',
    creator: 'Andy Weir',
    coverColor: '#8a4b1f',
  },
  {
    id: 'anilist-113415',
    title: 'Jujutsu Kaisen',
    description:
      'Ein Highschool-Schüler verschluckt einen verfluchten Finger und wird in eine Welt aus Flüchen und Exorzisten gezogen.',
    releaseYear: 2020,
    mediaType: 'ANIME',
    creator: null,
    coverColor: '#5b2f6b',
  },
]