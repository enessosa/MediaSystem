# MediaSystem

A self-hosted web app for tracking media (anime, manga, books, series): search via external APIs, add items to a personal list, manage status/rating/notes.

This is primarily a **learning project**. The goal isn't just to ship a feature-complete app, but to practice the full lifecycle of a real system end to end:

- designing an architecture (layered, provider/repository interfaces, ADRs for the decisions that matter)
- building it properly (Java/Spring Boot backend, React frontend, Postgres, tested)
- running it in CI
- eventually deploying and operating it myself (Docker, reverse proxy, HTTPS, self-hosted)

## Tech Stack

- **Backend:** Java, Spring Boot, Maven
- **Frontend:** React
- **Database:** Postgres (Flyway migrations)
- **Auth:** Spring Security (server-side sessions first, JWT later)
- **External APIs:** AniList, TMDB, OpenLibrary
- **CI/CD:** GitHub Actions
- **Deployment (planned):** Docker / docker-compose, reverse proxy, self-hosted

## Project Status

Early stage, actively in development. Architecture decisions are tracked as ADRs in [`Documentation/ADRs`](Documentation/ADRs).

## Planning & Design

Before writing any code, I deliberately spent time designing the system: modeling the domain, mapping out workflows, and thinking through the architecture and deployment up front rather than figuring it out along the way. That planning is captured in the following diagrams (in [`Documentation/Diagrams`](Documentation/Diagrams)):

- [Use Case Diagram](Documentation/Diagrams/UML-UseCase-Diagram/UML-UseCase-Diagram.jpg)
- [Component Diagram](Documentation/Diagrams/UML-Component-Diagram/UML-Component-Diagram.jpg)
- [Class Diagram](Documentation/Diagrams/UML-Class-Diagram/Class-Diagram.jpg)
- [Package Diagram](Documentation/Diagrams/UML-Package-Diagram/Package-Diagram.jpg)
- [Sequence Diagram](Documentation/Diagrams/UML-Sequence-Diagram/UML-Sequence-Diagram.jpg)
- [Deployment Diagram](Documentation/Diagrams/UML-Deployment-Diagram/UML-Deployment-Diagram.jpg)
- [ER Diagram](Documentation/Diagrams/ER-Diagram/ER-Diagram.jpg)
- [Activity Diagram – Registration](Documentation/Diagrams/UML-Activity-Diagram/Activity-Diagram-Registration.jpg)
- [Activity Diagram – Search Media](<Documentation/Diagrams/UML-Activity-Diagram/Activity Diagram - Search Media.jpg>)
- [Activity Diagram – Add Media](<Documentation/Diagrams/UML-Activity-Diagram/Activity Diagram - Add Media.jpg>)

## Architecture

```
React frontend → REST controllers → service layer → provider/repository interfaces → external API / database
```

External API integrations (AniList, TMDB, OpenLibrary) sit behind a `Provider` interface so the service layer never depends on a specific provider — see [ADR-002](Documentation/ADRs/ADR-002.md).
