package com.beachape.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

/// Request using Guava `ImmutableList` - triggers Bug A (generic type params lost).
public record GuavaBatchRequest(
        @JsonProperty("items") ImmutableList<Item> items) implements ApiModel {}
