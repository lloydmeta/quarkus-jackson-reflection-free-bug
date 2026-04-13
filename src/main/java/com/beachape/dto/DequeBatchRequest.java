package com.beachape.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Deque;

/// Request using `Deque` - a JDK interface. The generated deserialiser falls back to
/// `ArrayList` (which doesn't implement `Deque`), causing a ClassCastException.
public record DequeBatchRequest(
        @JsonProperty("items") Deque<Item> items) implements ApiModel {}
