# LazyInitializationException & @Transactional

## Kernfrage: Warum crasht `item.getTitle()`, wenn `item` doch eindeutig existiert?

Fehler beim curl-Test von `POST /media/entries` (Fall: Medium existiert schon global, zweiter
User fügt es hinzu):

```
org.hibernate.LazyInitializationException: Could not initialize proxy [MediaItem#1] - no session
	at MediaService.mapUserEntrytoResponse(MediaService.java:129)
```

## Was war der Auslöser im Code?

`Source.mediaItem` ist als `@ManyToOne(fetch = FetchType.LAZY)` deklariert (`Source.java`). In
`MediaService.addToUserList` wurde im "Medium existiert schon"-Zweig so geholt:

```java
item = optionalSource.get().getMediaItem();
```

Das liefert **keinen echten, fertig geladenen `MediaItem`**, sondern einen **Proxy** — ein
Platzhalter-Objekt, das die echten Daten erst nachlädt, sobald man wirklich einen Getter darauf
aufruft. Genau das passierte weiter unten in `mapUserEntrytoResponse(...)`, als `item.getTitle()`
etc. aufgerufen wurde.

## Warum das Nachladen fehlschlug: Hibernate-Session

Jeder Repository-Aufruf (`sourceRepository.findBySourceTypeAndExternalId(...)`,
`userRepository.findById(...)`, …) läuft normalerweise in seiner **eigenen, kurzlebigen**
Datenbank-Session. Ohne explizite Transaktions-Grenze um die ganze Methode herum schließt Hibernate
die Session direkt nach dem jeweiligen Repository-Aufruf wieder. Der Proxy für `item` weiß zwar
noch, *welche* Zeile er nachladen müsste (die ID), hat aber keine offene Verbindung mehr, um das zu
tun → `LazyInitializationException: no session`.

Wichtig: das Problem trat **nur** im "Medium existiert schon"-Zweig auf, nicht im
"neues Medium"-Zweig. Grund: dort wurde `item` frisch mit `new MediaItem()` gebaut und direkt mit
echten Werten befüllt — kein Proxy, also kein Nachladen nötig, kein Crash.

## Der Fix: `@Transactional`

```java
@Transactional
public UserEntryResponse addToUserList(MediaSearchResult result, Long userId) {
    ...
}
```

Mit `@Transactional` (aus `org.springframework.transaction.annotation`) bleibt **eine** Hibernate-
Session für die komplette Methode offen. Der Proxy kann sich jederzeit innerhalb der Methode selbst
nachladen, weil die Verbindung noch lebt.

Nebeneffekt, der auch inhaltlich Sinn ergibt: die zwei Speichervorgänge in der Methode
(`MediaItem` speichern, dann `UserEntry` speichern) werden dadurch **atomar** — schlägt einer fehl,
wird alles zurückgerollt, statt dass z.B. ein `MediaItem` ohne zugehörigen `UserEntry` stehen
bleibt.

## Merksatz

**Lazy-geladene Assoziationen (`FetchType.LAZY`) brauchen eine offene Hibernate-Session, sobald man
über den ersten Repository-Aufruf hinaus auf sie zugreift.** Bei Service-Methoden, die mehrere
Repositories/Assoziationen anfassen und deren Ergebnis über den ursprünglichen Aufruf hinaus
weiterverwendet wird, gehört `@Transactional` an die Methode — sonst tickt eine
`LazyInitializationException`, die aber nur in bestimmten Codepfaden (hier: "Objekt existiert
schon" statt "Objekt wird neu gebaut") überhaupt sichtbar wird. Deshalb reicht ein Test mit nur
einem Szenario oft nicht aus — genau dieser Bug wurde erst durch den zweiten curl-Testfall
(existierendes Medium, anderer User) gefunden, nicht durch den ersten (neues Medium).
