package com.rahim.reactive_cli.model;

import java.util.List;

public record UserWithPosts(User user, List<Post> posts) {}
