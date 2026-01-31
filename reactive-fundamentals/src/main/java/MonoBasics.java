import java.time.Duration;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class MonoBasics {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== MONO EXAMPLES === ");

        // 1. Empty Mono
        System.out.println("\n1. Empty Mono");
        Mono<String> emptyMono = Mono.empty();
        emptyMono.subscribe(
            value -> System.out.println("Value: "+ value),
            error -> System.err.println("Error: "+ error),
            () -> System.out.println("Completed!")
        );
        
        // 2. Mono with value
        System.out.println("\n2. Mono with value");
        Mono<String> helloMono = Mono.just("Hello reactive");
        helloMono.subscribe(System.out::println);

        // 3. Mono from Callable (Lazy Evaluation)
        System.out.println("\n3. Mono from Callable");
        Mono<String> lazyMono = Mono.fromCallable(() -> {
            System.out.println(" Computing value ");
            Thread.sleep(1000);
            return "Computed after 1 second";
        });

        System.out.println(" Mono created (not executed yet)");
        lazyMono.subscribe(System.out::println);
        Thread.sleep(1500);

        // 4. Mono with error
        System.out.println("\n4. Mono with error");
        Mono<String> errorMono = Mono.error(new RuntimeException("Something went wrong"));
        errorMono.subscribe(
            value -> System.out.println("Value: " + value),
            error -> System.out.println("Error: "+ error)
        );
        
        // 5. Mono from supplier
        System.out.println("\n5. Mono from supplier");
        Mono<Integer> randomMono = Mono.fromSupplier(() -> {
            return (int)(Math.random() * 100);
        });
        randomMono.subscribe(val -> System.out.println("Random: "+ val));

        // 6. Mono.defer (create fresh Mono for each subscription)
        System.out.println("\n6. Mono.defer (fresh each time):");
        Mono<Long> deferredMono = Mono.defer(() -> Mono.just(System.currentTimeMillis()));
        deferredMono.subscribe(time -> System.out.println("Time 1: " + time));
        Thread.sleep(1000);
        deferredMono.subscribe(time -> System.out.println("Time 2: " + time));
        
        // 7. Delayed Mono
        System.out.println("\n7. Delayed Mono (2 seconds):");
        Mono.delay(Duration.ofSeconds(2))
            .subscribe(val -> System.out.println("Delayed execution!"));
        
        Thread.sleep(3000);  // Wait for delay

        // Clean up Reactor schedulers to avoid lingering threads
        Schedulers.shutdownNow();
    }
}
