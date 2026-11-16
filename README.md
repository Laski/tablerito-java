# Tablerito

A kanban-style task board: one board with lists and cards, with attachments. It is the sample app of the
Infrastructure and Cloud-Native course, and it has problems on purpose: finding them is the
hands-on activity of session 2.

Java 21 and Spring Boot. The tools are in `mise.toml`; you don't need to install Maven, because the
repo ships the Maven wrapper.

## Running it

With [mise](https://mise.jdx.dev), which installs the right JDK, the commands are the same on
Windows, Mac and Linux:

```bash
mise install
mise run dev
```

`mise install` is only needed the first time; it asks whether you trust the repo's `mise.toml`.
Tablerito takes a few seconds to start. Then open http://localhost:8080.

Without mise you need JDK 21 or newer. On Mac or Linux:

```bash
./mvnw spring-boot:run
```

On Windows (PowerShell or cmd):

```bat
mvnw.cmd spring-boot:run
```

## Tests

```bash
mise run test
```

Without mise, `./mvnw test` (on Windows, `mvnw.cmd test`).
