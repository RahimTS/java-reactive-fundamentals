
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.Duration;

public class OperatorsGuide {
    public static void main(String[] args) throws InterruptedException {
        // === TRANSFORMATION OPERATORS ===

        // 1. map - Transform each element
        System.out.println("1. map (x -> x * 2):");
        Flux.range(1, 5)
            .map(x -> x * 2)
            .subscribe(val -> System.out.println(val + " "));
        System.out.println("\n");

         // 2. flatMap - Transform to Publisher (async)
        System.out.println("2. flatMap (async transformation):");
        Flux.range(1, 3)
            .flatMap(i -> Mono.just(i * 100).delayElement(Duration.ofMillis(100)))
            .subscribe(val -> System.out.println(" " + val));
        Thread.sleep(500);
        System.out.println();

        // 3. flatMapSequential - Preserve order
        System.out.println("3. flatMapSequential (ordered):");
                Flux.range(1, 3)
            .flatMapSequential(i -> Mono.just(i * 10).delayElement(Duration.ofMillis(100)))
            .subscribe(val -> System.out.println("  " + val));
        Thread.sleep(500);
        System.out.println();

        // 4. concatMap - Process one at a time (ordered)
        System.out.println("4. concatMap (sequential):");
        Flux.range(1, 3)
            .concatMap(i -> Mono.just(i * 10).delayElement(Duration.ofMillis(100)))
            .subscribe(val -> System.out.println("  " + val));
        Thread.sleep(500);
        System.out.println();

        // === FILTERING OPERATORS ===

        // 5. filter - Keep only matching elements
        System.out.println("5. filter (even numbers):");
        Flux.range(1, 10)
            .filter(x -> x % 2 == 0)
            .subscribe(val -> System.out.println(val + " "));
        System.out.println("\n");

        // 6. take - Take first N elements
        System.out.println("6. take (first 3):");
        Flux.range(1, 10)
            .take(3)
            .subscribe(val -> System.out.print(val + " "));
        System.out.println("\n");
        
        // 7. skip - Skip first N elements
        System.out.println("7. skip (skip first 5):");
        Flux.range(1, 10)
            .skip(5)
            .subscribe(val -> System.out.print(val + " "));
        System.out.println("\n");
        
        // 8. distinct - Remove duplicates
        System.out.println("8. distinct:");
        Flux.just(1, 2, 2, 3, 3, 3, 4)
            .distinct()
            .subscribe(val -> System.out.print(val + " "));
        System.out.println("\n");

        // === COMBINING OPERATORS ===

        // 9. zip - Combine two streams pairwise
        System.out.println("9. zip:");
        var names = Flux.just("Alice", "Bob", "Charlie");
        var ages = Flux.just(25, 35, 45);
        Flux.zip(names, ages)
            .subscribe(tuple -> System.out.println(" " + tuple.getT1() + " is " + tuple.getT2()));
        System.out.println();

        // 10. merge - Interleave streams
        System.out.println("10. merge:");
        Flux<Integer> flux1 = Flux.just(1, 3, 5);
        Flux<Integer> flux2 = Flux.just(2, 4, 6);
        Flux.merge(flux1, flux2)
            .subscribe(val -> System.out.print(val + " "));
        System.out.println("\n");
        
        // 11. concat - Append streams (in order)
        System.out.println("11. concat:");
        Flux.concat(flux1, flux2)
            .subscribe(val -> System.out.print(val + " "));
        System.out.println("\n");

        // === ERROR HANDLING ===

        // 12. onErrorReturn - Fallback value
        System.out.println("12. onErrorReturn:");
        Flux.range(1, 5)
            .map(i -> {
                if (i == 3) throw new RuntimeException("Error!");
                return i;
            })
            .onErrorReturn(-1)
            .subscribe(val -> System.out.print(val + " "));
        System.out.println("\n");
        
        // 13. onErrorResume - Fallback Publisher
        System.out.println("13. onErrorResume:");
        Flux.range(1, 5)
            .map(i -> {
                if (i == 3) throw new RuntimeException("Error!");
                return i;
            })
            .onErrorResume(e -> Flux.just(100, 200))
            .subscribe(val -> System.out.print(val + " "));
        System.out.println("\n");
        
        // 14. retry - Retry on error
        System.out.println("14. retry (3 times):");
        final int[] attempts = {0};
        Flux.just(1)
            .map(i -> {
                attempts[0]++;
                System.out.println("  Attempt: " + attempts[0]);
                if (attempts[0] < 3) throw new RuntimeException("Retry!");
                return i;
            })
            .retry(3)
            .subscribe(val -> System.out.println("  Success: " + val));
        System.out.println();
        
        // === UTILITY OPERATORS ===
        
        // 15. doOnNext - Side effect on each element
        System.out.println("15. doOnNext:");
        Flux.range(1, 3)
            .doOnNext(val -> System.out.println("  Processing: " + val))
            .map(x -> x * 2)
            .subscribe(val -> System.out.println("  Result: " + val));
        System.out.println();
        
        // 16. doOnComplete - Side effect on completion
        System.out.println("16. doOnComplete:");
        Flux.range(1, 3)
            .doOnComplete(() -> System.out.println("  Stream finished!"))
            .subscribe(val -> System.out.print(val + " "));
        System.out.println("\n");
        
        // 17. delayElements - Delay each emission
        System.out.println("17. delayElements (200ms):");
        Flux.range(1, 3)
            .delayElements(Duration.ofMillis(200))
            .subscribe(val -> System.out.println("  " + val));
        Thread.sleep(1000);
        System.out.println();
        
        // === AGGREGATION ===
        
        // 18. reduce - Combine all elements
        System.out.println("18. reduce (sum):");
        Flux.range(1, 5)
            .reduce(0, (acc, val) -> acc + val)
            .subscribe(sum -> System.out.println("  Sum: " + sum));
        System.out.println();
        
        // 19. collect - Collect to Collection
        System.out.println("19. collectList:");
        Flux.just("a", "b", "c")
            .collectList()
            .subscribe(list -> System.out.println("  List: " + list));
        System.out.println();
        
        // 20. count - Count elements
        System.out.println("20. count:");
        Flux.range(1, 100)
            .count()
            .subscribe(count -> System.out.println("  Count: " + count));
    }
}
