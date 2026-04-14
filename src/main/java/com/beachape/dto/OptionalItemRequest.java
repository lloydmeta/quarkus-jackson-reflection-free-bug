package com.beachape.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Optional;

/// Tests that `Optional<Item>` preserves the generic type parameter during deserialisation.
public record OptionalItemRequest(@JsonProperty("item") Optional<Item> item) implements ApiModel {}
