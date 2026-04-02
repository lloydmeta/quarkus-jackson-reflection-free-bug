package com.beachape.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.LinkedHashSet;

/// Request using `LinkedHashSet` - a standard JDK collection not in the hardcoded list.
public record LinkedHashSetInvalidateRequest(
        @JsonProperty("tokens") LinkedHashSet<Token> tokens) implements ApiModel {}
