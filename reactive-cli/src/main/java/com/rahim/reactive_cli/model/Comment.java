package com.rahim.reactive_cli.model;

public record Comment(
    Long id,
    Long postId,
    String name,
    String email,
    String body
) {}
