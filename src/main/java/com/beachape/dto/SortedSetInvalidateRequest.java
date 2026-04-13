package com.beachape.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.SortedSet;

/// Request using `SortedSet` - a JDK interface. The generated deserialiser falls back to
/// `HashSet` (which doesn't implement `SortedSet`), causing a ClassCastException.
public record SortedSetInvalidateRequest(
        @JsonProperty("tokens") SortedSet<Token> tokens) implements ApiModel {}
