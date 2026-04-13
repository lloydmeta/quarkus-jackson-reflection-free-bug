package com.beachape.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.SortedMap;

/// Request using `SortedMap` - a JDK interface. The generated deserialiser hardcodes
/// `HashMap` (which doesn't implement `SortedMap`), causing a ClassCastException.
public record SortedMapRequest(
        @JsonProperty("entries") SortedMap<String, Item> entries) implements ApiModel {}
