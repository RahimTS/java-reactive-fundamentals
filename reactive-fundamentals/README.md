# Reactive Fundamentals

A hands-on Java project demonstrating core reactive programming concepts using **Project Reactor**. This is Part 1 of the Reactive Systems learning path, focusing on foundational reactive patterns and operators.

## Prerequisites

- Java 17+
- Maven 3.6+

## Project Structure

```
reactive-fundamentals/
├── pom.xml
└── src/main/java/
    ├── MonoBasics.java         # Single-element publishers
    ├── FluxBasics.java         # Multi-element publishers & backpressure
    ├── SubscriptionBasics.java # Subscription lifecycle management
    ├── ColdVsHotPublishers.java# Publisher behavior patterns
    └── OperatorsGuide.java     # Essential reactive operators
```

## Concepts Covered

### 1. Mono - Single Value Publishers
- Empty, value-based, and error Monos
- Lazy evaluation with `fromCallable()` and `fromSupplier()`
- Deferred creation for fresh instances per subscription
- Delayed emissions

### 2. Flux - Multi-Value Publishers
- Creating streams from values, collections, and ranges
- Infinite streams with `interval()`
- Programmatic generation with `generate()` and `create()`
- **Backpressure** - controlling data flow between publisher and subscriber

### 3. Subscription Management
- Subscribing with value, error, and completion handlers
- Disposing subscriptions to cancel and clean up resources

### 4. Cold vs Hot Publishers
- **Cold**: Each subscriber gets independent data from the start
- **Hot**: Subscribers share the stream and join midway
- Converting cold to hot with `publish()` and `connect()`

### 5. Operators
| Category | Operators |
|----------|-----------|
| Transform | `map`, `flatMap`, `flatMapSequential`, `concatMap` |
| Filter | `filter`, `take`, `skip`, `distinct` |
| Combine | `zip`, `merge`, `concat` |
| Error | `onErrorReturn`, `onErrorResume`, `retry` |
| Utility | `doOnNext`, `doOnComplete`, `delayElements` |
| Aggregate | `reduce`, `collectList`, `count` |

## Running the Examples

Each file contains a `main()` method with executable demonstrations:

```bash
# Build the project
mvn compile

# Run individual examples
mvn exec:java -Dexec.mainClass="MonoBasics"
mvn exec:java -Dexec.mainClass="FluxBasics"
mvn exec:java -Dexec.mainClass="SubscriptionBasics"
mvn exec:java -Dexec.mainClass="ColdVsHotPublishers"
mvn exec:java -Dexec.mainClass="OperatorsGuide"
```

Or run directly from your IDE by executing the `main()` method in each class.

## Dependencies

| Dependency | Purpose |
|------------|---------|
| reactor-core | Core reactive library from Project Reactor |
| reactor-test | Testing utilities for reactive streams |
| lombok | Boilerplate reduction |

## Related Documentation

See [understanding-reactive.md](understanding-reactive.md) for theoretical background on:
- Why reactive programming exists (the problem with blocking I/O)
- Blocking vs non-blocking I/O comparison
- The Reactive Streams API and its 4 interfaces
- Project Reactor's Mono and Flux types
- Key operators (transformation, combination, error handling)
- Backpressure strategies
- The Reactive Manifesto's four pillars
- How to read marble diagrams
