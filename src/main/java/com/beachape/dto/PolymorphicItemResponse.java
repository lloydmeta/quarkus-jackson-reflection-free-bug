package com.beachape.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PolymorphicItemResponse(@JsonProperty("item") Item item) implements ApiModel {}
