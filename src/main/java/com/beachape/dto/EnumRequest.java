package com.beachape.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/// Sanity check that enums with `@JsonValue`/`@JsonCreator` work via `readTreeAsValue`.
public record EnumRequest(@JsonProperty("status") Status status) implements ApiModel {}
