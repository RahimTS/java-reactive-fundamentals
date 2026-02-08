package com.rahim.reactive_cli;

import com.rahim.reactive_cli.service.JsonPlaceholderService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Scanner;

@SpringBootApplication
public class ReactiveCliApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReactiveCliApplication.class, args);
    }

    @Bean
    public CommandLineRunner run(JsonPlaceholderService service) {
        return args -> {
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("\n=== Reactive CLI ===");
                System.out.println("1. List all users");
                System.out.println("2. Get user by ID");
                System.out.println("3. Get user's posts");
                System.out.println("4. Search users by name");
                System.out.println("5. Get most active users");
                System.out.println("6. Exit");
                System.out.print("Choose: ");

                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1 -> listAllUsers(service);
                    case 2 -> getUserById(service, scanner);
                    case 3 -> getUserPosts(service, scanner);
                    case 4 -> searchUsers(service, scanner);
                    case 5 -> getMostActiveUsers(service);
                    case 6 -> {
                        System.out.println("Goodbye!");
                        scanner.close();
                        return;
                    }
                    default -> System.out.println("Invalid choice!");
                }

            }
        };
    }

    private void listAllUsers(JsonPlaceholderService service) {
        System.out.println("\n=== All Users ===");
        service.getAllUsers()
                .doOnNext(user -> System.out.printf("%d. %s (%s)%n", user.id(), user.name(), user.email()))
                .doOnError(error -> System.err.println("Error: " + error.getMessage()))
                .doOnComplete(() -> System.out.println("\nCompleted!"))
                .blockLast();
    }

    private void getUserById(JsonPlaceholderService service, Scanner scanner) {
        System.out.print("Enter user ID: ");
        Long userId = scanner.nextLong();

        service.getUserById(userId)
                .doOnNext(user -> System.out.printf("\n%d. %s (%s) - %s%n",
                        user.id(), user.name(), user.username(), user.email()))
                .doOnError(error -> System.err.println("Error: " + error.getMessage()))
                .block();
    }

    private void getUserPosts(JsonPlaceholderService service, Scanner scanner) {
        System.out.print("Enter user ID: ");
        Long userId = scanner.nextLong();

        try {
            var userWithPosts = service.getUserWithPosts(userId).block();
            if (userWithPosts != null) {
                System.out.println("\n=== Posts by " + userWithPosts.user().name() + " ===");
                userWithPosts.posts().forEach(post
                        -> System.out.printf("%d. %s%n", post.id(), post.title())
                );
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void searchUsers(JsonPlaceholderService service, Scanner scanner) {
        System.out.print("Enter name to search: ");
        String searchTerm = scanner.nextLine().toLowerCase();

        System.out.println("\n=== Search Results ===");
        service.getAllUsers()
                .filter(user -> user.name().toLowerCase().contains(searchTerm))
                .doOnNext(user -> System.out.printf("%d. %s%n", user.id(), user.name()))
                .doOnError(error -> System.err.println("Error: " + error.getMessage()))
                .doOnComplete(() -> System.out.println("\nSearch completed!"))
                .blockLast();
    }

    private void getMostActiveUsers(JsonPlaceholderService service) {
        System.out.println("\n=== Most Active Users (by posts) ===");

        service.getAllUsers()
                .flatMap(user
                        -> service.getPostsByUser(user.id())
                        .count()
                        .map(count -> new UserActivity(user.name(), count))
                )
                .sort((a, b) -> Long.compare(b.postCount(), a.postCount()))
                .take(5)
                .doOnNext(activity -> System.out.printf("%s: %d posts%n",
                        activity.username(), activity.postCount()))
                .doOnError(error -> System.err.println("Error: " + error.getMessage()))
                .doOnComplete(() -> System.out.println("\nCompleted!"))
                .blockLast();
    }

    record UserActivity(String username, Long postCount) {

    }
}
