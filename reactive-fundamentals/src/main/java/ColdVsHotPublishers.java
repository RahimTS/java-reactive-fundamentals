import java.time.Duration;

import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

public class ColdVsHotPublishers {
    public static void main(String[] args) throws InterruptedException {
        // === COLD PUBLISHER ===
        // Each subscriber gets its own data stream from the beginning
        
        System.out.println("=== COLD PUBLISHER ===");
        Flux<Long> coldFlux = Flux.interval(Duration.ofSeconds(1)).take(5);
        
        System.out.println("Subscriber 1 connects:");
        coldFlux.subscribe(val -> System.out.println("  Sub1: " + val));
        
        Thread.sleep(3000);  // Wait 3 seconds
        
        System.out.println("Subscriber 2 connects (starts from 0):");
        coldFlux.subscribe(val -> System.out.println("  Sub2: " + val));
        
        Thread.sleep(6000);
        
        // === HOT PUBLISHER ===
        // All subscribers share the same data stream
        
        System.out.println("\n=== HOT PUBLISHER ===");
        ConnectableFlux<Long> hotFlux = Flux.interval(Duration.ofSeconds(1))
            .take(5)
            .publish();  // Convert to hot
        
        System.out.println("Subscriber 1 connects:");
        hotFlux.subscribe(val -> System.out.println("  Sub1: " + val));
        
        System.out.println("Starting hot stream...");
        hotFlux.connect();  // Start emitting
        
        Thread.sleep(3000);  // Wait 3 seconds
        
        System.out.println("Subscriber 2 connects (joins midstream):");
        hotFlux.subscribe(val -> System.out.println("  Sub2: " + val));
        
        Thread.sleep(3000);
    }
}
