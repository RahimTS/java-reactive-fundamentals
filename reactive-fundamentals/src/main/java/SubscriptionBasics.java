import java.time.Duration;

import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class SubscriptionBasics {
    public static void main(String[] args) throws InterruptedException {
        // 1. Basic subscribe (only consume values)
        System.out.println("1. Basic subscribe:");
        Flux.range(1, 3).subscribe(
            value -> System.out.println("  Value: " + value)
        );
        
        // 2. Subscribe with error handler
        System.out.println("\n2. With error handler:");
        Flux.range(1, 5)
            .map(i -> {
                if (i == 3) throw new RuntimeException("Error!");
                return i;
            })
            .subscribe(
                value -> System.out.println("  Value: " + value),
                error -> System.out.println("  Error: " + error.getMessage())
            );
        
        // 3. Subscribe with completion handler
        System.out.println("\n3. With completion handler:");
        Flux.range(1, 3).subscribe(
            value -> System.out.println("  Value: " + value),
            error -> System.err.println("  Error: " + error),
            () -> System.out.println("  Stream completed!")
        );
        
        // 4. Disposable (cancel subscription)
        System.out.println("\n4. Disposable (cancel):");
        Disposable subscription = Flux.interval(Duration.ofMillis(100))
            .subscribe(tick -> System.out.println("  Tick: " + tick));
        
        Thread.sleep(500);
        subscription.dispose();  // Cancel subscription
        System.out.println("  Subscription cancelled");
        
        Thread.sleep(500);  // No more ticks

        // Clean up Reactor schedulers to avoid lingering threads
        Schedulers.shutdownNow();
    }
}
