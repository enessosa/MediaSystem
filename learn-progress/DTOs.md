# DTOs (Data Transfer Objects) – Warum, Wofür, Wo im Projekt

## Kernfrage: Warum nicht einfach die Entity/das externe JSON direkt durchreichen?

Eine DTO ist eine reine Datenklasse, die nur dazu da ist, Daten von A nach B zu transportieren – ohne
Verhalten, ohne Datenbank-Bezug, ohne mehr Feldern als nötig. Der Grund, warum man sie überhaupt
braucht, statt einfach die JPA-Entity oder das rohe externe JSON weiterzureichen: **die Form, die eine
Entity für die Datenbank braucht, ist selten dieselbe Form, die nach außen (Client) oder von außen
(externe API) sinnvoll ist.**

## Drei verschiedene DTO-Rollen im Projekt

### 1. Request-DTOs (Client → Server)

Bilden genau das ab, was ein Endpoint an Eingabe braucht – nicht mehr, nicht weniger – und tragen die
Bean-Validation-Annotationen (`@NotBlank`, `@Email`, `@Size`, `@Min`/`@Max`), die über `@Valid` im
Controller automatisch geprüft werden.

- `RegisterRequest` (`username`, `email`, `password`, `codeword`)
- `LoginRequest` (`identifier`, `password`)
- `UpdateUserEntryRequest` (`status`, `rating`, `note`)

**Wichtige Regel, die unterwegs gelernt wurde:** Validierungs-Annotationen gehören *nur* auf
Request-DTOs. Eine Response-Klasse baut der Server selbst zusammen – die muss nicht validiert werden,
weil sie nie von außen kommt.

### 2. Response-DTOs (Server → Client)

Bilden genau das ab, was der Client sehen soll – nicht die komplette Entity mit allen internen
Feldern/Relationen.

- `MediaSearchResult`: `MediaItem` hat eine bidirektionale `Source`-Referenz
  (`MediaItem.sources` ↔ `Source.mediaItem`) – die Entity direkt zurückzugeben würde Jackson beim
  Serialisieren in eine Endlosschleife laufen lassen. Frische Suchergebnisse sind außerdem noch keine
  gespeicherten Entities (keine ID), ein eigenes DTO passt also ohnehin besser.
- `UserEntryResponse`: `UserEntry.getUser()` liefert die volle `User`-Entity inkl. `passwordHash` –
  siehe `PasswordHash-im-Response.md` für die ausführliche Geschichte dazu. Deshalb: eigenes DTO ohne
  `User`-Relation, dafür mit den `MediaItem`-Feldern flach mit reinkopiert (`mediaItemid`, `title`, …),
  damit der Client nicht extra nachladen muss.

### 3. Externe-API-DTOs (externe API → Server, nie nach außen sichtbar)

Eine dritte Rolle, die leicht übersehen wird: die Response-Records, die die **fremde** JSON-Struktur
eines Providers 1:1 abbilden, nur um sie zu deserialisieren – `AniListSearchResponse`,
`TmdbSearchResponse`, `RawgSearchResult`, `OpenLibrarySearchResponse`. Diese DTOs:

- spiegeln exakt das Schema der externen API (inkl. `@JsonProperty("snake_case_feld")`, wenn der
  Provider snake_case liefert wie TMDB/RAWG/OpenLibrary, aber die Java-Konvention camelCase ist)
- werden **nie** an den eigenen Client weitergereicht – sie existieren nur kurz beim Deserialisieren,
  werden sofort auf `MediaItem`/`Source` gemappt (`mapToMediaItems(...)`) und dann verworfen
- eine eigene Regel entstand hier: **Anzahl der verschachtelten Records = Anzahl der `{ }`-Ebenen im
  JSON** – AniLists tief verschachtelte GraphQL-Antwort brauchte viele Records (`Title`, `StartDate`,
  `CoverImage`, `Staff`, …), TMDBs/RAWGs/OpenLibrarys flachere REST-Antworten brauchen nur 1-2.

## Warum Records die natürliche Wahl für DTOs sind

Ein `record` ist die kürzeste Java-Form für eine reine Datenklasse: Konstruktor, Getter, `equals`/
`hashCode`/`toString` kommen automatisch, unveränderlich (keine Setter) – genau das, was ein DTO
braucht und nichts, was es nicht braucht. Entities dagegen sind bewusst *keine* Records (brauchen
JPA-Annotationen, Lazy-Loading-Proxies, `@NoArgsConstructor` für Hibernate, veränderliche Felder) –
ein weiterer Grund, Entity und DTO nie zu vermischen.

## Merksatz

**Die Form eines DTOs folgt seiner Richtung.** Request-DTO: genau das, was validiert werden muss.
Response-DTO: so wenig wie nötig, nie eine rohe Entity. Externe-API-DTO: exakt das Schema der fremden
Antwort, nur intern genutzt, nie nach außen exponiert. Bei jedem neuen Endpoint/Provider die Frage:
"Welche der drei Rollen ist das gerade, und passt die Form dazu?"
