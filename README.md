# Arte7 Backend

Spring Boot REST API exposing resources for movies, actors, directors, genres, awards, nominations, platforms, and reviews.

## Context

This API serves as the backend for the [Arte7 Frontend](https://github.com/fedemelo/arte7-front).

It was originally developed as a group project for the _Desarrollo de Software en Equipo_ course at the University of the Andes.

## Local Setup

Java 11 and Maven are required to run the application locally. The command to do so is:

```bash
mvn spring-boot:run
```

Run tests with:

```bash
mvn test
```

## Code Quality

Continuous code analysis is performed on this project by [SonarCloud](https://sonarcloud.io/project/overview?id=fedemelo_arte7-back).

## Tech Stack

- Java 11
- Spring Boot
- Maven
- H2 Database (for development and testing)
- Lombok (for reducing boilerplate code)
- JaCoCo (for test coverage analysis)
- SonarCloud (for code quality analysis)