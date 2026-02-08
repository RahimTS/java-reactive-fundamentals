# Reactive CLI

A command-line application built with **Spring Boot WebFlux** that consumes the [JSONPlaceholder](https://jsonplaceholder.typicode.com) REST API using reactive, non-blocking HTTP calls.

## Features

| # | Command | Description |
|---|---------|-------------|
| 1 | List all users | Fetches and displays all users |
| 2 | Get user by ID | Fetches a single user by their ID |
| 3 | Get user's posts | Fetches a user along with all their posts |
| 4 | Search users by name | Filters users by a name substring |
| 5 | Get most active users | Ranks the top 5 users by post count |
| 6 | Exit | Exits the application |

## Tech Stack

- **Java 25**
- **Spring Boot 4.0.2**
- **Spring WebFlux** (WebClient for non-blocking HTTP)
- **Project Reactor** (Mono / Flux reactive streams)

## Project Structure

```
src/main/java/com/rahim/reactive_cli/
├── ReactiveCliApplication.java        # CLI entry point and menu loop
├── model/
│   ├── User.java                      # User record
│   ├── Post.java                      # Post record
│   ├── Comment.java                   # Comment record
│   └── UserWithPosts.java             # Composite record (User + Posts)
└── service/
    └── JsonPlaceholderService.java     # Reactive API client
```

## Getting Started

### Prerequisites

- JDK 25+
- Maven 3.8+

### Run

```bash
mvn spring-boot:run
```

### Build

```bash
mvn clean package
java -jar target/reactive-cli-0.0.1-SNAPSHOT.jar
```
