# Defense in Depth bei Autorisierung: Warum eine URL-Regel allein nicht reicht

## Kernfrage: Reicht `SecurityConfig`s URL-Regel, um einen Admin-Endpoint zu schützen?

Plan für einen neuen Endpoint, der das Signup-Codewort ändern kann (nur für Admins gedacht):

```java
auth.requestMatchers("/admin/**").hasRole("ADMIN");
auth.anyRequest().authenticated();
```

Das ist eine echte, funktionierende Sicherheitsregel — Spring Security prüft sie zentral im
Filter-Chain, bevor der Request überhaupt beim Controller ankommt. Die Frage war trotzdem
berechtigt: **Ist das die einzige Stelle, an der diese Prüfung passiert — und was, wenn genau
diese eine Stelle mal versagt?**

## Drei reale Wege, wie eine reine URL-Regel versagen kann

1. **Pfad-Match ist unvollständig.** `/admin/**` schützt exakt diesen Pfad-Präfix. Ein zweiter
   Admin-Endpoint unter einem anderen Pfad (Tippfehler, ein neuer Controller, ein Alias-Mapping)
   fällt einfach nicht unter die Regel — und das fällt beim Schreiben des neuen Endpoints nicht
   automatisch auf, weil der Code weiterhin kompiliert und "irgendwie" funktioniert.
2. **Reihenfolge der Regeln.** `authorizeHttpRequests`-Regeln werden in Deklarationsreihenfolge
   geprüft, die erste passende Regel gewinnt. Steht eine breitere Regel (z.B. ein `permitAll()`
   für einen anderen Zweck) versehentlich *vor* der Admin-Regel, überdeckt sie diese unbemerkt.
3. **Die Regel prüft nur den HTTP-Request, nicht den Methodenaufruf selbst.** Sie sitzt im
   Servlet-Filter-Chain — das schützt ausschließlich Aufrufe, die tatsächlich über diesen einen
   HTTP-Pfad reinkommen. Wird die dahinterliegende Service-Methode später von woanders aus
   aufgerufen (ein zweiter Controller, ein Scheduled Job, ein interner Aufruf), greift die
   URL-Regel gar nicht — es gibt ja keinen HTTP-Request, den sie filtern könnte.

Allen drei Fällen gemeinsam: es ist **eine einzige Schutzschicht**, deren Korrektheit von Hand
sichergestellt werden muss (richtiger Pfad, richtige Reihenfolge, richtiger Aufrufweg). Fällt sie
aus einem dieser Gründe aus, gibt es nichts, was den Zugriff trotzdem verhindert.

## Der Fix: eine zweite, unabhängige Schutzschicht direkt an der Methode

**Defense in Depth** heißt: sensible Aktionen nicht nur an einer Stelle prüfen, sondern an
mehreren unabhängigen Stellen — fällt eine aus, greift die andere trotzdem. Für Autorisierung in
Spring Security heißt das konkret: zusätzlich zur URL-Regel eine Prüfung direkt an der Methode,
die die sensible Aktion ausführt.

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity   // aktiviert @PreAuthorize/@PostAuthorize projektweit
public class SecurityConfig { ... }
```

```java
@Service
public class AdminService {

    @PreAuthorize("hasRole('ADMIN')")
    public void updateCodeword(String codeword) { ... }
}
```

`@PreAuthorize` prüft die Rolle des aktuell eingeloggten Nutzers **bei jedem Aufruf dieser
Methode**, egal von wo aus sie gerufen wird — nicht nur bei Aufrufen über den einen HTTP-Pfad.
Fällt die URL-Regel in `SecurityConfig` durch einen der drei oben genannten Fälle aus, blockt
`@PreAuthorize` den Zugriff trotzdem, weil es eine komplett unabhängige Prüfung ist (Spring AOP
statt Servlet-Filter).

## Wo platziert man die zweite Schicht: Controller oder Service?

Im Zweifel auf der **Service-Methode**, nicht (nur) auf dem Controller. Der Controller ist nur
der HTTP-Einstiegspunkt — die eigentlich sensible Aktion (hier: das Codewort ändern) passiert in
der Service-Schicht. Sitzt die Prüfung dort, ist sie automatisch für jeden zukünftigen Aufrufer
dieser Methode wirksam, nicht nur für den einen Controller, der heute existiert.

## Merksatz

**Eine einzelne Autorisierungs-Prüfung ist ein Single Point of Failure — sie ist immer nur so
sicher wie ihre eigene Korrektheit (richtiger Pfad, richtige Reihenfolge, richtiger Aufrufweg).**
Bei allem, was wirklich sensibel ist (Admin-Aktionen, Zahlungen, Löschoperationen, fremde Daten),
lohnt sich eine zweite, unabhängige Prüfung direkt an der Stelle, wo die Aktion passiert —
in Spring Security typischerweise die Kombination aus `authorizeHttpRequests` (grobe
URL-Filterung) und `@PreAuthorize` auf der Service-Methode (Methoden-Prüfung, unabhängig vom
Aufrufweg). Dieses Prinzip ist nicht Spring-spezifisch — es gilt überall dort, wo eine einzige
Bedingung entscheidet, ob eine sensible Aktion erlaubt ist: lieber zwei unabhängige Wächter als
einen, der zufällig immer an der richtigen Stelle steht.
