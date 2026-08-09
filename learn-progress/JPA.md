# JPA & Spring Data – Repositories als Interfaces

## Kernfrage: Wenn alle Repositories nur Interfaces sind, wo werden sie implementiert?

Das ist eine **Spring Data JPA-Sache**. Man schreibt nur das Interface – die Implementierung
erzeugt Spring automatisch zur Laufzeit. Es gibt bewusst **keine** `UserRepositoryImpl`-Datei.

## Wie funktioniert das?

Beim Start scannt Spring Boot alle Interfaces, die `JpaRepository` erweitern, und erzeugt für
jedes einen **Proxy** – eine dynamisch generierte Klasse, die das Interface implementiert. Diese
Proxy-Instanz landet als **Bean** im Spring-Container. Die Implementierung existiert also nur als
Objekt im Speicher, nicht als Quelldatei.

Die Methoden werden auf zwei Arten „gefüllt":

- **Geerbte Methoden** (`save`, `findById`, `count`, `delete`, …)
  → kommen aus Spring Datas Basis-Implementierung `SimpleJpaRepository`.
- **Eigene Query-Methoden** (`findByUsername`, `existsByUserIdAndMediaItemId`)
  → Spring **parst den Methodennamen** und leitet daraus die JPQL/SQL-Query ab.
  Beispiel: `findByUsername` → `SELECT u FROM User u WHERE u.username = ?`.
  Deshalb ist die exakte Benennung wichtig: Die Namensteile müssen zu den Entity-Feldern passen.

## Wo werden sie benutzt?

Nicht per `new` (bei einem Interface ohnehin unmöglich), sondern per **Dependency Injection** –
Spring reicht die Proxy-Bean rein. Das passiert im Service-Layer, z.B. bei der Registrierung:

```java
@Service
public class UserService {

    private final UserRepository userRepository;

    // Constructor Injection: Spring reicht den Proxy hier rein
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean isUsernameTaken(String username) {
        return userRepository.existsByUsername(username);
    }
}
```

## Bezug zum Projekt

- Aktuell werden die Repositories **noch nirgends benutzt**, weil es noch keinen Service-Layer
  gibt. Das ist normal: Die Repos sind das Fundament, die Nutzung kommt mit Auth/Service-Logik.
- Das passt zu **ADR-002** (Layered Architecture): Der Service hängt vom *Interface* ab, nicht von
  einer konkreten Klasse. Spring liefert die konkrete Implementierung, ohne dass der eigene Code
  sie kennt (Dependency Inversion).

## Merksatz

Interface schreiben = Vertrag definieren → Spring liefert die Umsetzung (Proxy-Bean) → per
Konstruktor in den Service injizieren.
