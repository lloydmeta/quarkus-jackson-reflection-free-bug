package com.beachape.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Optional;

/// Tests the `Nulls.AS_EMPTY` config override: explicit JSON `null` should deserialise
/// as `Optional.empty()`, not Java `null`.
public record OptionalStringRequest(@JsonProperty("label") Optional<String> label) implements ApiModel {}
