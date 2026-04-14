package com.beachape.dto;

/// No `@JsonProperty` annotations - relies on the ObjectMapper's `SnakeCaseStrategy` to
/// map `first_name` in JSON to `firstName` in Java.
public record MapperSnakeCaseRequest(String firstName) implements ApiModel {}
