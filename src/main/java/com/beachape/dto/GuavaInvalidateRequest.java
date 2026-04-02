package com.beachape.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;

/// Request using Guava `ImmutableSet` - triggers Bug A (generic type params lost).
public record GuavaInvalidateRequest(
        @JsonProperty("tokens") ImmutableSet<Token> tokens) implements ApiModel {}
