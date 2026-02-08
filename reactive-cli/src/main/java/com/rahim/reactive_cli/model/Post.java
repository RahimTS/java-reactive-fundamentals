package com.rahim.reactive_cli.model;

public record Post(
    Long id,
    Long userId,
    String title,
    String body
) {}
