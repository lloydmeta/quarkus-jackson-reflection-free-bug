package com.beachape.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

/// Sanity check that `Instant` fields work via `readTreeAsValue(jsonNode, Instant.class)`
/// with `JavaTimeModule` registered.
public record InstantRequest(@JsonProperty("timestamp") Instant timestamp) implements ApiModel {}
