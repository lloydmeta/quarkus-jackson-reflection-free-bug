package com.beachape.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Detail(@JsonProperty("id") String id, @JsonProperty("value") String value)
    implements ApiModel {}
