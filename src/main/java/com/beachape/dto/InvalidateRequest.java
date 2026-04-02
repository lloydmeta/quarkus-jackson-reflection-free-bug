package com.beachape.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Set;

/// Request using standard `Set` - control case that should work.
public record InvalidateRequest(
        @JsonProperty("tokens") Set<Token> tokens) implements ApiModel {}
