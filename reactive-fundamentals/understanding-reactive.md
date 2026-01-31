# The Problem with Traditional (Blocking) Code

## Blocking I/O Example:

```java
// Traditional Spring MVC (Blocking)
@GetMapping("/user/{id}")
public User getUser(@PathVariable Long id) {
    User user = userRepository.findById(id);        // Thread waits here
    Orders orders = orderService.getOrders(userId); // Thread waits here
    Payment payment = paymentService.get(userId);   // Thread waits here
    return user;
}
```

**What happens:**

- Thread makes DB call → BLOCKED waiting for response
- Thread makes HTTP call → BLOCKED waiting for response
- Each request needs its own thread
- 1000 concurrent users = 1000 threads
- Threads are expensive (1-2 MB each)

**Math:**

- Tomcat default: 200 threads
- 201st user? Queued/Rejected

---

# The Reactive Solution

## Non-Blocking I/O:

```java
// Reactive (Non-blocking)
@GetMapping("/user/{id}")
public Mono<User> getUser(@PathVariable Long id) {
    return userRepository.findById(id)              // Returns immediately
        .flatMap(user -> orderService.getOrders(user.getId())
            .flatMap(orders -> paymentService.get(user.getId())
                .map(payment -> /* combine results */)));
}
```

**What happens:**

- Thread submits DB query → Moves to next request
- When DB responds → Event triggers callback
- Same thread handles 1000s of requests
- 10,000 concurrent users? No problem!

**When Reactive Shines:**

- High I/O workloads (many DB/HTTP calls)
- Microservices with lots of service-to-service calls
- Real-time data streams (chat, notifications, stock tickers)
- Limited resources (serverless, edge computing)

**When to AVOID Reactive:**

- CPU-intensive tasks (image processing, encryption)
- Simple CRUD with low traffic
- Team unfamiliar with reactive (learning curve)

---

# Part 2: Core Concepts - The Building Blocks

## 1. Reactive Streams API

**4 Interfaces:**

```java
Publisher<T>   // Emits data
Subscriber<T>  // Consumes data
Subscription   // Controls flow
Processor<T,R> // Both publisher and subscriber
```

**Flow:**

```
Publisher ──(subscribe)──> Subscriber
    │                           │
    │←────(request n items)─────│
    │                           │
    │──────(onNext: item)──────>│
    │──────(onNext: item)──────>│
    │──────(onComplete)────────>│
```

## 2. Project Reactor (Spring's Implementation)

**Two Main Types:**

### Mono<T> - 0 or 1 element

```java
Mono<User> user = userRepository.findById(1L);
// Like: Optional<User> or CompletableFuture<User>
```

### Flux<T> - 0 to N elements

```java
Flux<User> users = userRepository.findAll();
// Like: List<User> or Stream<User>
```

## 3. Key Operators

### Transformation:

```java
// map - Transform each element
Flux<String> names = userFlux.map(user -> user.getName());

// flatMap - Transform to another Publisher (async operation)
Flux<Order> orders = userFlux.flatMap(user -> orderService.findByUserId(user.getId()));

// filter - Keep only matching elements
Flux<User> adults = userFlux.filter(user -> user.getAge() >= 18);
```

### Combination:

```java
// zip - Combine two streams
Flux<Tuple2<User, Order>> combined = Flux.zip(userFlux, orderFlux);

// merge - Interleave multiple streams
Flux<Event> allEvents = Flux.merge(stream1, stream2, stream3);
```

### Error Handling:

```java
// onErrorReturn - Fallback value
Mono<User> user = userRepo.findById(1L)
    .onErrorReturn(new User("Guest"));

// onErrorResume - Fallback Publisher
Mono<User> user = userRepo.findById(1L)
    .onErrorResume(e -> userRepo.findByUsername("default"));

// retry - Try again
Mono<User> user = userRepo.findById(1L)
    .retry(3);
```

---

## 4. Backpressure

**The Problem:**

```
Fast Publisher ──(1000 items/sec)──> Slow Subscriber (10 items/sec)
// Result: Memory overflow!
```

**The Solution:**

```java
Flux.range(1, 1000)
    .onBackpressureBuffer(100)  // Buffer 100, drop rest
    .onBackpressureDrop()        // Drop excess items
    .onBackpressureLatest()      // Keep only latest
```

---

# The Reactive Manifesto

## Four Pillars:

```
┌─────────────────────────────────────────┐
│         REACTIVE SYSTEMS                │
├─────────────────────────────────────────┤
│                                         │
│  1. RESPONSIVE                          │
│     → Fast, consistent response times   │
│     → Quick problem detection           │
│                                         │
│  2. RESILIENT                           │
│     → Stay responsive during failures   │
│     → Isolation, replication, delegation│
│                                         │
│  3. ELASTIC                             │
│     → Stay responsive under load        │
│     → Scale up/down based on demand     │
│                                         │
│  4. MESSAGE-DRIVEN                      │
│     → Async message passing             │
│     → Loose coupling, backpressure      │
│                                         │
└─────────────────────────────────────────┘
```

---

# Blocking vs Non-Blocking: Visual Comparison

## Blocking (Traditional)

```
Request 1: [====DB====][==HTTP==][=Process=] ✓ (3 seconds)
                                              Thread 1 occupied entire time

Request 2:                                    [====DB====][==HTTP==] ✓
                                              Thread 2 occupied entire time

Request 3:                                                           [====DB====] ✓
                                                                     Thread 3 occupied

With 3 threads: Can handle 3 concurrent requests
Request 4: ❌ QUEUED (no threads available)
```

## Non-Blocking (Reactive)

```
Request 1: [DB req]─────────────────[callback] ✓
Request 2:    [DB req]──────────[callback] ✓
Request 3:       [HTTP req]──[callback] ✓
Request 4:          [DB req]─────────[callback] ✓
                                              All use Thread 1!

With 1 thread: Can handle 100s of concurrent requests
Thread is never waiting, always working
```

---

# Marble Diagrams (How to Read Reactive Streams)

```
Timeline →

Flux<Integer>:  ──1──2──3──4──5──|
                  │  │  │  │  │  └─ Complete
                  └──┴──┴──┴──┴──── Values emitted over time

map(x -> x * 2): ──2──4──6──8──10──|

filter(x > 5):   ──────────6──8──10──|

Error stream:    ──1──2──3──X
                           └─ Error

Never complete:  ──1──2──3──4──5──6──7──... (infinite)
```

**Common Symbols:**

| Symbol | Meaning |
|--------|---------|
| `──` | Timeline |
| `1,2,3` | Values |
| `\|` | Completion |
| `X` | Error |
| `...` | Continues |
