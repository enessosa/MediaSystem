# @Qualifier bei mehreren Beans gleichen Typs

## Kernfrage: Warum startete die App plötzlich nicht mehr, obwohl der Code kompiliert hat?

Nachdem `ProviderConfig` einen zweiten `RestClient`-Bean (`tmdbRestClient`, für TMDB) bekommen hat,
schlug der Start (lokal wie in der CI) fehl:

```
NoUniqueBeanDefinitionException: No qualifying bean of type 'org.springframework.web.client.RestClient'
available: expected single matching bean but found 2: aniListRestClient,tmdbRestClient
```

`./mvnw compile` war grün — der Fehler passiert erst zur **Laufzeit**, beim Hochfahren des
Spring-Kontexts (Dependency Injection), nicht beim Kompilieren.

## Was war der Auslöser im Code?

`ProviderConfig` hatte bis dahin genau einen `RestClient`-Bean (`aniListRestClient`).
`AniListProvider` injiziert sich einen `RestClient` per Konstruktor:

```java
public AniListProvider(RestClient restclient) { ... }
```

Solange es nur **einen** `RestClient`-Bean gab, konnte Spring eindeutig zuordnen: "es gibt genau
einen Kandidaten vom Typ `RestClient`, den nehme ich." Sobald `tmdbRestClient` dazukam, gab es
**zwei** Kandidaten vom exakt gleichen Typ — und Spring injiziert standardmäßig **nach Typ**, nicht
nach Namen. Für Spring ist "gib mir einen `RestClient`" ab da mehrdeutig, egal wie die Bean-Methoden
im Code heißen.

## Warum reicht der Methodenname der `@Bean`-Methode nicht als Unterscheidung?

Der Bean-*Name* (`aniListRestClient`, `tmdbRestClient` — Spring nimmt dafür standardmäßig den
Methodennamen der `@Bean`-Methode) existiert zwar in der Bean-Registry, wird aber bei der
Konstruktor-Injektion **nicht automatisch** mit dem Parameternamen abgeglichen. Das würde nur
zufällig funktionieren, wenn der Parametername exakt gleich geschrieben ist wie der Bean-Name
(fragil, hängt von Compiler-Flags wie `-parameters` ab) — kein verlässlicher Mechanismus.

## Der Fix: `@Qualifier`

```java
public AniListProvider(@Qualifier("aniListRestClient") RestClient restclient) { ... }
```

`@Qualifier("<Bean-Name>")` sagt Spring explizit: "von den mehreren `RestClient`-Kandidaten will ich
genau den mit diesem Namen." Das macht die Injektion wieder eindeutig, unabhängig von
Parameternamen oder Compiler-Einstellungen.

Zusätzlich musste `application.yaml` angepasst werden: `tmdb.api-key: ${TMDB_API_KEY:}` (leerer
Default statt Pflichtwert ohne Default). Grund: `tmdbRestClient` braucht den Key schon beim
Bean-Bau (`@Value("${tmdb.api-key}")` im Konstruktor-Parameter der `@Bean`-Methode). Ohne Default
scheitert der komplette Spring-Kontext-Start überall dort, wo `TMDB_API_KEY` nicht gesetzt ist — z.B.
in GitHub Actions, wo kein entsprechendes Secret hinterlegt ist. Der leere Default lässt den Kontext
sauber starten; erst ein *echter* Aufruf gegen die TMDB-API würde mit einem leeren/ungültigen Key
scheitern, nicht schon der App-Start oder die Unit-Tests.

## Merksatz

**Sobald zwei Beans vom gleichen Typ existieren, reicht Spring die reine Typ-Injektion nicht mehr —
`@Qualifier("<Bean-Name>")` am Konstruktor-Parameter macht die Zuordnung explizit.** Das betrifft vor
allem Konfigurationsklassen mit mehreren `@Bean`-Methoden desselben Rückgabetyps (hier:
mehrere `RestClient`s für unterschiedliche externe APIs) — ein Muster, das mit jedem weiteren
Provider (als Nächstes: `TmdbProvider` als `@Service`, dann `tmdbRestClient` per `@Qualifier`)
wieder auftaucht. Wichtig auch: Der Fehler zeigt sich erst zur Laufzeit beim Kontext-Start, nicht
beim Kompilieren — deshalb lohnt sich nach so einer Änderung immer ein echter Start (App oder
`@SpringBootTest`-Tests), nicht nur `mvnw compile`.