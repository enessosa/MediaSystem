# Vorsicht: Es gibt mehrere verschiedene `@NonNull`/`@NotNull`-Annotationen

## Kernfrage: Warum wurde `UpdateUserEntryRequest.status` trotz `@Valid` nie auf `null` geprüft?

```java
public record UpdateUserEntryRequest(
        @NonNull Status status,
        @Min(1) @Max(10) Integer rating,
        String note
) { }
```

Sieht auf den ersten Blick korrekt aus: `status` ist mit `@NonNull` markiert, der Controller nimmt
den Request mit `@Valid` entgegen. Trotzdem wäre `{"status": null, ...}` nie mit einem `400`
abgelehnt worden — die Bean-Validation-Prüfung lief für dieses Feld schlicht nie.

## Der Grund: Es gibt nicht "ein" `@NonNull` — mehrere Bibliotheken benutzen exakt denselben Namen

In diesem Projekt (und in Java allgemein) sind mindestens drei komplett unterschiedliche
Annotationen mit ähnlichem Namen im Spiel, je nachdem, welcher Import gezogen wird:

| Import | Woher | Was macht sie wirklich |
|---|---|---|
| `lombok.NonNull` | Lombok | Generiert **zur Compile-Zeit** einen `if (x == null) throw new NullPointerException(...)` im Konstruktor/Setter. Hat mit Jakarta Bean Validation (`@Valid`) **nichts** zu tun. |
| `jakarta.validation.constraints.NotNull` | Jakarta Bean Validation | Das ist der echte **Validation-Constraint**, den `@Valid` beim Prüfen eines Objekts tatsächlich auswertet. |
| `org.jspecify.annotations.NonNull` | JSpecify | Reine **statische Dokumentation** für Tools/IDE (z.B. "dieser Parameter ist laut Vertrag nie `null`"). Kein Laufzeitverhalten, keine Exception, keine Validation — nur eine Anmerkung für Compiler-/IDE-Analysen. |

Auf einem `record`-Feld tut `lombok.NonNull` (Variante 1) praktisch **gar nichts** – Lombok
generiert den Null-Check normalerweise in einen von ihm selbst erzeugten Konstruktor/Setter,
bei `record`s greift das nicht zuverlässig auf dieselbe Weise wie bei einer normalen Klasse.
Der Compiler beschwert sich nicht, IntelliJ beschwert sich nicht (`@NonNull` ist ja "irgendein"
gültiges Nullability-Attribut) — aber `@Valid` ignoriert das Feld komplett, weil Jakarta Bean
Validation ausschließlich Annotationen aus dem `jakarta.validation.constraints`-Paket als
Constraints erkennt.

## Der Fix

```java
import jakarta.validation.constraints.NotNull;

public record UpdateUserEntryRequest(
        @NotNull Status status,
        @Min(1) @Max(10) Integer rating,
        String note
) { }
```

Erkennungsmerkmal beim Draufschauen: **`@NotNull`** (Jakarta) vs. **`@NonNull`** (Lombok) — nur ein
Buchstabe Unterschied im Namen, aber komplett unterschiedliche Wirkung. Die IDE-Autovervollständigung
schlägt oft zuerst den Lombok-Import vor, wenn der schon irgendwo im Projekt importiert ist (hier:
weil andere Klassen `lombok.NonNull` für `ResponseEntity<@NonNull ...>` nutzen) — man landet also
leicht beim falschen, obwohl der Name fast identisch aussieht.

## Wo `org.jspecify.annotations.NonNull` im Projekt trotzdem der richtige Import ist

Ganz anderer Anwendungsfall: `AppUserDetailsService.loadUserByUsername` überschreibt eine Methode
aus einem Spring-Security-Interface, dessen Paket mit `@NullMarked` (JSpecify) annotiert ist —
das ist ein reiner **Dokumentations-/Tooling-Vertrag** ("dieser Parameter/Rückgabewert ist laut
API-Vertrag nie `null`"), kein Validation-Constraint. Hier ist `org.jspecify.annotations.NonNull`
tatsächlich der korrekte Import, weil es genau diesen Vertrag widerspiegelt — nur eben nicht bei
einem `@Valid`-validierten Request-DTO, wo es um echte Laufzeit-Validierung von Nutzereingaben geht.

## Merksatz

**Drei verschiedene Pakete, drei verschiedene Bedeutungen, gleicher/ähnlicher Name:**
- `jakarta.validation.constraints.NotNull` → echter Validation-Constraint, wird von `@Valid`
  tatsächlich geprüft. Das ist die richtige Wahl für **Request-DTOs**.
- `lombok.NonNull` → Compile-Zeit-Codegenerierung eines Null-Checks in Konstruktor/Setter,
  wirkt unabhängig von Bean Validation.
- `org.jspecify.annotations.NonNull` → reine statische Dokumentation für IDE/Compiler, kein
  Laufzeitverhalten. Richtig, wenn eine überschriebene Methode aus einem `@NullMarked`-Paket
  denselben Vertrag dokumentieren soll (z.B. Spring-Security-/Spring-Web-Interfaces).

Bei jedem neuen `@NonNull`/`@NotNull` immer kurz den Import-Vorschlag der IDE gegenlesen, nicht
blind die erste Autovervollständigung nehmen — besonders bei Request-DTOs, wo eine falsche Wahl
eine Validierung **stillschweigend** wirkungslos macht (kein Compilefehler, kein Testfehler, außer
man schreibt gezielt einen Test dafür).
