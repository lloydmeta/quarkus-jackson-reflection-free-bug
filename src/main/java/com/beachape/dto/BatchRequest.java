package com.beachape.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/// Request using standard `List` - control case that should work.
public record BatchRequest(
        @JsonProperty("items") List<Item> items) implements ApiModel {}
