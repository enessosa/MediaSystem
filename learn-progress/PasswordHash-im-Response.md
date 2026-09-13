# Entities nie direkt als API-Response – das PasswordHash-Problem

## Kernfrage: Warum landet `passwordHash` im JSON, obwohl ich das nie hingeschrieben habe?

Wenn ein Controller eine JPA-**Entity** direkt zurückgibt (z.B. `ResponseEntity<User>`), serialisiert
Jackson automatisch **alle** Felder über die Getter – auch die, die nie für den Client gedacht waren.
Jackson kennt kein Konzept von "das ist intern/sensibel", es sieht nur: Getter vorhanden → Feld kommt
ins JSON.

## Wo das im Projekt konkret auftritt

- **`AuthController.register`**: gibt aktuell `ResponseEntity<User>` zurück. `User` hat ein Feld
  `passwordHash` (BCrypt-Hash) mit `@Getter` (Lombok) → landet im Response-Body. Notiert als offener
  Punkt seit 2026-08-15 (siehe `PROGRESS.md`), noch nicht behoben.
- **Künftiger "Medium hinzufügen"-Endpoint**: `UserEntry.getUser()` liefert die volle `User`-Entity
  inkl. `passwordHash` – gleiches Problem an neuer Stelle, wenn `UserEntry` roh zurückgegeben wird.

## Warum das ein echtes Problem ist, nicht nur Kosmetik

Es ist zwar "nur" der Hash, kein Klartext-Passwort – aber:
- der Hash taucht im Netzwerk-Traffic auf (Browser-Devtools, Proxy-Logs, evtl. Server-Access-Logs)
- wer den Hash hat, kann ihn offline gegen Passwortlisten/Rainbow-Tables angreifen (BCrypt ist zwar
  langsam/salted, aber "gar nicht erst rausgeben" ist immer die bessere Verteidigungslinie)
- generell: alles, was der Server aus Versehen mitschickt, ist ein Informationsleck, auch wenn es
  aktuell (noch) nicht ausgenutzt wird

## Die Lösung: eigenes Response-DTO statt der Entity

Ein DTO (Data Transfer Object) ist eine reine Datenklasse (bei uns: ein `record`) nur für die
API-Antwort, mit genau den Feldern, die der Client wirklich braucht – kein `passwordHash`, keine
internen IDs, die nicht gebraucht werden.

Das Muster gibt es im Projekt schon, nur aus einem anderen Grund entstanden: `MediaSearchResult`
(in `api/dto`) existiert, weil `MediaItem` direkt zurückzugeben wegen der bidirektionalen
`Source`-Referenz eine Endlosschleife beim Serialisieren auslösen würde. Der Fix ist aber derselbe
Mechanismus wie beim PasswordHash-Problem: **Entity rein für die DB, eigenes DTO für die API** –
auch wenn der ursprüngliche Auslöser (Sicherheit vs. Serialisierungs-Bug) ein anderer ist.

## Merksatz

**Entity ist für die Datenbank, DTO ist für die API – nie dasselbe Objekt für beides benutzen.**
Bei jedem neuen Endpoint kurz fragen: "Würde ich dieses Feld wirklich an den Client schicken wollen,
wenn ich es mir bewusst überlegen müsste?" – wenn nein (Passwort-Hash, interne Flags, …), gehört es
nicht in die Response-Klasse.

## Offene Stellen im Projekt (noch zu beheben)

- `AuthController.register`: Response-DTO ohne `passwordHash` bauen (analog `MediaSearchResult`).
- Neuer "Medium hinzufügen"-Endpoint: von Anfang an ein `UserEntryResponse`-DTO bauen, `UserEntry`
  nie direkt zurückgeben.
