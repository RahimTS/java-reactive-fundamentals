
import java.time.Duration;
import java.util.Arrays;

import org.reactivestreams.Subscription;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class FluxBasics {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== FLUX EXAMPLES===");

        // 1. Flux from values
        System.out.println("\n1. Flux from values");
        Flux<String> namesFlux = Flux.just("Alice", "Bob", "Charlie");
        namesFlux.subscribe(name -> System.out.println(" " + name));

        // 2. Flux from List
        System.out.println("\n2. Flux from List");
        var numbers = Arrays.asList(1, 2, 3, 4, 5);
        var numberFlux = Flux.fromIterable(numbers);
        numberFlux.subscribe(num -> System.out.println(" " + num));

        // 3. Flux range
        System.out.println("\n3. Flux range (1 to 10):");
        Flux<Integer> rangeFlux = Flux.range(1, 10);
        rangeFlux.subscribe(num -> System.out.println(" " + num));
        System.out.println();

        // 4. Flux interval (infinite stream)
        System.out.println("\n Flux interval (emit every 500ms, take 5):");
        Flux.interval(Duration.ofMillis(500))
                .take(5)
                .subscribe(tick -> System.out.println(" Tick " + tick));

        Thread.sleep(2000);

        // 5. Empty Flux
        System.out.println("\n5. Empty Flux");
        var emptyFlux = Flux.empty();
        emptyFlux.subscribe(
                value -> System.out.println("Value " + value),
                error -> System.out.println("Error " + error),
                () -> System.out.println("Completed with no values")
        );

        // 6. Flux with error
        System.out.println("\n6. Flux with error");
        Flux<Integer> errorFlux = Flux.range(1, 5)
                .map(i -> {
                    if (i == 3) {
                        throw new RuntimeException("Error at 3!");
                    }
                    return i;
                });
        errorFlux.subscribe(
                value -> System.out.println("  Value: " + value),
                error -> System.out.println("  Caught: " + error.getMessage())
        );

        // 7. Flux generate (programmatic creation)
        System.out.println("\n7. Flux generate (fibonacci):");
        Flux<Integer> fibonacci = Flux.generate(
                () -> new int[]{0, 1},
                (state, sink) -> {
                    sink.next(state[0]);
                    return new int[]{state[1], state[0] + state[1]};
                }
        );
        fibonacci.take(10).subscribe(num -> System.out.println(num + " "));
        System.out.println();

        // 8. Flux create (async emissions)
        System.out.println("\n8. Flux create (async):");
        Flux<String> asyncFlux = Flux.create(sink -> {
            for (int i = 0; i < 5; i++) {
                sink.next("Item" + i);
            }
            sink.complete();
        });
        asyncFlux.subscribe(item -> System.out.println(" " + item));

        Thread.sleep(2000);

        // 5. Backpressure (request n items)
        System.out.println("\n5. Backpressure (request 3 at a time):");
        Flux.range(1, 10).subscribe(new org.reactivestreams.Subscriber<Integer>() {
            private org.reactivestreams.Subscription subscription;
            private int count = 0;

            @Override
            public void onSubscribe(Subscription s) {
                this.subscription = s;
                s.request(3);
            }

            @Override
            public void onNext(Integer value) {
                System.out.println(" Value: " + value);
                count++;
                if (count % 3 == 0) {
                    subscription.request(3);
                }
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("  Error: " + t.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("  Completed!");
            }
        });

        // Clean up Reactor schedulers to avoid lingering threads
        Schedulers.shutdownNow();
    }
}
