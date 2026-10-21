# Wer ist eingeloggt? `GET /users/me` und der Weg vom Session-Cookie zum `Authentication`-Objekt

## Kernfrage: Woher weiß das Frontend nach einem Reload, wer eingeloggt ist?

Nach dem Login hält der Browser ein Session-Cookie (`JSESSIONID`). Das Cookie ist `HttpOnly` —
JavaScript kann es nicht lesen, und selbst wenn: es ist nur eine zufällige ID, kein Username.
Alles, was das Frontend über den eingeloggten User weiß, stammt aus der Antwort von
`POST /auth/login`. Nach einem Reload (oder in einem neuen Tab) ist dieser JavaScript-Zustand weg,
die Session auf dem Server aber noch gültig. Das Frontend muss also **den Server fragen**: „Zu
welchem User gehört die Session, die mein Browser gerade mitschickt?" — genau das ist `/users/me`
(`me` = „der User, der gerade fragt").

## Wie aus einem Cookie ein `Authentication`-Objekt wird

Beim Login passiert (im `AuthController`):

```java
SecurityContext context = SecurityContextHolder.createEmptyContext();
context.setAuthentication(auth);
repository.saveContext(context, httpServletRequest, httpServletResponse); // -> HttpSession
```

Der `SecurityContext` (mit dem `Authentication`-Objekt darin) wird in der `HttpSession` auf dem
Server abgelegt; der Browser bekommt nur die Session-ID als Cookie.

Bei **jedem folgenden Request** läuft es rückwärts, ganz vorne im Security-Filter-Chain:

1. Der Browser schickt das Cookie `JSESSIONID=...` mit (Frontend: `credentials: 'include'`).
2. Der `SecurityContextHolderFilter` fragt das `SecurityContextRepository`
   (`HttpSessionSecurityContextRepository`): „Gibt es zu dieser Session einen gespeicherten
   `SecurityContext`?"
3. Wenn ja, wird er für die Dauer dieses einen Requests in den `SecurityContextHolder` gelegt
   (thread-lokal — jeder Request hat seinen eigenen).
4. Wenn nein, setzt der `AnonymousAuthenticationFilter` später ein
   `AnonymousAuthenticationToken` ein („anonymousUser").

Im Controller kommt man dann an den User, ohne den `SecurityContextHolder` selbst anzufassen:

```java
@GetMapping("/media/entries")
public ResponseEntity<...> getUserEntries(Authentication authentication) {
    String username = authentication.getName();
    ...
}
```

Spring MVC füllt den Parameter `Authentication` aus `request.getUserPrincipal()`, und das liefert
Spring Security aus dem `SecurityContextHolder`. Wichtig: `getName()` ist der **Username**, der
beim Login im `UserDetails` stand (`AppUserDetailsService`) — auch wenn sich jemand per E-Mail
eingeloggt hat. Id, E-Mail usw. stehen *nicht* im `Authentication`-Objekt, die holt man über den
Username aus der DB.

## Der Endpoint

```java
@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/me")
    public ResponseEntity<@NonNull MeResponse> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(userService.getCurrentUser(username));
    }
}
```

```java
public MeResponse getCurrentUser(String username) {
    User user = userRepository.findByUsername(username).orElseThrow();
    boolean isAdmin = user instanceof Admin;
    return new MeResponse(user.getId(), user.getUsername(), user.getEmail(), isAdmin);
}
```

Zurück kommt ein eigenes DTO `MeResponse` statt der `User`-Entity (sonst landet `passwordHash` im
JSON, siehe `PasswordHash-im-Response.md`).

**Admin oder nicht?** Es gibt kein `role`-Feld an der Entity — trotzdem ist die Info da. `Admin`
ist eine Unterklasse von `User` (`@Inheritance`, Standard: `SINGLE_TABLE`), beide liegen in
derselben Tabelle `app_user`, die Discriminator-Spalte `role` steht auf `USER` oder `ADMIN`.
Hibernate liest diese Spalte und erzeugt je nach Wert ein `User`- **oder ein `Admin`-Objekt**.
Der Rückgabetyp `Optional<User>` verdeckt das nur — mit `instanceof Admin` fragt man ab, was das
Objekt wirklich ist. Genau so vergibt auch `AppUserDetailsService` beim Login `ROLE_ADMIN`, die
Rolle hat also nur eine einzige Quelle (die DB). Die Alternative, `authentication.getAuthorities()`
im Controller auszuwerten, würde Spring-Security-Details in die Response-Logik ziehen.

## Warum `/users/me` und nicht `/auth/me`?

Ursprünglich war `/auth/me` im `AuthController` geplant. In diesem Projekt ist `/auth/**` aber
`permitAll()` (Login/Registrierung müssen ohne Session erreichbar sein). Ein Endpoint dort hätte
zwei Fallen mitgebracht:

### Falle 1: `permitAll()` + `Authentication`-Parameter = `null`

Für anonyme Requests liefert `request.getUserPrincipal()` **`null`**, nicht das
`AnonymousAuthenticationToken`. Bei Endpoints hinter `.authenticated()` fällt das nie auf, weil
anonyme Requests gar nicht erst beim Controller ankommen. Liegt ein Endpoint aber unter einer
`permitAll()`-Regel (in diesem Projekt: `/auth/**`), kommt der anonyme Request durch — und
`authentication.getName()` wirft eine `NullPointerException` → `500` statt einer sauberen
„nicht eingeloggt"-Antwort.

### Falle 2: Reihenfolge der Regeln in `authorizeHttpRequests`

Die erste passende Regel gewinnt. Soll ein einzelner Pfad unter `/auth/**` geschützt sein, muss
seine Regel **vor** der breiten `permitAll()`-Regel stehen:

```java
auth.requestMatchers("/auth/me").authenticated();   // speziell zuerst
auth.requestMatchers("/auth/**").permitAll();       // dann allgemein
```

Andersherum wird `/auth/me` schon von `/auth/**` erfasst und die zweite Regel nie geprüft (siehe
auch `Defense-in-Depth-Autorisierung.md`, Punkt „Reihenfolge der Regeln").

### Die Lösung: den Endpoint gar nicht erst unter `permitAll()` legen

`/users/me` fällt automatisch unter `anyRequest().authenticated()`. Anonyme Requests werden schon
im Filter-Chain abgewiesen, `authentication` kann im Controller nie `null` sein, und es braucht
keine Sonderregel, deren Reihenfolge man falsch hinbekommen kann — **sicher per Default**: eine
Regel, die man nicht schreiben muss, kann man nicht falsch schreiben. Nebeneffekt: `/auth/**` heißt
jetzt eindeutig „für Leute ohne Session" (Login, Registrierung), und die geplanten
Account-Endpoints passen als Ressource dazu:

```
GET    /users/me            → eigene Daten
PUT    /users/me/password   → Passwort ändern
DELETE /users/me            → Account löschen
```

## 401 oder 403?

- **401 Unauthorized** heißt eigentlich „nicht authentifiziert" (wer bist du?).
- **403 Forbidden** heißt „authentifiziert, aber nicht berechtigt" (ich weiß, wer du bist, aber nein).

Ohne `formLogin()`/`httpBasic()` antwortet Spring Security bei fehlender Anmeldung standardmäßig
mit **403** (`Http403ForbiddenEntryPoint`). Will man korrekt `401`, setzt man den Entry Point
selbst:

```java
http.exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));
```

## Merksatz

**Das Session-Cookie ist nur ein Schlüssel — der eigentliche Zustand („wer ist eingeloggt")
liegt auf dem Server.** Ein Client, der wissen will, wer er ist, muss fragen. Spring Security
stellt den User pro Request im `SecurityContextHolder` bereit; im Controller reicht ein
`Authentication`-Parameter — der aber nur dann garantiert nicht `null` ist, wenn der Endpoint
hinter einer `authenticated()`-Regel liegt. Deshalb Endpoints, die einen eingeloggten User
brauchen, dort platzieren, wo die Default-Regel sie schon schützt, statt Ausnahmen von Ausnahmen
zu bauen.
