package com.beachape.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

public record PersonWithAddress(@JsonProperty("name") String name, @JsonUnwrapped Address address)
    implements ApiModel {}
