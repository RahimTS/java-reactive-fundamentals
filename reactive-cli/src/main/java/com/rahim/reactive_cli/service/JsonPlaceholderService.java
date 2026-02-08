package com.rahim.reactive_cli.service;

import com.rahim.reactive_cli.model.Comment;
import com.rahim.reactive_cli.model.Post;
import com.rahim.reactive_cli.model.User;
import com.rahim.reactive_cli.model.UserWithPosts;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class JsonPlaceholderService {

    private final WebClient webClient;

    public JsonPlaceholderService() {
        this.webClient = WebClient.builder()
            .baseUrl("https://jsonplaceholder.typicode.com")
            .build();
    }

    public Flux<User> getAllUsers() {
        return webClient.get()
            .uri("/users")
            .retrieve()
            .bodyToFlux(User.class)
            .doOnNext(user -> System.out.println("fetched users" + user.name()));
    }

    public Mono<User> getUserById(Long id) {
        return webClient.get()
            .uri("/users/{id}", id)
            .retrieve()
            .bodyToMono(User.class);
    }

    public Flux<Post> getPostsByUser(Long userId) {
        return webClient.get()
            .uri("/posts?userId={userId}", userId)
            .retrieve()
            .bodyToFlux(Post.class);
    }

    public Flux<Comment> getCommentsByPost(Long postId) {
        return webClient.get()
            .uri("/comments?postId={postId}", postId)
            .retrieve()
            .bodyToFlux(Comment.class);
    }

    public Mono<UserWithPosts> getUserWithPosts(Long userId) {
        var userMono = getUserById(userId);
        var postsFlux = getPostsByUser(userId);

        return userMono.zipWith(postsFlux.collectList())
            .map(tuple -> new UserWithPosts(tuple.getT1(), tuple.getT2()));
    }
}
